package nl.vpro.api.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.api.media.MediaSearchResult;
import nl.vpro.domain.api.media.MediaSearchResults;
import nl.vpro.domain.api.media.RedirectList;
import nl.vpro.domain.api.media.ScheduleForm;
import nl.vpro.domain.api.page.PageForm;
import nl.vpro.domain.api.page.PageSearchResult;
import nl.vpro.domain.api.page.PageSearchResults;
import nl.vpro.domain.api.profile.Profile;
import nl.vpro.domain.api.subtitles.SubtitlesForm;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.support.ResourceResolver;
import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.update.PageUpdate;
import nl.vpro.domain.secondscreen.Screen;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesType;

import static nl.vpro.domain.Xmlns.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class Mappings {

    private final Map<String, Class[]> MAPPING = new LinkedHashMap<>();
    private final Map<String, URI> SYSTEM_MAPPING = new LinkedHashMap<>();

    private final SchemaFactory SCHEMA_FACTORY = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);


    String pomsLocation = null;

    @Inject
    public Mappings(@Named("${poms.location}") String pomsLocation) {
        this.pomsLocation = pomsLocation == null ? "https://poms.omroep.nl/" : pomsLocation;

        SYSTEM_MAPPING.put(XMLConstants.XML_NS_URI, URI.create("https://www.w3.org/2009/01/xml.xsd"));
        KNOWN_LOCATIONS.putAll(SYSTEM_MAPPING);

        fillMappings();
        Set<Class> classes = new LinkedHashSet<>();
        for (Class[] c : MAPPING.values()) {
            classes.addAll(Arrays.asList(c));
        }
        try {
            generateXSDs(new ArrayList<>(classes).toArray(new Class[classes.size()]));
        } catch (JAXBException | IOException e) {
            log.error(e.getMessage(), e);
        }
        SCHEMA_FACTORY.setResourceResolver(new ResourceResolver());

    }


    public Collection<String> knownNamespaces() {
        return MAPPING.keySet();
    }

    public Map<String, URI> systemNamespaces() {
        return Collections.unmodifiableMap(SYSTEM_MAPPING);
    }

    protected final static Map<String, URI> KNOWN_LOCATIONS = new HashMap<>();

    protected void fillMappings() {
        MAPPING.put(PROFILE_NAMESPACE, new Class[]{Profile.class});
        MAPPING.put(API_NAMESPACE, new Class[]{PageForm.class, ScheduleForm.class, SubtitlesForm.class, RedirectList.class, MediaSearchResults.class, PageSearchResults.class, MediaSearchResult.class, PageSearchResult.class});
        MAPPING.put(PAGE_NAMESPACE, new Class[]{Page.class});
        MAPPING.put(PAGEUPDATE_NAMESPACE, new Class[]{PageUpdate.class, ImageType.class});
        MAPPING.put(SECOND_SCREEN_NAMESPACE, new Class[]{Screen.class});
        MAPPING.put(Xmlns.MEDIA_CONSTRAINT_NAMESPACE, new Class[]{nl.vpro.domain.constraint.media.Filter.class});
        MAPPING.put(Xmlns.PAGE_CONSTRAINT_NAMESPACE, new Class[]{nl.vpro.domain.constraint.page.Filter.class});
        MAPPING.put(Xmlns.CONSTRAINT_NAMESPACE, new Class[]{nl.vpro.domain.constraint.Operator.class});
        MAPPING.put(Xmlns.MEDIA_SUBTITLES_NAMESPACE, new Class[]{Subtitles.class, SubtitlesType.class});

        Xmlns.fillLocationsAtPoms(KNOWN_LOCATIONS, pomsLocation);
    }

    protected void generateXSDs(Class... classes) throws IOException, JAXBException {
        log.info("Generating xsds in {}", Arrays.asList(classes), getTempDir());
        JAXBContext context = JAXBContext.newInstance(classes);
        context.generateSchema(new SchemaOutputResolver() {
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

    private Schema getSchema(Class<?>... classesToRead) throws JAXBException, IOException, SAXException {
        JAXBContext context = JAXBContext.newInstance(classesToRead);
        final List<DOMResult> result = new ArrayList<>();
        context.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                DOMResult dom = new DOMResult();
                dom.setSystemId(namespaceUri);
                result.add(dom);
                return dom;
            }
        });
        return SCHEMA_FACTORY.newSchema(new DOMSource(result.get(0).getNode()));
    }


    private ThreadLocal<Unmarshaller> getUnmarshaller(boolean validate, Class<?> ... classes) {
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
    };

    public ThreadLocal<Unmarshaller> getUnmarshaller(boolean validate, String namespace) {
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

    ;

    protected static final long startTime = System.currentTimeMillis();


    protected static Path tempDir;

    public static  File getTempDir() {
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

    public File getFile(String namespace)  {
        String fileName = namespace.substring("urn:vpro:".length()).replace(':', '_') + ".xsd";
        File file = new File(getTempDir(), fileName);
        // last modified on fs only granalur to seconds.
        if (file.exists() && TimeUnit.SECONDS.convert(file.lastModified(), TimeUnit.MILLISECONDS) < TimeUnit.SECONDS.convert(startTime, TimeUnit.MILLISECONDS)) {
            log.info("Deleting {}, it is old {} < {}", file, file.lastModified(), startTime);
            file.delete();
        }
        return file;
    }

}
