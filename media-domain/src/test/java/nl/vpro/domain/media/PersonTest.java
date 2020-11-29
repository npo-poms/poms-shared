package nl.vpro.domain.media;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.media.gtaa.EmbeddablePerson;
import nl.vpro.domain.media.gtaa.GTAAStatus;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
public class PersonTest {


    @Test
    public void json() {
        Person person = new Person("Pietje", "Puk", RoleType.ACTOR);
        person.setGtaaInfo(new EmbeddablePerson("http://data.beeldengeluid.nl/gtaa/1869521", GTAAStatus.approved));
        assertThatJson(person).isSimilarTo(
            "{\n" +
                " \"objectType\" : \"person\",\n" +
                "  \"givenName\" : \"Pietje\",\n" +
                "  \"familyName\" : \"Puk\",\n" +
                "  \"role\" : \"ACTOR\",\n" +
                "  \"gtaaUri\" : \"http://data.beeldengeluid.nl/gtaa/1869521\",\n" +
                "  \"gtaaStatus\" : \"approved\"\n"  +
                "}").andRounded().isEqualTo(person);

    }
    @Test
    public void jsonWithoutType() throws JsonProcessingException {

        Person person = (Person) Jackson2Mapper.getInstance().readValue(
            "{\n" +
                "  \"givenName\" : \"Pietje\",\n" +
                "  \"familyName\" : \"Puk\",\n" +
                "  \"role\" : \"ACTOR\",\n" +
                "  \"gtaaUri\" : \"http://data.beeldengeluid.nl/gtaa/1869521\",\n" +
                "  \"gtaaStatus\" : \"approved\"\n"  +
                "}", Credits.class);
        assertThat(person.getRole()).isEqualTo(RoleType.ACTOR);


    }


    @Test
    public void xml() {
        Person person = new Person("Pietje", "Puk", RoleType.ACTOR);
        person.setGtaaInfo(new EmbeddablePerson("http://data.beeldengeluid.nl/gtaa/1869521", GTAAStatus.approved));
        JAXBTestUtil.assertThatXml(person).isSimilarTo(
            "<local:person role=\"ACTOR\" gtaaStatus=\"approved\" gtaaUri=\"http://data.beeldengeluid.nl/gtaa/1869521\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:local=\"uri:local\">\n" +
            "    <givenName>Pietje</givenName>\n" +
            "    <familyName>Puk</familyName>\n" +
            "</local:person>").andRounded().isEqualTo(person);

    }

}
