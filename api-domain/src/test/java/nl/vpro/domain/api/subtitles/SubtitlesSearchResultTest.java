package nl.vpro.domain.api.subtitles;

import java.time.Duration;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.api.SearchResultItem;
import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class SubtitlesSearchResultTest {

    @Test
    public void xml() {
        StandaloneCue cue1 = StandaloneCue.standaloneBuilder().content("bla").type(SubtitlesType.CAPTION).start(Duration.ofSeconds(10)).build();
        SearchResultItem<StandaloneCue> item = new SearchResultItem<>(cue1);
        SubtitlesSearchResult result = new SubtitlesSearchResult(Arrays.asList(item), 0L, 10, Result.Total.equalsTo(100L));
        JAXBTestUtil.roundTripAndSimilar(result, """
            <api:subtitlesSearchResult total="100" totalQualifier="EQUAL_TO" offset="0" max="10" xmlns="urn:vpro:media:2009" xmlns:subtitles="urn:vpro:media:subtitles:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:items>
                    <api:item xsi:type="api:searchResultItem" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                        <api:result xsi:type="subtitles:standaloneCue" type="CAPTION" sequence="0" identifier="0" start="P0DT0H0M10.000S">bla</api:result>
                    </api:item>
                </api:items>
            </api:subtitlesSearchResult>""");
    }

    @Test
    public void json() {
        StandaloneCue cue1 = StandaloneCue.standaloneBuilder().content("bla").type(SubtitlesType.CAPTION).start(Duration.ofSeconds(10)).build();
        SearchResultItem<StandaloneCue> item = new SearchResultItem<>(cue1);
        SubtitlesSearchResult result = new SubtitlesSearchResult(Arrays.asList(item), 0L, 10, Result.Total.equalsTo(100L));
        Jackson2TestUtil.roundTripAndSimilar(result, """
            {
              "total" : 100,
              "totalQualifier" : "EQUAL_TO",
              "offset" : 0,
              "max" : 10,
              "items" : [ {
                "result" : {
                  "objectType" : "StandaloneCue",
                  "sequence" : 0,
                   "identifier" : "0",
                  "start" : 10000,
                  "type" : "CAPTION",
                  "content" : "bla"
                }
              } ]
            }""");

    }
}
