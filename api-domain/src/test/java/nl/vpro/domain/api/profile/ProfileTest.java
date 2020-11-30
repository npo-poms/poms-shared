/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile;

import java.io.StringReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Predicate;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonMappingException;

import nl.vpro.domain.constraint.PredicateTestResult;
import nl.vpro.domain.constraint.media.*;
import nl.vpro.domain.media.*;
import nl.vpro.domain.page.Page;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class ProfileTest {

    @Test
    public void testGetNullValues() {
        Profile in = new Profile(null);
        assertNull(in.getName());
        assertNull(in.getPageProfile());
        assertNull(in.getMediaProfile());
    }
    @Test
    public void testGetName() {
        Profile in = new Profile("name");
        in.setTimestamp(Instant.EPOCH);

        Profile out = JAXBTestUtil.roundTripAndSimilar(in,
            "<profile timestamp=\"1970-01-01T01:00:00+01:00\" name=\"name\" xmlns=\"urn:vpro:api:profile:2013\" xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:media=\"urn:vpro:api:constraint:media:2013\"/>\n");
        assertThat(out.getName()).isEqualTo("name");
        assertThat(out.getTimestamp()).isEqualTo(new Date(0).toInstant());
    }


    @Test
    public void testGetPageProfile() {
        Profile in = new Profile("media", new ProfileDefinition<>(), null);
        in.setTimestamp(Instant.EPOCH);
        Profile out = JAXBTestUtil.roundTripAndSimilar(in,
            "<profile timestamp=\"1970-01-01T01:00:00+01:00\" name=\"media\" xmlns=\"urn:vpro:api:profile:2013\" xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:page=\"urn:vpro:api:constraint:page:2013\">\n" +
                "    <pageProfile/>\n" +
                "</profile>");
        assertNotNull(out.getPageProfile());
        assertThat((Predicate<Page>) out.getPageProfile()).isInstanceOf(ProfileDefinition.class);
    }

    @Test
    public void testGetMediaProfile() {
        Profile in = new Profile("media", null, new ProfileDefinition<>());
        in.setTimestamp(Instant.EPOCH);
        Profile out = JAXBTestUtil.roundTripAndSimilar(in,
            "<profile timestamp=\"1970-01-01T01:00:00+01:00\" name=\"media\" xmlns=\"urn:vpro:api:profile:2013\" xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:page=\"urn:vpro:api:constraint:page:2013\">\n" +
                "    <mediaProfile/>\n" +
                "</profile>");
        assertNotNull(out.getMediaProfile());
        assertThat((Predicate<MediaObject>) out.getMediaProfile()).isInstanceOf(ProfileDefinition.class);
    }

    @Test
    public void testAgeRatingProfile() {
        Profile in = new Profile("agerating", null, new ProfileDefinition<>(new Filter(new AgeRatingConstraint(AgeRating._6))));
        in.setTimestamp(Instant.EPOCH);
        Profile out = JAXBTestUtil.roundTripAndSimilar(in,
            "<profile timestamp=\"1970-01-01T01:00:00+01:00\" name=\"agerating\" xmlns=\"urn:vpro:api:profile:2013\" xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:page=\"urn:vpro:api:constraint:page:2013\">\n" +
            "    <mediaProfile>\n" +
            "        <media:filter>\n" +
            "            <media:ageRating>6</media:ageRating>\n" +
            "        </media:filter>\n" +
            "    </mediaProfile>\n" +
            "</profile>");
        assertNotNull(out.getMediaProfile());
        assertThat((Predicate<MediaObject>) out.getMediaProfile()).isInstanceOf(ProfileDefinition.class);
    }

    @Test
    public void testGenreProfile() {
        Profile in = new Profile("genre", null, new ProfileDefinition<>(new Filter(new GenreConstraint("Jeugd"))));
        in.setTimestamp(Instant.EPOCH);
        Profile out = JAXBTestUtil.roundTripAndSimilar(in,
                "<profile timestamp=\"1970-01-01T01:00:00+01:00\" name=\"genre\" xmlns=\"urn:vpro:api:profile:2013\" xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:page=\"urn:vpro:api:constraint:page:2013\">\n" +
                        "    <mediaProfile>\n" +
                        "        <media:filter>\n" +
                        "            <media:genre>Jeugd</media:genre>\n" +
                        "        </media:filter>\n" +
                        "    </mediaProfile>\n" +
                "</profile>");
        assertNotNull(out.getMediaProfile());
        assertThat((Predicate<MediaObject>) out.getMediaProfile()).isInstanceOf(ProfileDefinition.class);
    }

    @Test // not (yet?) supported
    @Disabled
    public void testJson() {
        assertThatThrownBy(() -> {
            Filter filter = new Filter();
            filter.setConstraint(new HasImageConstraint());
            ProfileDefinition<MediaObject> pd = new ProfileDefinition<>(filter);
            Profile in = new Profile("media", null, pd);
            System.out.println(Jackson2Mapper.getInstance().writeValueAsString(in));
        }).isInstanceOf(JsonMappingException.class);
    }

    @Test
    public void parseScheduleEvent() {
        String in = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<profile:profile xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:profile=\"urn:vpro:api:profile:2013\" name=\"test-profiel\">\n" +
            "  <profile:mediaProfile>\n" +
            "    <media:filter>\n" +
            "      <media:scheduleEvent operator=\"LT\" date=\"two years ago midnight\" />\n" +
            "    </media:filter>\n" +
            "  </profile:mediaProfile>\n" +
            "</profile:profile>";

        Profile profile = JAXB.unmarshal(new StringReader(in), Profile.class);
        ProfileDefinition<MediaObject> mediaProfile = profile.getMediaProfile();

        assertThat(mediaProfile.getFilter().getConstraint().toString()).isEqualTo("LT two years ago midnight");

    }

    @Test
    public void testHuman() {
        assertThat(getHumanProfile().getMediaProfile().getName()).isEqualTo("human");
    }


    @Test
    public void testPredicate() {
        Program program = MediaTestDataBuilder.program().build();
        ProfileDefinition<MediaObject> predicate = getHumanProfile().getMediaProfile();
        PredicateTestResult predicateTestResult = predicate.testWithReason(program);
        System.out.println(Arrays.asList(predicateTestResult.getDescription()));
    }


    @Test
    public void testPredicateZapp() {
        Program program1 = MediaTestDataBuilder.program().withImages().withLocations().build();
        Program program2 = MediaTestDataBuilder.program().withImages().withLocations().ageRating(AgeRating._16).build();
        ProfileDefinition<MediaObject> predicate = getZappProfile().getMediaProfile();
        PredicateTestResult predicateTestResult1 = predicate.testWithReason(program1);
        assertThat(predicate.test(program1)).isTrue();
        System.out.println(Arrays.asList(predicateTestResult1.getDescription()));
        PredicateTestResult predicateTestResult2 = predicate.testWithReason(program2);
        assertThat(predicate.test(program2)).isFalse();
        System.out.println(Arrays.asList(predicateTestResult2.getDescription()));
    }

    Profile getHumanProfile() {
        String in = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<profile xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:constraint=\"urn:vpro:api:constraint\" xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns=\"urn:vpro:api:profile:2013\" timestamp=\"2015-02-06T08:30:08.486+01:00\" name=\"human\">\n" +
            "  <pageProfile since=\"0\">\n" +
            "    <page:filter>\n" +
            "      <page:portal>HUMANNL</page:portal>\n" +
            "    </page:filter>\n" +
            "  </pageProfile>\n" +
            "  <mediaProfile since=\"5680508\">\n" +
            "    <media:filter>\n" +
            "      <media:and>\n" +
            "        <media:or>\n" +
            "          <media:broadcaster>HUMA</media:broadcaster>\n" +
            "          <media:broadcaster>HUMAN</media:broadcaster>\n" +
            "        </media:or>\n" +
            "        <media:hasImage/>\n" +
            "        <media:or>\n" +
            "          <media:hasLocation/>\n" +
            "          <media:type>SEGMENT</media:type>\n" +
            "        </media:or>\n" +
            "      </media:and>\n" +
            "    </media:filter>\n" +
            "  </mediaProfile>\n" +
            "</profile>\n";
        return JAXB.unmarshal(new StringReader(in), Profile.class);
    }

    Profile getZappProfile() {
        String in = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<profile:profile xmlns:media=\"urn:vpro:api:constraint:media:2013\" xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:profile=\"urn:vpro:api:profile:2013\" name=\"zappnet\">\n" +
            "  <profile:pageProfile>\n" +
            "    <page:filter>\n" +
            "      <page:portal>ZAPP</page:portal>\n" +
            "    </page:filter>\n" +
            "  </profile:pageProfile>\n" +
            "  <profile:mediaProfile>\n" +
            "    <media:filter>\n" +
            "      <media:and>\n" +
            "        <media:hasImage/>\n" +
            "        <media:or>\n" +
            "          <media:hasLocation/>\n" +
            "          <media:type>SEGMENT</media:type>\n" +
            "        </media:or>\n" +
            "        <media:not>\n" +
            "          <media:isExclusive />\n" +
            "        </media:not>\n" +
            "        <media:or>\n" +
            "          <media:not><media:hasAgeRating /></media:not>\n" +
            "          <media:ageRating>6</media:ageRating>\n" +
            "          <media:ageRating>9</media:ageRating>\n" +
            "          <media:ageRating>ALL</media:ageRating>\n" +
            "        </media:or>\n" +
            "      </media:and>\n" +
            "    </media:filter>\n" +
            "  </profile:mediaProfile>\n" +
            "</profile:profile>\n";
        System.out.println(in);
        return JAXB.unmarshal(new StringReader(in), Profile.class);
    }
}
