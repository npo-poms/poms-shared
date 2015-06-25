package nl.vpro.domain;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

/**
 * @author Michiel Meeuwissen
 * @since 1.5
 */
public final class Xmlns {
    public static final String MEDIA_NAMESPACE = "urn:vpro:media:2009";
    public static final URL    MEDIA_XSD       = Xmlns.class.getResource("/nl/vpro/domain/media/vproMedia.xsd");

    public static final String SHARED_NAMESPACE = "urn:vpro:shared:2009";
    public static final URL    SHARED_XSD       = Xmlns.class.getResource("/nl/vpro/domain/media/vproShared.xsd");

    public static final String UPDATE_NAMESPACE = "urn:vpro:media:update:2009";
    public static final URL    UPDATE_XSD       = Xmlns.class.getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd");

    public static final String SEARCH_NAMESPACE = "urn:vpro:media:search:2012";
    public static final URL    SEARCH_XSD        = Xmlns.class.getResource("/nl/vpro/domain/media/search/vproMediaSearch.xsd");

    public static final String SECOND_SCREEN_NAMESPACE = "urn:vpro:secondscreen:2015";
    public static final URL    SECOND_SCREEN_XSD        = Xmlns.class.getResource("/nl/vpro/domain/secondscreen/vproSecondScreen.xsd");

    public static final String PAGEUPDATE_NAMESPACE = "urn:vpro:pages:update:2013";

    public static final String PAGE_NAMESPACE = "urn:vpro:pages:2013";

    public static final String API_NAMESPACE = "urn:vpro:api:2013";

    public static final String PROFILE_NAMESPACE = "urn:vpro:api:profile:2013";

    public static final String MEDIA_CONSTRAINT_NAMESPACE = "urn:vpro:api:constraint:media:2013";

    public static final String MEDIA_SUBTITLES_NAMESPACE = "urn:vpro:media:subtitles:2009";

    public static final String PAGE_CONSTRAINT_NAMESPACE = "urn:vpro:api:constraint:page:2013";

    public static final String CONSTRAINT_NAMESPACE = "urn:vpro:api:constraint:2014";

    public static final String MEDIA_WS_NAMESPACE = "urn:vpro:ws:media:2009";

    public static final String IMAGE_NAMESPACE = "urn:vpro:image:2009";


    public static final Schema SCHEMA;

    static {
        try {
            SCHEMA = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(getStreamSources(SHARED_XSD, MEDIA_XSD, UPDATE_XSD, SEARCH_XSD));
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
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
            }
        }
        return result.toArray(new StreamSource[result.size()]);

    }

}
