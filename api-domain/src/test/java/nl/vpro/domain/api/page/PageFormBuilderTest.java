/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.DateRangeFacetItem;
import nl.vpro.domain.api.DateRangePreset;
import nl.vpro.domain.api.ExtendedTextMatcher;
import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.media.MediaFacetsBuilder;
import nl.vpro.domain.api.media.MediaSearch;
import nl.vpro.domain.page.PageType;
import nl.vpro.domain.page.RelationDefinition;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class PageFormBuilderTest {

    @Test
    public void testForm() {
        PageForm form = PageFormBuilder.form()
            .text(Match.SHOULD, "text")
            .sortDate(Instant.EPOCH, Instant.ofEpochMilli(1000))
            .sortDateFacet(new DateRangeFacetItem(), DateRangePreset.TODAY, DateRangePreset.LAST_WEEK)
            .types(PageType.ARTICLE)
            .typeFacet()
            .broadcasters("VPRO")
            .broadcasterFacet()
            .relationText(RelationDefinition.of("DIRECTOR", "VPRO"), "Stanley Kubrick")
            .relationText(RelationDefinition.of("DIRECTOR", "VPRO"), ExtendedTextMatcher.not("Quintin Tarentino"))
            .build();
        PageForm result = Jackson2TestUtil.roundTripAndSimilar(form,
            """
                {
                  "searches" : {
                    "text" : {
                      "value" : "text",
                      "match" : "SHOULD"
                    },
                    "broadcasters" : "VPRO",
                    "types" : {
                      "value" : "ARTICLE",
                      "match" : "SHOULD"
                    },
                    "sortDates" : [ {
                      "begin" : 0,
                      "end" : 1000,
                      "inclusiveEnd" : false
                    } ],
                    "relations" : [ {
                      "types" : "DIRECTOR",
                      "broadcasters" : "VPRO",
                      "values" : "Stanley Kubrick"
                    }, {
                      "types" : "DIRECTOR",
                      "broadcasters" : "VPRO",
                      "values" : {
                        "value" : "Quintin Tarentino",
                        "match" : "NOT"
                      }
                    } ]
                  },
                  "facets" : {
                    "sortDates" : [ { }, "TODAY", "LAST_WEEK" ],
                    "broadcasters" : {
                      "sort" : "COUNT_DESC",
                      "max" : 24
                    },
                    "types" : {
                      "sort" : "COUNT_DESC",
                      "max" : 24
                    }
                  }
                }""");
        System.out.println(result);

    }

    @Test
    public void testFormWithMediaFacets() throws Exception {
        PageForm form = PageFormBuilder.form()
            .mediaFacet(MediaFacetsBuilder.facets()
                .titles()
                .types()
                .sortDates(new DateRangeFacetItem("Mijn tijd range", Instant.now(), Instant.now()), DateRangePreset.TODAY, DateRangePreset.LAST_WEEK)
                .broadcasters()
                .genres()
                .tags()
                .memberOf()
                .episodeOf()
                .descendantOf()
                .filter(new MediaSearch())
                .build())
            .build();

        System.out.println(Jackson2Mapper.getInstance().writeValueAsString(form));

    }
}
