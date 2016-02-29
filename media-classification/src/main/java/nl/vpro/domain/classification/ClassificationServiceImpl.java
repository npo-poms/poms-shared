package nl.vpro.domain.classification;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class ClassificationServiceImpl extends AbstractClassificationServiceImpl {

    private final Instant startTime = Instant.now();
    private static Logger LOG = LoggerFactory.getLogger(ClassificationServiceImpl.class);

    private final URI[] resources;

    private long pollIntervalInMillis = 60000;

    public static ClassificationServiceImpl fromClassPath(String... url)  {
        URI[] uris = Arrays.stream(url)
            .map(ClassificationServiceImpl::uriFromClassPath)
            .filter(u -> u != null)
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
    }
    public ClassificationServiceImpl(String resources) throws MalformedURLException, URISyntaxException {
        this(getResources(resources));
    }

    public void setPollIntervalInMillis(long pollIntervalInMillis) {
        this.pollIntervalInMillis = pollIntervalInMillis;
    }

    private static URI[] getResources(String resources) throws URISyntaxException {
        final List<URI> result = new ArrayList<>();
        for (String r : resources.split("\\s*,\\s*")) {
            if (r.startsWith("classpath:")) {
                URI uri = uriFromClassPath(r.substring("classpath:".length() + 1));
                if (uri != null) {
                    result.add(uri);
                }
            } else {
                result.add(URI.create(r));
            }
        }
        return result.toArray(new URI[result.size()]);
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
                        LOG.warn(file + ":" + e.getMessage());
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
                LOG.error(e.getMessage(), e);
            }
        }
        return result;
    }

    private static URI uriFromClassPath(String resource) {
        URL url = ClassificationServiceImpl.class.getClassLoader().getResource(resource);
        try {
            return url == null ? null : url.toURI();
        } catch (URISyntaxException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private Instant lastModified(URI uri) throws MalformedURLException {
        try {
            return Instant.ofEpochMilli(new File(uri).lastModified());
        } catch (IllegalArgumentException ia) {
            LOG.debug("Could not get last modified from {}", uri);
            return startTime;
        }
    }

    private List<File> getDirectory(URI resource, boolean startWatchers) {
        File resourceFile = null;
        try {
            String protocol = resource.toURL().getProtocol().toLowerCase();
            String path = "file".equals(protocol) ? resource.getPath() : null;
            resourceFile = path == null ? null : new File(URLDecoder.decode(path, "UTF-8")); // e.g. on Jenkins.
        } catch (IOException e1) {

        }
        if (resourceFile != null) {


            if (resourceFile.isDirectory() || !resourceFile.exists()) {
                resourceFile.mkdirs();
                if (startWatchers) {
                    watch(resourceFile);
                    // show that we're watching...
                    File tempFile = new File(resourceFile, AbstractClassificationServiceImpl.class.getSimpleName() + ".watched");
                    if (tempFile.canWrite()) {
                        try {
                            tempFile.createNewFile();
                            tempFile.setLastModified(System.currentTimeMillis());
                            tempFile.deleteOnExit();
                        } catch (IOException ioe) {
                            LOG.warn(tempFile + ": " + ioe.getClass() + " " + ioe.getMessage());
                        }
                    }
                }

                List<File> result = new ArrayList<>();
                for (File file : resourceFile.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".xml");
                    }
                })) {
                    result.add(file);
                }
                return result;
            } else {
                // not a directory

            }
        }
        return null;
    }


    private void watch(final File directory) {
        synchronized (AbstractClassificationServiceImpl.class) {

            try {
                //watchOnADecentFileSystem(directory);
                pollingWatchDirectory(directory);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        LOG.debug("Watching " + directory);
    }


    private long lastCheck = -1;
    private void pollingWatchDirectory(final File directory) throws IOException {
        LOG.info("Watching " + directory + " (using polling, since NFS doesn't support more sane methods)");
        executorService.scheduleAtFixedRate((Runnable) () -> {
            if (directory.lastModified() > lastCheck) {
                LOG.info("Found change in {}", directory);
                lastCheck = directory.lastModified();
                List<InputSource> sources = getSources(false);
                if (sources != null) {
                    try {
                        ClassificationServiceImpl.this.terms = readTerms(sources);
                    } catch (ParserConfigurationException e) {
                        LOG.error(e.getMessage(), e);
                    }
                }
            } else {
                LOG.debug("No changes in {}", directory);
            }
        }, pollIntervalInMillis, pollIntervalInMillis, TimeUnit.MILLISECONDS);
    }
    private void watchOnADecentFileSystem(final File directory) throws IOException {
        final Path watchedPath = Paths.get(directory.getAbsolutePath());
        final WatchService watcher = watchedPath.getFileSystem().newWatchService();
        watchedPath.register(
            watcher,
            StandardWatchEventKinds.ENTRY_CREATE,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE
        );
        Callable<Void> callable = () -> {
            LOG.info("Watching " + directory);
            while (true) {
                try {
                    WatchKey key = watcher.take();
                    for (WatchEvent event : key.pollEvents()) {
                        if (String.valueOf(event.context()).endsWith(".xml")) {
                            LOG.info(String.valueOf(event.kind() + " " + event.context()));
                            List<InputSource> sources = getSources(false);
                            if (sources != null) {
                                ClassificationServiceImpl.this.terms = readTerms(sources);
                            }
                            break;
                        } else {
                            LOG.debug("Ignored " + String.valueOf(event.kind() + " " + event.context()));
                        }
                    }
                    key.reset();
                } catch (InterruptedException e) {
                    LOG.info("Interrupted watcher");
                    break;
                }
            }
            return null;
        };
        executorService.submit(callable);
    }

    @Override
    public String toString() {
        return super.toString() + " " + (resources == null ? "[unconfigured] " : String.valueOf(Arrays.asList(resources)));
    }
}
