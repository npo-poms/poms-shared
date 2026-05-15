package nl.vpro.domain.media.support;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

class DescriptionTest {

    @Test
    void strip() {
        assertThat(Description.strip("foo\r\nbar")).isEqualTo("foo\nbar");
        assertThat(Description.strip("foo\rbar")).isEqualTo("foo\nbar");
    }

    @Test
    void jacksonSerializationNormalizesNewLines() throws Exception {
        Description description = new Description("foo\rbar", OwnerType.BROADCASTER, TextualType.MAIN);

        assertThat(Jackson2Mapper.getInstance().writeValueAsString(description)).contains("\"value\":\"foo\\nbar\"");
    }

    @Test
    void jacksonDeserializationNormalizesNewLines() throws Exception {
        Description description = Jackson2Mapper.getInstance().readValue(
            """
                {
                  "value": "foo\\rbar",
                  "owner": "BROADCASTER",
                  "type": "MAIN"
                }
                """,
            Description.class
        );

        assertThat(description.get()).isEqualTo("foo\nbar");
    }
}
