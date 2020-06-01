package nl.vpro.domain;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * @author Michiel Meeuwissen
 * @since 1.5
 */
@Slf4j
public final class Xmlns {

    private Xmlns() {
    }

    public static final String MEDIA_XSD_NAME = "vproMedia.xsd";
    public static final String MEDIA_NAMESPACE = "urn:vpro:media:2009";
    public static final URL    MEDIA_XSD       = getResource("/nl/vpro/domain/media/" + MEDIA_XSD_NAME);

    public static final String SHARED_XSD_NAME = "vproShared.xsd";
    public static final String SHARED_NAMESPACE = "urn:vpro:shared:2009";
    public static final URL    SHARED_XSD       = getResource("/nl/vpro/domain/media/" + SHARED_XSD_NAME);

    public static final String UPDATE_NAMESPACE = "urn:vpro:media:update:2009";
    public static final URL    UPDATE_XSD       = getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd");


    public static final String SEARCH_NAMESPACE = "urn:vpro:media:search:2012";
    public static final URL    SEARCH_XSD        = getResource("/nl/vpro/domain/media/search/vproMediaSearch.xsd");

    public static final String PAGEUPDATE_NAMESPACE = "urn:vpro:pages:update:2013";
    public static final URL PAGEUPDATE_XSD = getResource("/xsds/pages_update_2013.xsd");


    public static final String PAGE_NAMESPACE = "urn:vpro:pages:2013";
    public static final URL PAGE_XSD = getResource("/xsds/pages_2013.xsd");


    public static final String API_NAMESPACE = "urn:vpro:api:2013";
    public static final URL    API_XSD = getResource("/xsds/api_2013.xsd");


    public static final String PROFILE_NAMESPACE = "urn:vpro:api:profile:2013";

    public static final String MEDIA_CONSTRAINT_NAMESPACE = "urn:vpro:api:constraint:media:2013";
    public static final URL MEDIA_CONSTRAINT_XSD = getResource("/xsds/api_constraint_media_2013.xsd");


    public static final String MEDIA_SUBTITLES_NAMESPACE = "urn:vpro:media:subtitles:2009";

    public static final String PAGE_CONSTRAINT_NAMESPACE = "urn:vpro:api:constraint:page:2013";
    public static final URL    PAGE_CONSTRAINT_XSD = getResource("/xsds/api_constraint_page_2013.xsd");


    public static final String CONSTRAINT_NAMESPACE = "urn:vpro:api:constraint:2014";
    public static final URL CONSTRAINT_XSD = getResource("/xsds/api_constraint_2014.xsd");

    public static final URL ABSENT_XSD = getResource("/nl/vpro/domain/media/absentnamespace.xsd");


    public static final String MEDIA_WS_NAMESPACE = "urn:vpro:ws:media:2009";

    public static final String IMAGE_WS_NAMESPACE = "urn:vpro:ws:image:2009";


    public static final String IMAGE_NAMESPACE = "urn:vpro:image:2009";

    public static final String MEDIA_I18N_NAMESPACE = "urn:vpro:media:i18n:2017";

    public static final String NEP_NOTIFY_NAMESPACE = "urn:vpro:media:notify:2017";

    public static final String GTAA_NAMESPACE = "urn:vpro:gtaa:2017";

    public static final String ADMIN_NAMESPACE = "urn:vpro:media:admin:2017";


    public static final URL XML_XSD = getResource("/nl/vpro/domain/media/w3/xml.xsd");

    public static final Schema SCHEMA;

    static {
        try {
            SCHEMA = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(getStreamSources(
                    XML_XSD,
                    SHARED_XSD,
                    MEDIA_XSD,
                    UPDATE_XSD,
                    SEARCH_XSD,
                    PAGE_XSD,
                    PAGEUPDATE_XSD
                ));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }
    private static URL getResource(String resource) {
        URL url = Xmlns.class.getResource(resource);
        if (url == null) {
            log.info("No resource found for {}", resource);
        } else {
            log.info("Resource found {}", url);
        }
        return url;

    }
    private static StreamSource[] getStreamSources(URL... url) {
        List<StreamSource> result = new ArrayList<>();
        for (URL u : url) {
            if (u != null) {
                try {
                    result.add(new StreamSource(u.openStream()));
                } catch (IOException ioe) {
                    System.err.println(ioe.getMessage());

                }
            } else {
                log.error("Null url");
            }
        }
        return result.toArray(new StreamSource[0]);

    }


    public static void fillLocationsAtPoms(Map<String, URI> map, String pomsLocation) {
        if (pomsLocation == null) {
            throw new IllegalArgumentException();
        }
        //map.put(XMLConstants.XML_NS_URI, URI.create(pomsLocation + "schema/xml.xsd"));
        map.put(MEDIA_NAMESPACE, URI.create(pomsLocation + "schema/" + MEDIA_XSD_NAME));
        map.put(SHARED_NAMESPACE, URI.create(pomsLocation + "schema/" + SHARED_XSD_NAME));
        map.put(SEARCH_NAMESPACE, URI.create(pomsLocation + "schema/search/vproMediaSearch.xsd"));
        map.put(UPDATE_NAMESPACE, URI.create(pomsLocation + "schema/update/vproMediaUpdate.xsd"));

    }



    public static final NamespaceContext NAMESPACE_CONTEXT = new NamespaceContext() {
        private final Map<String, String> mapping = new HashMap<>();
        {
            mapping.put("update", UPDATE_NAMESPACE);
            mapping.put("u", UPDATE_NAMESPACE);
            mapping.put("media", MEDIA_NAMESPACE);
            mapping.put("s", SEARCH_NAMESPACE);
            mapping.put("shared", SHARED_NAMESPACE);
            mapping.put("pageupdate", PAGEUPDATE_NAMESPACE);
        }
        @Override
        public String getNamespaceURI(String prefix) {
            return mapping.get(prefix);

        }

        @Override
        public String getPrefix(String namespaceURI) {
            return mapping.entrySet().stream().filter(e -> e.getValue().equals(namespaceURI)).findFirst().map(Map.Entry::getKey).orElse(null);
        }

        @Override
        public Iterator<String> getPrefixes(String namespaceURI) {
            return mapping.entrySet().stream()
                .filter(e -> e.getValue().equals(namespaceURI))
                .map(Map.Entry::getKey).iterator();

        }
    };

}
