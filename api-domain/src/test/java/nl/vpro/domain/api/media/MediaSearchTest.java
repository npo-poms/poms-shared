package nl.vpro.domain.api.media;

import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.domain.api.TextMatcher.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class MediaSearchTest {

    @Test
    public void testGetText() {
        MediaSearch in = new MediaSearch();
        in.setText(new SimpleTextMatcher("Title"));
        MediaSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:mediaSearch xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:text>Title</api:text>\n" +
                "</local:mediaSearch>");
        assertThat(must("Title")).isEqualTo(out.getText());
    }

    @Test
    public void testGetBroadcasters() {
        MediaSearch in = new MediaSearch();
        in.setBroadcasters(new TextMatcherList(must("VPRO"), must("TROS")));
        MediaSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<local:mediaSearch xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:broadcasters match=\"MUST\">\n" +
                "        <api:matcher>VPRO</api:matcher>\n" +
                "        <api:matcher>TROS</api:matcher>\n" +
                "    </api:broadcasters>\n" +
                "</local:mediaSearch>");
        assertThat(out.getBroadcasters().asList()).containsExactly(new TextMatcher("VPRO"), new TextMatcher("TROS"));
    }


    @Test
    public void testGetLocations() {
        MediaSearch in = new MediaSearch();
        in.setLocations(new TextMatcherList(new TextMatcher("http://some.domain.com/path"), new TextMatcher(".extension")));
        MediaSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:mediaSearch xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:locations match=\"MUST\">\n" +
                "        <api:matcher>http://some.domain.com/path</api:matcher>\n" +
                "        <api:matcher>.extension</api:matcher>\n" +
                "    </api:locations>\n" +
                "</local:mediaSearch>");
        assertThat(out.getLocations().asList()).containsExactly(new TextMatcher("http://some.domain.com/path"), new TextMatcher(".extension"));
    }

    @Test
    public void testGetTags() {
        MediaSearch in = new MediaSearch();
        in.setTags(new ExtendedTextMatcherList(new ExtendedTextMatcher("cultuur"), new ExtendedTextMatcher("kunst")));
        MediaSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:mediaSearch xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:tags match=\"MUST\">\n" +
                "        <api:matcher>cultuur</api:matcher>\n" +
                "        <api:matcher>kunst</api:matcher>\n" +
                "    </api:tags>\n" +
                "</local:mediaSearch>");
        assertThat(out.getTags().asList()).containsExactly(new ExtendedTextMatcher("cultuur"), new ExtendedTextMatcher("kunst"));
    }


    @Test
    public void testGetTypes() {
        MediaSearch in = new MediaSearch();
        in.setTypes(new TextMatcherList(Arrays.asList(new TextMatcher("A"), new TextMatcher("B", Match.SHOULD)), Match.SHOULD));
        MediaSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:mediaSearch xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:types match=\"SHOULD\">\n" +
                "        <api:matcher>A</api:matcher>\n" +
                "        <api:matcher match=\"SHOULD\">B</api:matcher>\n" +
                "    </api:types>\n" +
                "</local:mediaSearch>");
        assertThat(out.getTypes().asList()).containsExactly(new TextMatcher("A"), new TextMatcher("B", Match.SHOULD));

    }

    private static final TitleSearch TESTRESULT =
        TitleSearch.builder()
            .owner(OwnerType.PLUTO)
            .type(TextualType.LEXICO)
            .value("A")
            .build();

    @Test
    public void testTitleSearchJson() {
        MediaSearch in = new MediaSearch();
        in.setTitles(Arrays.asList(TESTRESULT));
        MediaSearch out = Jackson2TestUtil.roundTripAndSimilar(in, "{\n" +
            "  \"titles\" : [ {\n" +
            "    \"value\" : \"A\",\n" +
            "    \"owner\" : \"PLUTO\",\n" +
            "    \"type\" :\"LEXICO\"\n" +
            "  } ]\n" +
            "}");
        assertThat(out.getTitles()).containsExactly(TESTRESULT);

    }


    @Test
    public void testApplyText() {
        MediaSearch in = new MediaSearch();
        in.setText(new SimpleTextMatcher("title"));

        MediaObject object = new Program();
        assertThat(in.test(object)).isFalse();
        object.addTitle(new Title("main title", OwnerType.BROADCASTER, TextualType.MAIN));
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testApplyTypes() {
        MediaSearch in = new MediaSearch();
        in.setTypes(new TextMatcherList(should("SEASON"), should("SERIES")));

        {
            MediaObject object = new Group(GroupType.SERIES);
            assertThat(in.applyTypes(object).test()).isTrue();
        }
        {
            MediaObject object = new Group(GroupType.ALBUM);
            assertThat(in.applyTypes(object).test()).isFalse();
        }
    }

    @Test
    public void testApplyTypesWithNots() {
        MediaSearch in = new MediaSearch();
        in.setTypes(new TextMatcherList(
            not("SEASON"),
            not("SERIES")
        ));

        {
            MediaObject object = new Group(GroupType.SERIES);
            assertThat(in.applyTypes(object).test()).isFalse();
        }
        {
            MediaObject object = new Group(GroupType.ALBUM);
            assertThat(in.applyTypes(object).test()).isTrue();
        }
    }

    @Test
    public void testApplyIncludeSortDates() {
        DateRangeMatcher range = new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(10));

        MediaSearch search = new MediaSearch();
        search.setSortDates(new DateRangeMatcherList(range));

        {
            MediaObject object = MediaTestDataBuilder.program().creationInstant(Instant.ofEpochMilli(5)).build();
            assertThat(search.test(object)).isTrue();
        }

        {
            MediaObject object = MediaTestDataBuilder.program().creationInstant(Instant.ofEpochMilli(15)).build();
            assertThat(search.test(object)).isFalse();
        }
    }

    @Test
    public void testApplyExcludeSortDates() {
        DateRangeMatcher range = new DateRangeMatcher(Instant.EPOCH, Instant.ofEpochMilli(10));
        range.setMatch(Match.NOT);

        MediaSearch search = new MediaSearch();
        search.setSortDates(new DateRangeMatcherList(range));

        {
            MediaObject object = MediaTestDataBuilder.program().creationInstant(Instant.ofEpochMilli(5)).build();
            assertThat(search.test(object)).isFalse();
        }

        {
            MediaObject object = MediaTestDataBuilder.program().creationInstant(Instant.ofEpochMilli(15)).build();
            assertThat(search.test(object)).isTrue();
        }
    }

    @Test
    public void testApplyBroadcasters() {

    }

    @Test
    public void testApplyIncludeMediaIds() {
        MediaSearch in = new MediaSearch();
        in.setMediaIds(new TextMatcherList(should("urn:vpro:media:program:1"), should("SOME_MID")));

        {
            MediaObject object = new Program(2L);
            assertThat(in.test(object)).isFalse();
        }

        {
            MediaObject object = new Program();
            object.setMid("SOME_MID");
            assertThat(in.test(object)).isTrue();
        }

        {
            MediaObject object = new Program();
            object.setMid("SOME_OTHER_MID");
            assertThat(in.test(object)).isFalse();
        }
    }

    @Test
    public void testApplyExcludeMediaIds() {
        MediaSearch in = new MediaSearch();
        in.setMediaIds(new TextMatcherList(new TextMatcher("urn:vpro:media:program:1", Match.NOT), new TextMatcher("SOME_MID", Match.NOT)));

        {
            MediaObject object = new Program(2L);
            assertThat(in.test(object)).isTrue();
        }
        {
            MediaObject object = new Program();
            object.setMid("SOME_MID");
            assertThat(in.test(object)).isFalse();
        }
        {
            MediaObject object = new Program();
            object.setMid("SOME_OTHER_MID");
            assertThat(in.test(object)).isTrue();
        }
    }

    @Test
    public void testApplyTags() {
        MediaSearch in = new MediaSearch();
        in.setTags(new ExtendedTextMatcherList(
            ExtendedTextMatcher.should("cultuur"),
            ExtendedTextMatcher.should("kunst"))
        );

        MediaObject object = new Program();
        assertThat(in.test(object)).isFalse();
        object.setTags(new TreeSet<>(Collections.singletonList(new Tag("cultuur"))));
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testApplyIncludeDurations() {
        DurationRangeMatcher range = new DurationRangeMatcher(Duration.ofMillis(0), Duration.ofMillis(10));

        MediaSearch search = new MediaSearch();
        search.setDurations(new DurationRangeMatcherList(range));

        {
            MediaObject object = MediaTestDataBuilder.program().duration(Duration.ofMillis(5)).build();
            assertThat(search.test(object)).isTrue();
        }

        {
            MediaObject object = MediaTestDataBuilder.program().duration(Duration.ofMillis(15)).build();
            assertThat(search.test(object)).overridingErrorMessage("15 MUST lie between 0 and 10").isFalse();
        }
    }

    @Test
    public void testApplyExcludeDurations() {
        DurationRangeMatcher range = new DurationRangeMatcher(Duration.ofMillis(0), Duration.ofMillis(10));
        range.setMatch(Match.NOT);

        MediaSearch search = new MediaSearch();
        search.setDurations(new DurationRangeMatcherList(range));

        {
            MediaObject object = MediaTestDataBuilder.program().duration(Duration.ofMillis(5)).build();
            assertThat(search.test(object)).isFalse();
        }

        {
            MediaObject object = MediaTestDataBuilder.program().duration(Duration.ofMillis(15)).build();
            assertThat(search.test(object)).isTrue();
        }
    }


    @Test
    public void testApplyNotDescendantOfOnEmpty() {
        MediaSearch search = new MediaSearch();
        search.setDescendantOf(new TextMatcherList(not("MID")));
        MediaObject object = MediaTestDataBuilder.program().build();
        assertThat(search.test(object)).isTrue();
    }

    @Test
    public void testApplyNotDescendantOf() {
        MediaSearch search = new MediaSearch();
        search.setDescendantOf(new TextMatcherList(not("MID")));
        MediaObject object = MediaTestDataBuilder.program()
            .descendantOf(new DescendantRef("MID", "urn", MediaType.ALBUM))
            .build();
        assertThat(search.test(object)).isFalse();
    }

    private static final ScheduleEventSearch AT_NED1 = ScheduleEventSearch.builder().channel(Channel.NED1).build();
    private static final ScheduleEventSearch AT_NED2 = ScheduleEventSearch.builder().channel(Channel.NED2).begin(Instant.EPOCH).build();
    private static final ScheduleEventSearch FROM_EPOCH = ScheduleEventSearch.builder().begin(Instant.EPOCH).build();

    @Test
    public void testScheduleEventSearchXml() {
        MediaSearch in = new MediaSearch();
        in.setScheduleEvents(Arrays.asList(AT_NED1, AT_NED2));
        MediaSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<local:mediaSearch xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:scheduleEvents>\n" +
                "        <api:channel>NED1</api:channel>\n" +
                "    </api:scheduleEvents>\n" +
                "     <api:scheduleEvents>\n" +
                "       <api:begin>1970-01-01T01:00:00+01:00</api:begin>\n" +
                "       <api:channel>NED2</api:channel>\n" +
                "   </api:scheduleEvents>\n"+
                "</local:mediaSearch>");
        assertThat(out.getScheduleEvents()).containsExactly(AT_NED1, AT_NED2);

    }
   @Test
    public void testScheduleEventSearchJson() {
        MediaSearch in = new MediaSearch();
        in.setScheduleEvents(Arrays.asList(AT_NED1));
        MediaSearch out = Jackson2TestUtil.roundTripAndSimilar(in, "{\n" +
            "  \"scheduleEvents\" : {\n" +
            "    \"channel\" : \"NED1\"\n" +
            "  }\n" +
            "}");
       assertThat(out.getScheduleEvents()).containsExactly(AT_NED1);

    }

    @Test
    public void testScheduleEventSearchJson2() {
        MediaSearch in = new MediaSearch();
        in.setScheduleEvents(Arrays.asList(AT_NED1, AT_NED2));
        MediaSearch out = Jackson2TestUtil.roundTripAndSimilar(in, "{\n" +
            "  \"scheduleEvents\" : [ {\n" +
            "    \"channel\" : \"NED1\"\n" +
            "  }, {\n" +
            "    \"begin\" : 0,\n" +
            "    \"channel\" : \"NED2\"\n" +
            "  } ]\n" +
            "}");
        assertThat(out.getScheduleEvents()).containsExactly(AT_NED1, AT_NED2);

    }

    @Test
    public void testScheduleEventSearchJsonLenientChannel() throws Exception {


        ScheduleEventSearch search = Jackson2Mapper.getLenientInstance().readerFor(ScheduleEventSearch.class).readValue(new StringReader("{'begin': 0, 'channel': 'x'}"));
        assertThat(search).isEqualTo(FROM_EPOCH);
        MediaSearch out = Jackson2Mapper.getLenientInstance().readerFor(MediaSearch.class).readValue(new StringReader("{\n" +
            "  \"scheduleEvents\" : [ {\n" +
            "    \"channel\" : \"NED1\"\n" +
            "  }, {\n" +
            "    \"begin\" : 0,\n" +
            "    \"channel\" : \"\"\n" +
            "  } ]\n" +
            "}"));
        assertThat(out.getScheduleEvents()).containsExactly(AT_NED1, FROM_EPOCH);

    }
  /*  @Test
    public void testApplyRelations() {
        MediaSearch in = new MediaSearch();
        in.setRelations(Arrays.asList(new RelationDefinitionSearch(
                new RelationDefinition("LABEL", "VPRO"),
                new TextMatcherList(Match.MUST, new TextMatcher("V3"), new TextMatcher("V2", Match.NOT)))));

        {
            MediaObject object = new Program();
            object.addRelation(new Relation(new RelationDefinition("LABEL", "VPRO"), "http://www.v3.com", "V3"));
            assertThat(in.testRelations(object)).isTrue();
            object.addRelation(new Relation(new RelationDefinition("LABEL", "VPRO"), "http://www.v3.com", "V2"));
            assertThat(in.testRelations(object)).isFalse();
        }
        {
            MediaObject object = new Group(GroupType.ALBUM);
            assertThat(in.testRelations(object)).isTrue();
        }
    }
*/

    @Test
    public void hasNoSearches() {
        assertThat(new MediaSearch().hasSearches()).isFalse();
    }

    @Test
    public void hasSearchesText() {
        MediaSearch search = new MediaSearch();
        search.setText(SimpleTextMatcher.should("bla"));
        assertThat(search.hasSearches()).isTrue();
    }

    @Test
    public void hasSearchesIds() {
        MediaSearch search = new MediaSearch();
        search.setMediaIds(TextMatcherList.must(should("bla")));
        assertThat(search.hasSearches()).isTrue();
    }

    @Test
    public void withDateRangeAsString() throws JsonProcessingException {
        String json = "{\n" +
            "    \"sort\":{\n" +
            "        \"sortDate\":\"DESC\"\n" +
            "    },\n" +
            "    \"searches\": {\n" +
            "        \"sortDate\": {\n" +
            "            \"end\": \"1444255200000\"\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        MediaForm form = Jackson2Mapper.getLenientInstance().readValue(json, MediaForm.class);
        assertThat(form.getSearches().getSortDates().get(0).getEnd()).isEqualTo(Instant.ofEpochMilli(1444255200000L));
    }
}

