/*
 * Copyright (C) 2017 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.classification.CachedURLClassificationServiceImpl;
import nl.vpro.domain.page.*;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Machiel
 * @since 5.4
 */
public class GenreConstraintTest {

    private static CachedURLClassificationServiceImpl cs;

    @BeforeAll
    public static void init() throws URISyntaxException {
        URL url = GenreConstraintTest.class.getResource("/nl/vpro/domain/media/classification/ebu_ContentGenreCS.xml");
        cs = new CachedURLClassificationServiceImpl(url.toURI());
    }

    @Test
    public void testGetValue() {
        GenreConstraint in = new GenreConstraint("jeugd");
        GenreConstraint out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:genreConstraint xmlns:page=\"urn:vpro:api:constraint:page:2013\" xmlns:local=\"uri:local\">jeugd</local:genreConstraint>");
        assertThat(out.getValue()).isEqualTo("jeugd");
    }

    @Test
    public void testGetESPath() {
        assertThat(new GenreConstraint().getESPath()).isEqualTo("genres.id");
    }

    @Test
    public void testApplyWhenTrue() {
        Page article = PageBuilder.page(PageType.ARTICLE).genres(cs.getTerm("3.0.1.1")).build();
        assertThat(new GenreConstraint("3.0.1.1").test(article)).isTrue();
    }

    @Test
    public void testApplyWhenFalse() {
        Page article = PageBuilder.page(PageType.ARTICLE).genres(cs.getTerm("3.0.1.1.7")).build();
        assertThat(new GenreConstraint("3.0.1.1.7").test(article)).isTrue();
        assertThat(new GenreConstraint("3.0.1.1").test(article)).isFalse();
        assertThat(new GenreConstraint("3.0.3").test(article)).isFalse();
    }

    @Test
    public void testSubPath() {
        GenreConstraint genreConstraint = new GenreConstraint("3.0.1.*");
        assertThat(genreConstraint.test(article("3.0.1.1.7"))).isTrue();
        assertThat(genreConstraint.test(article("3.0.1.1"))).isTrue();

        // Hard to support that in ES.
        // Do an OR.

        // assertThat(genreConstraint.test(article("3.0.1"))).isTrue();
        assertThat(new GenreConstraint("3.0.1*")
            .test(article("3.0.1")))
            .isTrue();
    }

    private Page article(String g) {
        Page article = PageBuilder.page(PageType.ARTICLE).genres(cs.getTerm(g)).build();
        return article;
    }
}
