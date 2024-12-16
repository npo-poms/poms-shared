package nl.vpro.domain.media;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.BasicObjectTheory;

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
public class PersonTest implements BasicObjectTheory<Person> {


    @Test
    public void json() {
        Person person = new Person("Pietje", "Puk", RoleType.ACTOR);
        person.setGtaaInfo(new EmbeddablePerson("http://data.beeldengeluid.nl/gtaa/1869521", GTAAStatus.approved));
        assertThatJson(person).isSimilarTo(
            """
                {
                 "objectType" : "person",
                  "givenName" : "Pietje",
                  "familyName" : "Puk",
                  "role" : "ACTOR",
                  "gtaaUri" : "http://data.beeldengeluid.nl/gtaa/1869521",
                  "gtaaStatus" : "approved"
                }""").andRounded().isEqualTo(person);

    }
    @Test
    public void jsonWithoutType() throws JsonProcessingException {

        Person person = (Person) Jackson2Mapper.getInstance().readValue(
            """
                {
                  "givenName" : "Pietje",
                  "familyName" : "Puk",
                  "role" : "ACTOR",
                  "gtaaUri" : "http://data.beeldengeluid.nl/gtaa/1869521",
                  "gtaaStatus" : "approved"
                }""", Credits.class);
        assertThat(person.getRole()).isEqualTo(RoleType.ACTOR);


    }


    @Test
    public void xml() {
        Person person = new Person("Pietje", "Puk", RoleType.ACTOR);
        person.setGtaaInfo(new EmbeddablePerson("http://data.beeldengeluid.nl/gtaa/1869521", GTAAStatus.approved));
        JAXBTestUtil.assertThatXml(person).isSimilarTo(
            """
                <local:person role="ACTOR" gtaaStatus="approved" gtaaUri="http://data.beeldengeluid.nl/gtaa/1869521" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:local="uri:local">
                    <givenName>Pietje</givenName>
                    <familyName>Puk</familyName>
                </local:person>""").andRounded().isEqualTo(person);

    }

    @Override
    public Arbitrary<Person> datapoints() {
        Person pietjePuk = new Person("Pietje", "Puk", RoleType.ACTOR);
        pietjePuk.setGtaaInfo(new EmbeddablePerson("http://data.beeldengeluid.nl/gtaa/1869521", GTAAStatus.approved));
        Person pietjePuk2 = new Person("Pietje", "Puk", RoleType.DIRECTOR);
        pietjePuk2.setGtaaInfo(new EmbeddablePerson("http://data.beeldengeluid.nl/gtaa/1869521", GTAAStatus.approved));
        Person pietjePuk3 = new Person("Pietje", "Puk", RoleType.DIRECTOR);

        return Arbitraries.of(
                pietjePuk,
                pietjePuk2,
                pietjePuk3
            );
    }

  /*  @Override
    public Arbitrary<? extends Tuple.Tuple2<? extends Person, ? extends Person>> equalDatapoints() {
        Person pietje = Person.builder()
            .givenName("Pietje")
            .familyName("Puk")
            .role(RoleType.PRESENTER)
            .uri(URI.create("https://gtaa.nl/1234"))
            .build();

        return Arbitraries.of(
            Tuple.of(pietje, pietje),
            Tuple.of(pietje, Person
                    .builder()
                    .gtaaUri(pietje.getGtaaUri())
                    .build()
            )
        );
    }*/
}
