package nl.vpro.media.tva;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.function.Function;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import nl.vpro.domain.media.Channel;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.23
 */
@Slf4j
class ConstantsTest {


    @ParameterizedTest
    @EnumSource(Constants.ChannelIdType.class)
    public void createChannelMapping(Constants.ChannelIdType channelIdType) throws ParserConfigurationException, SAXException, IOException {
        Document document = Constants.createChannelMapping(channelIdType);
        NodeList nodeList = document.getElementsByTagName("entry");
        Function<Channel, String> method = channelIdType.getMethod();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node entry = nodeList.item(i);
            String key = entry.getAttributes().getNamedItem("key").getTextContent();
            String value = entry.getTextContent();
            if (method != null) {
                assertThat(method.apply(Channel.valueOfXml(value))).isEqualTo(key);
            } else {
                assertThat(Channel.valueOfXml(value)).isNotNull();
                assertThat(key).isNotBlank();
            }
        }
    }

}
