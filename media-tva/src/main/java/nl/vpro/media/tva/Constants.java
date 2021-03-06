package nl.vpro.media.tva;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.function.Function;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.xml.sax.*;

import nl.vpro.domain.media.Channel;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@Slf4j
public class Constants {

    public static final String XSL_PARAM_NEWGENRES = "newGenres";

    public static final String XSL_PARAM_LONGDESCRIPTIONS = "longDescriptions";

    public final static String XSL_PARAM_CHANNELMAPPING = "channelMapping";

    public final static String XSL_PARAM_OWNER = "owner";

    public final static String XSL_PARAM_PERSON_URI_PREFIX = "personUriPrefix";

    public final static String XSL_PARAM_WORKFLOW = "workflow";

    public final static String TVA_NAMESPACE = "urn:tva:metadata:2004";


    public enum ChannelIdType {
        MIS(Channel::misId),
        PD(Channel::pdId),
        POMS(Channel::name),
        /**
         * @since 5.20.2
         */
        BINDINC(null);

        @Getter
        final Function<Channel, String> method;

        ChannelIdType(Function<Channel, String> method) {
            this.method = method;
        }
    }

    private static final Map<String, String> BINDINC_CHANNEL_MAPPINGS;
    static {
        Map<String, String> mappings = new HashMap<>();

        try {
            Properties properties = new Properties();
            properties.load(Constants.class.getResourceAsStream("/bindinc.channel.properties"));
            properties.forEach((k, v) -> mappings.put(k.toString(), v.toString()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        BINDINC_CHANNEL_MAPPINGS = Collections.unmodifiableMap(mappings);

    }
    public static Map<String, String> getBindincChannelMappings() {
        return BINDINC_CHANNEL_MAPPINGS;
    }

    public static Document createChannelMapping(ChannelIdType type) throws IOException, ParserConfigurationException, SAXException {
        Properties channelMapping = new Properties();
        if (type == ChannelIdType.BINDINC) {
            getBindincChannelMappings().forEach((k, v) -> channelMapping.put(k, Channel.valueOf(v).getXmlValue()));
        } else {
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
                    default:
                        throw new IllegalArgumentException();
                }
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
