package nl.vpro.domain.media;

import java.util.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Michiel Meeuwissen
 * @since 2.1
 */
public class ChannelTest {

    @Test
    public void testNoDuplicateStrings() {
        Set<String> strings = new HashSet<>();
        for (Channel c: Channel.values()) {
            if (!strings.add(c.toString())) {
                fail("toString of " + c + " is duplicate");
            }
        }
    }

    @Test
    @Disabled("sadly fails, but we can't really fix it withough huge republications.")
    public void testJson() throws JsonProcessingException {
        assertThat(Jackson2Mapper.getInstance().writeValueAsString(Channel._101_)).isEqualTo("\"101_\"");
    }


    @Test
    public void testJson2() throws JsonProcessingException {
        assertThat(Jackson2Mapper.getInstance().writeValueAsString(Channel.NED1)).isEqualTo("\"NED1\"");
    }

    @Test
    public void testGetValues() {
        for (Channel c : Channel.values()) {
            assertThat(Channel.valuesOf(Collections.singletonList(c.name())).get(0)).isEqualTo(c);
        }
        Channel.valuesOf(Collections.singleton("13ST")).containsAll(Collections.singletonList(Channel._13ST));
        Channel.valuesOf(Collections.singleton("ARD")).containsAll(Collections.singletonList(Channel.ARD_));

    }
    /**
     * JS-195 To be able to generate js-parent/js-vpro-media-domain/vpro/media/domain/support/Channels.js
     * when channels change; instead of having to type them all
     * @throws Exception
     */
    //@Test
    public void printJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,JsonChannel> channels = new HashMap<>();
        for(Channel channel : Channel.values()) {
            channels.put(channel.name(), new JsonChannel(channel.name(), channel.toString()));
        }
        System.out.println(mapper.writeValueAsString(channels));
    }

    private static class JsonChannel {
        private final String id;
        private final String displayName;

        private JsonChannel(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getId() {
            return id;
        }
    }
}

