package nl.vpro.domain.api.media.bind;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.media.MediaForm;
import nl.vpro.domain.api.media.MediaSortField;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class MediaSortOrderListJsonTest {

    @Test
    public void test() throws IOException {
        String example = "{\n" +
            "    \"searches\" : {\n" +
            "        \"text\" : \"Twee vandaag\"\n" +
            "    },\n" +
            "    \"sort\" : {\n" +
            "        \"title\" :{\n" +
            "            \"order\" : \"ASC\",\n" +
            "            \"type\" : \"LEXICO\",\n" +
            "            \"owner\" : \"BROADCASTER\"\n" +
            "        },\n" +
            "        \"sortDate\" : \"DESC\"\n" +
            "    }\n" +
            "}\n";

        MediaForm form = Jackson2Mapper.getLenientInstance().readValue(example, MediaForm.class);
        assertThat(form.getSortFields()).hasSize(2);
        assertThat(form.getSortFields().get(0).getField()).isEqualTo(MediaSortField.title);
    }


    @Test
    public void testArray() throws IOException {
        String example = "{\n" +
            "    \"searches\" : {\n" +
            "        \"text\" : \"Twee vandaag\"\n" +
            "    },\n" +
            "    /* json doesn't actually have a order, so we should support arrays too */\n" +
            "    \"sort\" : [\n" +
            "        {\n" +
            "            \"field\": \"title\",\n" +
            "            \"order\" : \"ASC\",\n" +
            "            \"type\" : \"LEXICO\",\n" +
            "            \"owner\" : \"BROADCASTER\"\n" +
            "        },\n" +
            "        {\n" +
            "            \"field\": \"sortDate\",\n" +
            "            \"order\" : \"DESC\"\n" +
            "        },\n" +
            "        \"lastModified\"\n" +
            "    ]\n" +
            "}\n";

        MediaForm form = Jackson2Mapper.getLenientInstance().readValue(example, MediaForm.class);
        assertThat(form.getSortFields()).hasSize(3);
        assertThat(form.getSortFields().get(0).getField()).isEqualTo(MediaSortField.title);
    }

}
