package nl.vpro.domain.media;

import java.net.URI;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class NameTest {

    @Test
    public void testJson() {

        Name name = Name.builder()
            .id(1L)
            .role(RoleType.GUEST)
            .uri(URI.create("http://gtaa/1"))
            .name("Doe Maar")
            .scopeNotes(Arrays.asList("bla1", "bla2"))
            .gtaaStatus(GTAAStatus.approved)
            .build();

        assertThatJson(name).isSimilarTo(
            "{ " +
                "\"objectType\" : \"name\"," +
                "\"role\" : \"GUEST\", " +
                "\"name\" : \"Doe Maar\", " +
                "\"scopeNotes\" : [ \"bla1\", \"bla2\" ], " +
                "\"gtaaUri\" : \"http://gtaa/1\", " +
                "\"gtaaStatus\" : \"approved\" " +
                "}").andRounded().isEqualTo(name);
    }

    @Test
    public void testJsonWithoutType() throws JsonProcessingException {

        Name name = (Name) Jackson2Mapper.getInstance().readValue(
            "{ " +
            "\"role\" : \"GUEST\", " +
            "\"name\" : \"Doe Maar\", " +
            "\"scopeNotes\" : [ \"bla1\", \"bla2\" ], " +
            "\"gtaaUri\" : \"http://gtaa/1\", " +
            "\"gtaaStatus\" : \"approved\" " +
            "}", Credits.class);

        assertThat(name.getRole()).isEqualTo(RoleType.GUEST);
        assertThat(name.getName()).isEqualTo("Doe Maar");
        assertThat(name.getGtaaUri()).isEqualTo("http://gtaa/1");
        assertThat(name.getGtaaStatus()).isEqualTo(GTAAStatus.approved);
        assertThat(name.getScopeNotes().size()).isEqualTo(2);
        assertThat(name.getScopeNotes().get(0)).isEqualTo("bla1");
        assertThat(name.getScopeNotes().get(1)).isEqualTo("bla2");
    }

    @Test
    public void testXml() {

        Name name = Name.builder()
            .id(1L)
            .role(RoleType.GUEST)
            .uri(URI.create("http://gtaa/1"))
            .name("Doe Maar")
            .scopeNotes(Arrays.asList("bla1", "bla2"))
            .gtaaStatus(GTAAStatus.approved)
            .build();

        JAXBTestUtil.assertThatXml(name).isSimilarTo(
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<local:name gtaaUri=\"http://gtaa/1\" gtaaStatus=\"approved\" role=\"GUEST\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">\n" +
                "    <name>Doe Maar</name>\n" +
                "    <scopeNote>bla1</scopeNote>\n" +
                "    <scopeNote>bla2</scopeNote>\n" +
                "</local:name>"
        ).andRounded().isEqualTo(name);
    }
}
