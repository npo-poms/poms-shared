/**
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import java.util.Date;

import org.junit.Test;

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
    public void testForm() throws Exception {
        PageForm form = PageFormBuilder.form()
            .text(Match.SHOULD, "text")
            .sortDate(new Date(0), new Date(1000))
            .sortDateFacet(new DateRangeFacetItem(), DateRangePreset.TODAY, DateRangePreset.LAST_WEEK)
            .types(PageType.ARTICLE)
            .typeFacet()
            .broadcasters("VPRO")
            .broadcasterFacet()
            .relationText(RelationDefinition.of("DIRECTOR", "VPRO"), "Stanley Kubrick")
            .relationText(RelationDefinition.of("DIRECTOR", "VPRO"), ExtendedTextMatcher.not("Quintin Tarentino"))
            .build();
        PageForm result = Jackson2TestUtil.roundTripAndSimilar(form,
            "{\n" +
                "  \"searches\" : {\n" +
                "    \"text\" : {\n" +
                "      \"value\" : \"text\",\n" +
                "      \"match\" : \"should\"\n" +
                "    },\n" +
                "    \"broadcasters\" : \"VPRO\",\n" +
                "    \"types\" : {\n" +
                "      \"value\" : \"ARTICLE\",\n" +
                "      \"match\" : \"should\"\n" +
                "    },\n" +
                "    \"sortDates\" : [ {\n" +
                "      \"begin\" : 0,\n" +
                "      \"end\" : 1000,\n" +
                "      \"inclusiveEnd\" : false\n" +
                "    } ],\n" +
                "    \"relations\" : [ {\n" +
                "      \"types\" : \"DIRECTOR\",\n" +
                "      \"broadcasters\" : \"VPRO\",\n" +
                "      \"values\" : \"Stanley Kubrick\"\n" +
                "    }, {\n" +
                "      \"types\" : \"DIRECTOR\",\n" +
                "      \"broadcasters\" : \"VPRO\",\n" +
                "      \"values\" : {\n" +
                "        \"value\" : \"Quintin Tarentino\",\n" +
                "        \"match\" : \"not\"\n" +
                "      }\n" +
                "    } ]\n" +
                "  },\n" +
                "  \"facets\" : {\n" +
                "    \"sortDates\" : [ { }, \"TODAY\", \"LAST_WEEK\" ],\n" +
                "    \"broadcasters\" : {\n" +
                "      \"sort\" : \"COUNT_DESC\",\n" +
                "      \"max\" : 24\n" +
                "    },\n" +
                "    \"types\" : {\n" +
                "      \"sort\" : \"COUNT_DESC\",\n" +
                "      \"max\" : 24\n" +
                "    }\n" +
                "  },\n" +
                "  \"highlight\" : false\n" +
                "}");
        System.out.println(result);

    }

    @Test
    public void testFormWithMediaFacets() throws Exception {
        PageForm form = PageFormBuilder.form()
            .mediaFacet(MediaFacetsBuilder.facets()
                .titles()
                .types()
                .sortDates(new DateRangeFacetItem("Mijn tijd range", new Date(), new Date()), DateRangePreset.TODAY, DateRangePreset.LAST_WEEK)
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
