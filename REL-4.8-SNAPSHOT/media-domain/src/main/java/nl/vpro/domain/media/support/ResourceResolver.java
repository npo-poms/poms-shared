package nl.vpro.domain.media.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import nl.vpro.domain.Xmlns;

/**
 * This is an {@link LSResourceResolver} (which makes it useable in schema validation) which can resolve the
 * namespaces used in the media project to their xsd schema's.
 *
 * @author Michiel Meeuwissen
 */
public class ResourceResolver implements LSResourceResolver {

    private static final Logger LOG  = LoggerFactory.getLogger(ResourceResolver.class);

    private static DOMImplementationLS DOM;
    static {
        try {
            DOMImplementation impl  = DOMImplementationRegistry.newInstance().getDOMImplementation("XML 3.0");
            DOM = (DOMImplementationLS) impl;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
    private static Map<String, URL> MAP = new TreeMap<>();
    static {
        MAP.put(Xmlns.MEDIA_NAMESPACE,  Xmlns.MEDIA_XSD);
        MAP.put(Xmlns.SHARED_NAMESPACE, Xmlns.SHARED_XSD);
        MAP.put(Xmlns.UPDATE_NAMESPACE, Xmlns.UPDATE_XSD);
        MAP.put(Xmlns.SEARCH_NAMESPACE, Xmlns.SEARCH_XSD);
    }

    public static Map<String, URL> getSchemas() {
        return Collections.unmodifiableMap(MAP);
    }

    public static URL resolveToURL(String namespaceURI) {
        return MAP.get(namespaceURI);
    }
    /**
     * Resolve namespace to an InputStream representing the XSD.
     * @param namespaceURI
     * @return
     */
    public static InputStream resolve(String namespaceURI)  {
        URL resource = resolveToURL(namespaceURI);
        if (resource != null) {
            try {
                return resource.openStream();
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

        InputStream resource = resolve(namespaceURI);
        if (resource != null) {
            LSInput lsinput = DOM.createLSInput();
            lsinput.setCharacterStream(new InputStreamReader(resource));
            return lsinput;
        } else {
            return null;
        }
    }

}
