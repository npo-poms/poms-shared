/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile;

import lombok.extern.log4j.Log4j2;

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
@Log4j2
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
            """
                <profile timestamp="1970-01-01T01:00:00+01:00" name="media" xmlns="urn:vpro:api:profile:2013" xmlns:constraint="urn:vpro:api:constraint" xmlns:media="urn:vpro:api:constraint:media:2013" xmlns:page="urn:vpro:api:constraint:page:2013">
                    <pageProfile/>
                </profile>""");
        assertNotNull(out.getPageProfile());
        assertThat((Predicate<Page>) out.getPageProfile()).isInstanceOf(ProfileDefinition.class);
    }

    @Test
    public void testGetMediaProfile() {
        Profile in = new Profile("media", null, new ProfileDefinition<>());
        in.setTimestamp(Instant.EPOCH);
        Profile out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <profile timestamp="1970-01-01T01:00:00+01:00" name="media" xmlns="urn:vpro:api:profile:2013" xmlns:constraint="urn:vpro:api:constraint" xmlns:media="urn:vpro:api:constraint:media:2013" xmlns:page="urn:vpro:api:constraint:page:2013">
                    <mediaProfile/>
                </profile>""");
        assertNotNull(out.getMediaProfile());
        assertThat((Predicate<MediaObject>) out.getMediaProfile()).isInstanceOf(ProfileDefinition.class);
    }

    @Test
    public void testAgeRatingProfile() {
        Profile in = new Profile("agerating", null, new ProfileDefinition<>(new Filter(new AgeRatingConstraint(AgeRating._6))));
        in.setTimestamp(Instant.EPOCH);
        Profile out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <profile timestamp="1970-01-01T01:00:00+01:00" name="agerating" xmlns="urn:vpro:api:profile:2013" xmlns:constraint="urn:vpro:api:constraint" xmlns:media="urn:vpro:api:constraint:media:2013" xmlns:page="urn:vpro:api:constraint:page:2013">
                    <mediaProfile>
                        <media:filter>
                            <media:ageRating>6</media:ageRating>
                        </media:filter>
                    </mediaProfile>
                </profile>""");
        assertNotNull(out.getMediaProfile());
        assertThat((Predicate<MediaObject>) out.getMediaProfile()).isInstanceOf(ProfileDefinition.class);
    }

    @Test
    public void testGenreProfile() {
        Profile in = new Profile("genre", null, new ProfileDefinition<>(new Filter(new GenreConstraint("Jeugd"))));
        in.setTimestamp(Instant.EPOCH);
        Profile out = JAXBTestUtil.roundTripAndSimilar(in,
            """
                <profile timestamp="1970-01-01T01:00:00+01:00" name="genre" xmlns="urn:vpro:api:profile:2013" xmlns:constraint="urn:vpro:api:constraint" xmlns:media="urn:vpro:api:constraint:media:2013" xmlns:page="urn:vpro:api:constraint:page:2013">
                    <mediaProfile>
                        <media:filter>
                            <media:genre>Jeugd</media:genre>
                        </media:filter>
                    </mediaProfile>
                </profile>""");
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
        String in = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <profile:profile xmlns:media="urn:vpro:api:constraint:media:2013" xmlns:page="urn:vpro:api:constraint:page:2013" xmlns:profile="urn:vpro:api:profile:2013" name="test-profiel">
              <profile:mediaProfile>
                <media:filter>
                  <media:scheduleEvent operator="LT" date="two years ago midnight" />
                </media:filter>
              </profile:mediaProfile>
            </profile:profile>""";

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
        log.info(Arrays.asList(predicateTestResult.getDescription()));
    }


    @Test
    public void testPredicateZapp() {
        Program program1 = MediaTestDataBuilder.program().withImages().withLocations().build();
        Program program2 = MediaTestDataBuilder.program().withImages().withLocations().ageRating(AgeRating._16).build();
        ProfileDefinition<MediaObject> predicate = getZappProfile().getMediaProfile();
        PredicateTestResult predicateTestResult1 = predicate.testWithReason(program1);
        assertThat(predicate.test(program1)).isTrue();
        log.info(Arrays.asList(predicateTestResult1.getDescription()));
        PredicateTestResult predicateTestResult2 = predicate.testWithReason(program2);
        assertThat(predicate.test(program2)).isFalse();
        log.info(Arrays.asList(predicateTestResult2.getDescription()));
    }

    @Test
    public void npostart() {
        ProfileDefinition<MediaObject> mediaProfile = getNpoStartProfile().getMediaProfile();
        log.info("{}", mediaProfile);
        assertThat(mediaProfile.getFilter()).isNotNull();
    }

    Profile getHumanProfile() {
        String in = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <profile xmlns:media="urn:vpro:api:constraint:media:2013" xmlns:constraint="urn:vpro:api:constraint" xmlns:page="urn:vpro:api:constraint:page:2013" xmlns="urn:vpro:api:profile:2013" timestamp="2015-02-06T08:30:08.486+01:00" name="human">
              <pageProfile since="0">
                <page:filter>
                  <page:portal>HUMANNL</page:portal>
                </page:filter>
              </pageProfile>
              <mediaProfile since="5680508">
                <media:filter>
                  <media:and>
                    <media:or>
                      <media:broadcaster>HUMA</media:broadcaster>
                      <media:broadcaster>HUMAN</media:broadcaster>
                    </media:or>
                    <media:hasImage/>
                    <media:or>
                      <media:hasLocation/>
                      <media:type>SEGMENT</media:type>
                    </media:or>
                  </media:and>
                </media:filter>
              </mediaProfile>
            </profile>
            """;
        return JAXB.unmarshal(new StringReader(in), Profile.class);
    }

    Profile getZappProfile() {
        String in = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <profile:profile xmlns:media="urn:vpro:api:constraint:media:2013" xmlns:page="urn:vpro:api:constraint:page:2013" xmlns:profile="urn:vpro:api:profile:2013" name="zappnet">
              <profile:pageProfile>
                <page:filter>
                  <page:portal>ZAPP</page:portal>
                </page:filter>
              </profile:pageProfile>
              <profile:mediaProfile>
                <media:filter>
                  <media:and>
                    <media:hasImage/>
                    <media:or>
                      <media:hasLocation/>
                      <media:type>SEGMENT</media:type>
                    </media:or>
                    <media:not>
                      <media:isExclusive />
                    </media:not>
                    <media:or>
                      <media:not><media:hasAgeRating /></media:not>
                      <media:ageRating>6</media:ageRating>
                      <media:ageRating>9</media:ageRating>
                      <media:ageRating>ALL</media:ageRating>
                    </media:or>
                  </media:and>
                </media:filter>
              </profile:mediaProfile>
            </profile:profile>
            """;
        log.info(in);
        return JAXB.unmarshal(new StringReader(in), Profile.class);
    }

    public Profile getNpoStartProfile() {
        String in = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <profile xmlns:constraint="urn:vpro:api:constraint:2014"
                     xmlns="urn:vpro:api:profile:2013"
                     xmlns:media="urn:vpro:api:constraint:media:2013"
                     xmlns:shared="urn:vpro:shared:2009" xmlns:page="urn:vpro:api:constraint:page:2013"
                     name="npostart">
              <mediaProfile>
                <media:filter>
                  <media:not>
                    <media:isExclusive/>
                  </media:not>
                </media:filter>
              </mediaProfile>
            </profile>""";
        return JAXB.unmarshal(new StringReader(in), Profile.class);

    }
}
