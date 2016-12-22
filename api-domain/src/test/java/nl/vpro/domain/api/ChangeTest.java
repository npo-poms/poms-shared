package nl.vpro.domain.api;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerator;

import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Schedule;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeTest {

    @Test
    public void tailJson() throws IOException {
        Change tail = Change.tail(100);
        assertThat(Jackson2Mapper.getInstance().writeValueAsString(tail)).isEqualTo("{\"revision\":100,\"tail\":true}");

        JsonGenerator jg = Jackson2Mapper.INSTANCE.getFactory().createGenerator(System.out);
        jg.writeObject(tail);
    }

    @Test
    public void json() throws Exception {
        Change change = new Change();
        change.setPublishDate(LocalDate.of(2016, 7, 20).atTime(13, 38).atZone(Schedule.ZONE_ID).toInstant());
        change.setMid("MID_123");
        change.setDeleted(false);
        change.setMedia(MediaTestDataBuilder.program().lean().build());

        Jackson2TestUtil.roundTripAndSimilar(change, "{\n" +
            "  \"publishDate\" : 1469014680000,\n" +
            "  \"mid\" : \"MID_123\",\n" +
            "  \"media\" : {\n" +
            "    \"objectType\" : \"program\",\n" +
            "    \"embeddable\" : true,\n" +
            "    \"broadcasters\" : [ ],\n" +
            "    \"genres\" : [ ],\n" +
            "    \"countries\" : [ ],\n" +
            "    \"languages\" : [ ]\n" +
            "  }\n" +
            "}");
    }

}
