package nl.vpro.domain;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.XMLConstants;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

/**
 * This is an {@link LSResourceResolver} (which makes it useable in schema validation) which can resolve the
 * namespaces used in the media project to their xsd schema's.
 *
 * @author Michiel Meeuwissen
 */
@Slf4j
public class ResourceResolver implements LSResourceResolver {


    public static final DOMImplementationLS DOM;
    static {
        DOMImplementation impl = null;
        try {
            impl  = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
         DOM = (DOMImplementationLS) impl;
    }
    private static final Map<String, URL> MAP = new TreeMap<>();
    static {
        MAP.put(XMLConstants.XML_NS_URI, Xmlns.XML_XSD);
        MAP.put(Xmlns.MEDIA_NAMESPACE, Xmlns.MEDIA_XSD);
        MAP.put(Xmlns.SHARED_NAMESPACE, Xmlns.SHARED_XSD);
        MAP.put(Xmlns.UPDATE_NAMESPACE, Xmlns.UPDATE_XSD);
        MAP.put(Xmlns.SEARCH_NAMESPACE, Xmlns.SEARCH_XSD);
        MAP.put(Xmlns.API_NAMESPACE, Xmlns.API_XSD);
        MAP.put(Xmlns.PAGE_CONSTRAINT_NAMESPACE, Xmlns.PAGE_CONSTRAINT_XSD);
        MAP.put(Xmlns.MEDIA_CONSTRAINT_NAMESPACE, Xmlns.MEDIA_CONSTRAINT_XSD);
        MAP.put(Xmlns.CONSTRAINT_NAMESPACE, Xmlns.CONSTRAINT_XSD);
        MAP.put(Xmlns.MEDIA_SUBTITLES_NAMESPACE, Xmlns.MEDIA_SUBTITLES_XSD);
    }

    public static Map<String, URL> getSchemas() {
        return Collections.unmodifiableMap(MAP);
    }

    public static URL resolveToURL(String namespaceURI) {
        if (namespaceURI == null) {
            return Xmlns.ABSENT_XSD;
        }
        return MAP.get(namespaceURI);
    }
    /**
     * Resolve namespace to an InputStream representing the XSD.
     */
    public static InputStream resolve(String namespaceURI)  {
        URL resource = resolveToURL(namespaceURI);
        if (resource != null) {
            try {
                return resource.openStream();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        } else {
            log.debug("No xsd found for {}", namespaceURI);
            return null;
        }
    }

    public static LSInput resolveNamespaceToLS(String namespaceURI) {
        URL url = resolveToURL(namespaceURI);

        if (url != null) {
            LSInput lsinput = DOM.createLSInput();
            try {
                lsinput.setCharacterStream(new InputStreamReader(url.openStream()));
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            lsinput.setSystemId(url.toString());
            //lsinput.setPublicId(namespaceURI);
            return lsinput;
        } else {
            return null;
        }
    }

    @Override
    public LSInput resolveResource(
        String type,
        String namespaceURI, String publicId, String systemId, String baseURI) {
        LSInput result = resolveNamespaceToLS(namespaceURI);
        if (result == null) {
            log.debug("{} / {}", systemId, baseURI);

        }
        return result;
    }

}
