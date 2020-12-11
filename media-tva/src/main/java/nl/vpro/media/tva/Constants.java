package nl.vpro.media.tva;

import java.io.*;
import java.util.Properties;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.xml.sax.*;

import nl.vpro.domain.media.Channel;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class Constants {

    public static final String XSL_PARAM_NEWGENRES = "newGenres";

    public final static String XSL_PARAM_CHANNELMAPPING = "channelMapping";

    public final static String XSL_PARAM_OWNER = "owner";

    public final static String XSL_PARAM_PERSON_URI_PREFIX = "personUriPrefix";

    public final static String XSL_PARAM_WORKFLOW = "workflow";


    public enum ChannelIdType {
        MIS, PD, POMS, BINDINC
    }


    public static Document createChannelMapping(ChannelIdType type) throws IOException, ParserConfigurationException, SAXException {
        Properties channelMapping = new Properties();
        for (Channel channel : Channel.values()) {
            switch (type) {
                case MIS:
                    channelMapping.setProperty(channel.misId(), channel.getXmlEnumValue());
                    break;
                case PD:
                    channelMapping.setProperty(channel.pdId(), channel.getXmlEnumValue());
                    break;
                case POMS:
                    channelMapping.setProperty(channel.name(), channel.getXmlEnumValue());
                    break;
                case BINDINC:
                    channelMapping.setProperty(channel.bindincId(), channel.getXmlEnumValue());
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        channelMapping.storeToXML(stream, null);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = factory.newDocumentBuilder();
        parser.setEntityResolver(new EntityResolver() {
            @Override
            public InputSource resolveEntity(String publicId, String systemId) {
                if ("http://java.sun.com/dtd/properties.dtd".equals(systemId)) {
                    InputStream input = getClass().getResourceAsStream("/java/util/properties.dtd");
                    return new InputSource(input);
                }
                return null;
            }
        });
        return parser.parse(new ByteArrayInputStream(stream.toByteArray()));
    }

}
