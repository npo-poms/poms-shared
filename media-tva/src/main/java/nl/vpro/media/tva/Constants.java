package nl.vpro.media.tva;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import java.util.function.Function;

import jakarta.xml.parsers.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
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

    /**
     * In bindinc XML's genres are matched on name, and in the XSLT prefixed by this.
     */
    public final static String BINDINC_GENRE_PREFIX = "urn:bindinc:genre:";

    @Getter
    public enum ChannelIdType {
        MIS(Channel::misId),
        PD(Channel::pdId),
        POMS(Channel::name),
        /**
         * @since 5.20.2
         */
        BINDINC(null);

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
            properties.forEach((k, v) -> {
                String previous = mappings.put(k.toString(), v.toString());
                if (previous != null) {
                    log.warn("Replaced mapping {} {} -> {}", k, previous, v);
                }
                }
            );
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        BINDINC_CHANNEL_MAPPINGS = Collections.unmodifiableMap(mappings);

    }

    @NonNull
    public static Map<String, String> getBindincChannelMappings() {
        return BINDINC_CHANNEL_MAPPINGS;
    }

    public static Document createChannelMapping(ChannelIdType type) throws IOException, ParserConfigurationException, SAXException {
        Properties channelMapping = new Properties();
        if (type == ChannelIdType.BINDINC) {
            final Map<Channel, String> targetted = new HashMap<>();
            getBindincChannelMappings().forEach((k, v) -> {
                Channel c = Channel.valueOf(v);
                String prevKey = targetted.put(c, k);
                if (prevKey != null) {
                    log.warn("{} is also mapped from {}", c.getXmlEnumValue(), prevKey);
                }
                Object put = channelMapping.put(k, c.getXmlValue());
                if (put != null){
                    log.warn("Replaced {} -> {} -> {}", k, put, c.getXmlValue());
                }
                }
            );
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
        Document parse = parser.parse(new ByteArrayInputStream(stream.toByteArray()));
        NodeList nodeList = parse.getElementsByTagName("entry");
        assert  nodeList.getLength() == Channel.values().length : channelMapping.size() + "->" + nodeList.getLength() + " != " + Channel.values().length;

        return parse;
    }

}
