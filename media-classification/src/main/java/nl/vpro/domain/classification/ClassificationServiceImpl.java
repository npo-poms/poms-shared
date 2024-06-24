package nl.vpro.domain.classification;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.xml.sax.InputSource;

import nl.vpro.util.DirectoryWatcher;

import static java.util.Objects.requireNonNull;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */

@Slf4j
public class ClassificationServiceImpl extends AbstractClassificationServiceImpl {

    private final Instant startTime = Instant.now();

    private final URI[] resources;

    private long pollIntervalInMillis = 60000;

    public static ClassificationServiceImpl fromClassPath(String... url)  {
        URI[] uris = Arrays.stream(url)
            .map(ClassificationServiceImpl::uriFromClassPath)
            .filter(Objects::nonNull)
            .toArray(URI[]::new);
        return new ClassificationServiceImpl(uris);
    }

    public static ClassificationServiceImpl fromFiles(File ... files) {
        URI[] uris = Arrays.stream(files)
            .map(File::toURI)
            .toArray(URI[]::new);
        return new ClassificationServiceImpl(uris);
    }

    public ClassificationServiceImpl(URI... resources) {
        this.resources = resources;
        log.info("Configured classification service with {}", Arrays.asList(this.resources));
    }

    public ClassificationServiceImpl(String resourcesAsString) {
        this(getResources(resourcesAsString));
    }

    public void setPollIntervalInMillis(long pollIntervalInMillis) {
        this.pollIntervalInMillis = pollIntervalInMillis;
    }

    private static URI[] getResources(String resources) {
        final List<URI> result = new ArrayList<>();
        for (String r : resources.split("\\s*,\\s*")) {
            if (r.startsWith("classpath:")) {
                URI uri = uriFromClassPath(r.substring("classpath:".length()));
                if (uri != null) {
                    result.add(uri);
                } else {
                    log.warn("No uri found for {}", r);
                }
            } else {
                result.add(URI.create(r));
            }
        }
        return result.toArray(new URI[0]);
    }

    @Override
    protected List<InputSource> getSources(boolean startWatchers) {
        List<InputSource> result = new ArrayList<>();
        for (URI resource : resources) {
            List<File> directory = getDirectory(resource, startWatchers);
            if (directory != null) {
                for (File file : directory) {
                    try {
                        InputSource inputStream = new InputSource(new FileInputStream(file));
                        inputStream.setSystemId(file.toURI().toString());
                        result.add(inputStream);
                        if (this.lastModified == null || file.lastModified() > this.lastModified.toEpochMilli()) {
                            this.lastModified = Instant.ofEpochMilli(file.lastModified());
                        }
                    } catch (FileNotFoundException e) {
                        log.warn(file + ":" + e.getMessage());
                    }
                }
                continue;
            }
            try {
                InputSource source = new InputSource(resource.toURL().openStream());
                source.setSystemId(resource.toString());
                result.add(source);
                Instant lastModified = lastModified(resource);
                if (this.lastModified == null || lastModified.isAfter(this.lastModified)) {
                    this.lastModified = lastModified;
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
        return result;
    }

    @Nullable
    private static URI uriFromClassPath(String resource) {
        URL url = ClassificationServiceImpl.class.getClassLoader().getResource(resource);
        try {
            if (url == null) {
                log.warn("No such resource {}", resource);
            }
            return url == null ? null : url.toURI();
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private Instant lastModified(URI uri) {
        try {
            return Instant.ofEpochMilli(new File(uri).lastModified());
        } catch (IllegalArgumentException ia) {
            log.debug("Could not get last modified from {}", uri);
            return startTime;
        }
    }

    @Nullable
    private List<File> getDirectory(URI resource, boolean startWatchers) {
        File resourceFile;
        try {
            String protocol = resource.toURL().getProtocol().toLowerCase();
            String path = "file".equals(protocol) ? resource.getPath() : null;
            resourceFile = path == null ? null : new File(URLDecoder.decode(path, StandardCharsets.UTF_8)); // e.g. on Jenkins.
        } catch (IOException ignored) {
            resourceFile = null;
        }
        if (resourceFile != null) {
            if (resourceFile.isDirectory() || !resourceFile.exists()) {
                if (! resourceFile.exists()) {
                    if (!resourceFile.mkdirs()) {
                        log.warn("Couldn't make {}", resourceFile);
                    } else {
                        log.info("Created {}", resourceFile);
                    }
                }
                if (startWatchers) {
                    watch(resourceFile, false);
                    // show that we're watching...
                    File tempFile = new File(resourceFile, AbstractClassificationServiceImpl.class.getSimpleName() + ".watched");
                    if (tempFile.canWrite()) {
                        try {
                            if (!tempFile.createNewFile()) {
                                log.warn("The temp file {} already existed", tempFile);
                            }
                            if (!tempFile.setLastModified(System.currentTimeMillis())) {
                                log.warn("Couldn't set last modified of  {}", tempFile);
                            }
                            tempFile.deleteOnExit();
                        } catch (IOException ioe) {
                            log.warn(tempFile + ": " + ioe.getClass() + " " + ioe.getMessage());
                        }
                    }
                } else {
                    log.debug("Not watching {}", resourceFile);
                }

                final List<File> result = new ArrayList<>();
                Collections.addAll(result, requireNonNull(resourceFile.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"))));
                return result;
            } else {
                log.debug("{} not a directory", resourceFile);
            }
        }
        return null;
    }


    private void watch(final File directory, boolean usePolling) {
        synchronized (AbstractClassificationServiceImpl.class) {
            if (usePolling) {
                pollingWatchDirectory(directory);
            } else {
                try {
                    watchOnADecentFileSystem(directory);
                } catch (IOException e) {
                    log.error(e.getClass() + " " + e.getMessage(), e);
                    pollingWatchDirectory(directory);
                }
            }
        }
        log.debug("Watching " + directory);
    }


    private long lastCheck = -1;
    private void pollingWatchDirectory(final File directory) {
        log.info("Watching " + directory + " (using polling, since NFS doesn't support more sane methods)");
        executorService.scheduleAtFixedRate(() -> {
            if (directory.lastModified() > lastCheck) {
                log.info("Found change in {}", directory);
                lastCheck = directory.lastModified();
                List<InputSource> sources = getSources(false);
                if (sources != null) {
                    try {
                        ClassificationServiceImpl.this.terms = readTerms(sources);
                    } catch (ParserConfigurationException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            } else {
                log.debug("No changes in {}", directory);
            }
        }, pollIntervalInMillis, pollIntervalInMillis, TimeUnit.MILLISECONDS);
    }

    private void watchOnADecentFileSystem(final File directory) throws IOException {

        final Path watchedPath = Paths.get(directory.getAbsolutePath());
        DirectoryWatcher watcher = DirectoryWatcher.builder()
            .directory(watchedPath)
            .pathConsumer((f) -> {
            log.info("Found change in {}", f);
            List<InputSource> sources = getSources(false);
            if (sources != null) {
                try {
                    ClassificationServiceImpl.this.terms = readTerms(sources);
                } catch (ParserConfigurationException e) {
                    log.error(e.getMessage(), e);
                }
            }
            }).filter((f) -> f.getFileName().toString().endsWith(".xml"))
            .build();
    }

    @Override
    public String toString() {
        return super.toString() + " " + (resources == null ? "[unconfigured] " : String.valueOf(Arrays.asList(resources)));
    }
}
