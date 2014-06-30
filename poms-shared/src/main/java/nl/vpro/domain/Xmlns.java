package nl.vpro.domain;

import java.io.IOException;
import java.net.URL;

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
    public static final URL    UPDATE_XSD       = Xmlns.class.getResource("//nl/vpro/domain/media/update/vproMediaUpdate.xsd");

    public static final String SEARCH_NAMESPACE = "urn:vpro:media:search:2012";
    public static final URL    SEARCH_XSD        = Xmlns.class.getResource("/nl/vpro/domain/media/search/vproMediaSearch.xsd");

    public static final Schema SCHEMA;

    static {
        try {
            SCHEMA = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(
                    new StreamSource[]{
                        new StreamSource(SHARED_XSD.openStream()),
                        new StreamSource(MEDIA_XSD.openStream()),
                        new StreamSource(UPDATE_XSD.openStream()),
                        new StreamSource(SEARCH_XSD.openStream())
                    }
                );
        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
