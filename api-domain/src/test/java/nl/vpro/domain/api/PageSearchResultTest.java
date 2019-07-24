package nl.vpro.domain.api;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import nl.vpro.domain.api.media.MediaFacetsResult;
import nl.vpro.domain.api.page.PageFacetsResult;
import nl.vpro.domain.api.page.PageSearchResult;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageBuilder;
import nl.vpro.domain.page.PageType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

@Slf4j
public class PageSearchResultTest {

    @Test
    public void testJson() throws Exception {
        List<SearchResultItem<? extends Page>> list = new ArrayList<>();
        Page page = PageBuilder
            .page(PageType.ARTICLE)
            .embeds(MediaTestDataBuilder.program().lean().withLocations().build())
            .build();
        SearchResultItem<Page> item = new SearchResultItem<>(page);
        list.add(item);
        PageSearchResult result = new PageSearchResult(list, 0L, 10, 1L);
        result.setFacets(new PageFacetsResult());
        result.setMediaFacets(new MediaFacetsResult());

        Jackson2TestUtil.roundTripAndSimilar(result, "{\n" +
            "  \"facets\" : { },\n" +
            "  \"mediaFacets\" : { },\n" +
            "  \"total\" : 1,\n" +
            "  \"offset\" : 0,\n" +
            "  \"max\" : 10,\n" +
            "  \"items\" : [ {\n" +
            "    \"result\" : {\n" +
            "      \"objectType\" : \"page\",\n" +
            "      \"type\" : \"ARTICLE\",\n" +
            "      \"embeds\" : [ {\n" +
            "        \"media\" : {\n" +
            "          \"objectType\" : \"program\",\n" +
            "          \"embeddable\" : true,\n" +
            "          \"broadcasters\" : [ ],\n" +
            "          \"genres\" : [ ],\n" +
            "          \"countries\" : [ ],\n" +
            "          \"languages\" : [ ],\n" +
            "          \"locations\" : [ {\n" +
            "            \"programUrl\" : \"http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v\",\n" +
            "            \"avAttributes\" : {\n" +
            "              \"avFileFormat\" : \"MP4\"\n" +
            "            },\n" +
            "            \"offset\" : 780000,\n" +
            "            \"duration\" : 600000,\n" +
            "            \"owner\" : \"BROADCASTER\",\n" +
            "            \"creationDate\" : 1457102700000,\n" +
            "            \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "          }, {\n" +
            "            \"programUrl\" : \"http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf\",\n" +
            "            \"avAttributes\" : {\n" +
            "              \"avFileFormat\" : \"WM\"\n" +
            "            },\n" +
            "            \"owner\" : \"BROADCASTER\",\n" +
            "            \"creationDate\" : 1457099100000,\n" +
            "            \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "          }, {\n" +
            "            \"programUrl\" : \"http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf\",\n" +
            "            \"avAttributes\" : {\n" +
            "              \"avFileFormat\" : \"WM\"\n" +
            "            },\n" +
            "            \"duration\" : 1833000,\n" +
            "            \"owner\" : \"BROADCASTER\",\n" +
            "            \"creationDate\" : 1457095500000,\n" +
            "            \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "          }, {\n" +
            "            \"programUrl\" : \"http://player.omroep.nl/?aflID=4393288\",\n" +
            "            \"avAttributes\" : {\n" +
            "              \"avFileFormat\" : \"HTML\"\n" +
            "            },\n" +
            "            \"owner\" : \"NEBO\",\n" +
            "            \"creationDate\" : 1457091900000,\n" +
            "            \"workflow\" : \"FOR_PUBLICATION\"\n" +
            "          } ]\n" +
            "        }\n" +
            "      } ]\n" +
            "    }\n" +
            "  } ]\n" +
            "}");

    }

    @Test
    public void testUnMarshal() throws IOException {
        PageSearchResult searchResultItems = Jackson2Mapper.getInstance().readValue(getClass().getResourceAsStream("/pageSearchResult.json"), PageSearchResult.class);
        log.info("{}", searchResultItems);

    }


}
