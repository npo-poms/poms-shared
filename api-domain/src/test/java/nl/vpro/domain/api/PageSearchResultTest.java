package nl.vpro.domain.api;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.media.MediaFacetsResult;
import nl.vpro.domain.api.page.PageFacetsResult;
import nl.vpro.domain.api.page.PageSearchResult;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.page.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

@Slf4j
public class PageSearchResultTest {

    @Test
    public void testJson() {
        List<SearchResultItem<? extends Page>> list = new ArrayList<>();
        Page page = PageBuilder
            .page(PageType.ARTICLE)
            .embeds(MediaTestDataBuilder.program().lean().withLocations().build())
            .build();
        SearchResultItem<Page> item = new SearchResultItem<>(page);
        list.add(item);
        PageSearchResult result = new PageSearchResult(list, 0L, 10, Result.Total.equalsTo(1L));
        result.setFacets(new PageFacetsResult());
        result.setMediaFacets(new MediaFacetsResult());

        Jackson2TestUtil.roundTripAndSimilar(result, """
            {
               "facets" : { },
               "mediaFacets" : { },
               "total" : 1,
               "totalQualifier" : "EQUAL_TO",
               "offset" : 0,
               "max" : 10,
               "items" : [ {
                 "result" : {
                   "objectType" : "page",
                   "type" : "ARTICLE",
                   "embeds" : [ {
                     "media" : {
                       "objectType" : "program",
                       "embeddable" : true,
                       "broadcasters" : [ ],
                       "genres" : [ ],
                       "countries" : [ ],
                       "languages" : [ ],
                       "predictions" : [ {
                         "state" : "REALIZED",
                         "platform" : "INTERNETVOD"
                       } ],
                       "locations" : [ {
                         "programUrl" : "http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v",
                         "avAttributes" : {
                           "bitrate" : 1500,
                           "avFileFormat" : "MP4"
                         },
                         "owner" : "BROADCASTER",
                         "creationDate" : 1457102700000,
                         "workflow" : "PUBLISHED",
                         "offset" : 780000,
                         "duration" : 600000,
                         "platform" : "INTERNETVOD"
                       }, {
                         "programUrl" : "http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf",
                         "avAttributes" : {
                           "bitrate" : 3000,
                           "avFileFormat" : "WM"
                         },
                         "owner" : "BROADCASTER",
                         "creationDate" : 1457099100000,
                         "workflow" : "PUBLISHED",
                         "platform" : "INTERNETVOD"
                       }, {
                         "programUrl" : "http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf",
                         "avAttributes" : {
                           "bitrate" : 2000,
                           "avFileFormat" : "WM"
                         },
                         "owner" : "BROADCASTER",
                         "creationDate" : 1457095500000,
                         "workflow" : "PUBLISHED",
                         "duration" : 1833000,
                         "platform" : "INTERNETVOD"
                       }, {
                         "programUrl" : "http://player.omroep.nl/?aflID=4393288",
                         "avAttributes" : {
                           "bitrate" : 1000,
                           "avFileFormat" : "HTML"
                         },
                         "owner" : "NEBO",
                         "creationDate" : 1457091900000,
                         "workflow" : "PUBLISHED",
                         "platform" : "INTERNETVOD"
                       } ]
                     }
                   } ]
                 }
               } ]
             }""");

    }

    @Test
    public void testUnMarshal() throws IOException {
        PageSearchResult searchResultItems = Jackson2Mapper.getInstance().readValue(getClass().getResourceAsStream("/pageSearchResult.json"), PageSearchResult.class);
        log.info("{}", searchResultItems);

    }


}
