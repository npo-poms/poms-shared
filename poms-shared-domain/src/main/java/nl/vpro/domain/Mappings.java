package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.StringUtils;
import org.meeuw.jaxbdocumentation.DocumentationAdder;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * Maintains a mapping between XML-namespaces and their XSD's, it therefore also is an {@link LSResourceResolver}
 * and it provides {@link #getUnmarshaller(boolean, String)}
 *
 * @author Michiel Meeuwissen
 * @since 5.4
 */
@Slf4j
public abstract class Mappings implements Function<String, File>, LSResourceResolver {

    protected final static Map<String, URI> KNOWN_LOCATIONS = new HashMap<>();

    protected static final long startTime = System.currentTimeMillis();

    protected static Path tempDir;

    protected final Map<String, Class[]> MAPPING = new LinkedHashMap<>();

    private final Map<String, URI> SYSTEM_MAPPING = new LinkedHashMap<>();

    private final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

    @Getter
    @Setter
    protected boolean generateDocumentation = false;

    private boolean inited = false;

    public static File getTempDir() {
        if (tempDir == null) {
            try {
                tempDir = Files.createTempDirectory("schemas");
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return tempDir.toFile();
    }

    public Collection<String> knownNamespaces() {
        init();
        return MAPPING.keySet();
    }

    public Map<String, URI> systemNamespaces() {
        init();
        return Collections.unmodifiableMap(SYSTEM_MAPPING);
    }

    public ThreadLocal<Unmarshaller> getUnmarshaller(boolean validate, String namespace) {
        init();
        return ThreadLocal.withInitial(() -> {
            try {
                Class[] classes = MAPPING.get(namespace);
                if (classes == null) {
                    throw new IllegalArgumentException("No mapping found for " + namespace);
                }
                Unmarshaller result = JAXBContext.newInstance(classes).createUnmarshaller();
                if (validate) {
                    File xsd = getFile(namespace);
                    if (xsd.exists()) {
                        Schema schema = SCHEMA_FACTORY.newSchema(xsd);
                        result.setSchema(schema);
                    } else {
                        log.warn("Not found for {}: {}", namespace, xsd);
                    }
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public File apply(String namespace) {
        if (generateDocumentation) {
            return getFileWithDocumentation(namespace);
        } else {
            return getFile(namespace);
        }
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        URL url = ResourceResolver.resolveToURL(namespaceURI);
        if (url != null) {
            return ResourceResolver.resolveNamespaceToLS(namespaceURI);
        }

        try {
            InputStream resource = new FileInputStream(apply(namespaceURI));
            LSInput lsinput = ResourceResolver.DOM.createLSInput();
            lsinput.setCharacterStream(new InputStreamReader(resource));
            return lsinput;
        } catch (FileNotFoundException fne) {
            throw new RuntimeException(fne);
        }
    }

    public File getFile(String namespace) {
        init();
        String fileName = namespace.substring("urn:vpro:".length()).replace(':', '_') + ".xsd";
        return new File(getTempDir(), fileName);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getFileWithDocumentation(String namespace) {
        File file = getFile(namespace);
        File fileWithDocumentation = new File(file.getParentFile(), "documented." + file.getName());
        if (fileWithDocumentation.exists() && fileWithDocumentation.lastModified() < file.lastModified()) {
            fileWithDocumentation.delete();
        }
        if (!fileWithDocumentation.exists()) {
            DocumentationAdder transformer = new DocumentationAdder(MAPPING.get(namespace));
            try {
                transformer.transform(new StreamSource(new FileInputStream(file)), new StreamResult(new FileOutputStream(fileWithDocumentation)));
                log.info("Generated {} with {}", fileWithDocumentation, transformer);
            } catch (FileNotFoundException | TransformerException e) {
                log.error(e.getMessage(), e);
                try {
                    Files.copy(Paths.get(file.toURI()), Paths.get(fileWithDocumentation.toURI()), REPLACE_EXISTING);
                } catch (IOException e1) {
                    throw new RuntimeException(e1);
                }
            }

        }
        return fileWithDocumentation;
    }

    protected void init() {
        if (!inited) {
            inited = true;
            SYSTEM_MAPPING.put(XMLConstants.XML_NS_URI, URI.create("https://www.w3.org/2009/01/xml.xsd"));
            KNOWN_LOCATIONS.putAll(SYSTEM_MAPPING);

            fillMappings();

            try {
                generateXSDs();
            } catch (JAXBException | IOException e) {
                log.error(e.getMessage(), e);
            }
            SCHEMA_FACTORY.setResourceResolver(new ResourceResolver());
        }
    }

    protected abstract void fillMappings();

    protected Class<?>[] getClasses() {
        Set<Class<?>> classes = new LinkedHashSet<>();
        for (Class<?>[] c : MAPPING.values()) {
            classes.addAll(Arrays.asList(c));
        }
        return new ArrayList<>(classes).toArray(new Class[classes.size()]);
    }

    protected void generateXSDs() throws IOException, JAXBException {
        Class<?>[] classes = getClasses();
        log.info("Generating xsds {} in {}", Arrays.asList(classes), getTempDir());
        JAXBContext context = JAXBContext.newInstance(classes);
        context.generateSchema(new SchemaOutputResolver() {
            @SuppressWarnings("ResultOfMethodCallIgnored")
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                if (KNOWN_LOCATIONS.containsKey(namespaceUri)) {
                    Result result = new DOMResult();
                    result.setSystemId(KNOWN_LOCATIONS.get(namespaceUri).toString());
                    return result;
                }
                File f;
                if (StringUtils.isEmpty(namespaceUri)) {
                    f = new File(getTempDir(), suggestedFileName);
                } else {
                    f = getFile(namespaceUri);
                }
                deleteIfOld(f);
                if (!f.exists()) {
                    f.getParentFile().mkdirs();
                    log.info("Creating {} -> {}", namespaceUri, f);

                    StreamResult result = new StreamResult(f);
                    result.setSystemId(f);

                    FileOutputStream fo = new FileOutputStream(f);
                    result.setOutputStream(fo);
                    return result;
                } else {
                    log.debug("{} -> {} Was already generated", namespaceUri, f);
                    return null;
                }

            }
        });
        log.info("Ready");


    }

    private ThreadLocal<Unmarshaller> getUnmarshaller(boolean validate, Class<?>... classes) {
        return ThreadLocal.withInitial(() -> {
            try {
                Unmarshaller result = JAXBContext.newInstance(classes).createUnmarshaller();
                if (validate) {
                    result.setSchema(getSchema(classes));
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Schema getSchema(Class<?>... classesToRead) throws JAXBException, IOException, SAXException {
        JAXBContext context = JAXBContext.newInstance(classesToRead);
        final List<DOMResult> result = new ArrayList<>();
        context.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) {
                DOMResult dom = new DOMResult();
                dom.setSystemId(namespaceUri);
                result.add(dom);
                return dom;
            }
        });
        return SCHEMA_FACTORY.newSchema(new DOMSource(result.get(0).getNode()));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void deleteIfOld(File file) {
        // last modified on fs only granalur to seconds.
        if (file.exists() && TimeUnit.SECONDS.convert(file.lastModified(), TimeUnit.MILLISECONDS) < TimeUnit.SECONDS.convert(startTime, TimeUnit.MILLISECONDS)) {
            log.info("Deleting {}, it is old {} < {}", file, file.lastModified(), startTime);
            file.delete();
        }
    }

    public static void reset() {
        KNOWN_LOCATIONS.clear();
        for (File f : getTempDir().listFiles()) {
            f.delete();
        }
    }
}
