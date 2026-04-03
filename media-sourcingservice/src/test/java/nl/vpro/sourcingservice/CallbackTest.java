package nl.vpro.sourcingservice;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

class CallbackTest {


    @Test
    public void unmarshal() throws JsonProcessingException {
        String actual = """
        {"asset_url":"https://test.entry.cdn.npoaudio.nl/handle/WO_BV_A20011542.mp4","media_id":"WO_BV_A20011542","status":"Failed"}
        """;
        Callback o = Jackson2Mapper.getLenientInstance().readerFor(Callback.class).readValue(actual);
        assertThat(o.getMedia_id()).isEqualTo("WO_BV_A20011542");
        assertThat(o.getStatus()).isEqualTo("Failed");
    }

}
