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
        String example = """
            {
                "searches" : {
                    "text" : "Twee vandaag"
                },
                "sort" : {
                    "title" :{
                        "order" : "ASC",
                        "type" : "LEXICO",
                        "owner" : "BROADCASTER"
                    },
                    "sortDate" : "DESC"
                }
            }
            """;

        MediaForm form = Jackson2Mapper.getLenientInstance().readValue(example, MediaForm.class);
        assertThat(form.getSortFields()).hasSize(2);
        assertThat(form.getSortFields().get(0).getField()).isEqualTo(MediaSortField.title);
    }


    @Test
    public void testArray() throws IOException {
        String example = """
            {
                "searches" : {
                    "text" : "Twee vandaag"
                },
                /* json doesn't actually have a order, so we should support arrays too */
                "sort" : [
                    {
                        "field": "title",
                        "order" : "ASC",
                        "type" : "LEXICO",
                        "owner" : "BROADCASTER"
                    },
                    {
                        "field": "sortDate",
                        "order" : "DESC"
                    },
                    "lastModified"
                ]
            }
            """;

        MediaForm form = Jackson2Mapper.getLenientInstance().readValue(example, MediaForm.class);
        assertThat(form.getSortFields()).hasSize(3);
        assertThat(form.getSortFields().get(0).getField()).isEqualTo(MediaSortField.title);
    }

}
