/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.LocalDate;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.logging.LoggerOutputStream;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.util.Version;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static nl.vpro.test.util.jaxb.JAXBTestUtil.roundTripAndSimilar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertNotNull;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@Slf4j
public class MediaFormTest {

    @Test
    public void testGetSort() {
        MediaForm in = new MediaForm();
        MediaSortOrderList list = new MediaSortOrderList();
        list.put(MediaSortField.sortDate, Order.DESC);
        list.put(MediaSortField.title, null);
        list.add(TitleSortOrder.builder().textualType(TextualType.LEXICO).build());
        in.setHighlight(true);
        in.setSortFields(list);
        MediaForm out = roundTripAndSimilar(in,
            "<api:mediaForm highlight=\"true\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <api:sortFields>\n" +
                "        <api:sort order=\"DESC\">sortDate</api:sort>\n" +
                "        <api:sort order=\"ASC\">title</api:sort>\n" +
                "        <api:titleSort type=\"LEXICO\" order=\"ASC\" />\n" +
                "    </api:sortFields>\n" +
                "</api:mediaForm>"
        );
        assertThat(out.getSortFields()).hasSize(3);
    }

    @Test
    public void testGetSortJson() {
        MediaForm in = new MediaForm();
        MediaSortOrderList list = new MediaSortOrderList();
        list.put(MediaSortField.sortDate, Order.DESC);
        list.put(MediaSortField.title, null);
        list.add(TitleSortOrder.builder().textualType(TextualType.LEXICO).build());
        in.setHighlight(true);
        in.setSortFields(list);
        MediaForm result = Jackson2TestUtil.roundTripAndSimilarAndEquals(in, "{\n" +
            "  \"sort\" : [ {\n" +
            "    \"order\" : \"DESC\",\n" +
            "    \"field\" : \"sortDate\"\n" +
            "  }, \"title\", {\n" +
            "    \"order\" : \"ASC\",\n" +
            "    \"type\" : \"LEXICO\",\n" +
            "    \"field\" : \"title\"\n" +
            "  } ],\n" +
            "  \"highlight\" : true\n" +
            "}");
        assertThat(result.getSortFields()).hasSize(3);
        assertThat(result.getSortFields().get(0).getField()).isEqualTo(MediaSortField.sortDate);
        assertThat(result).isEqualTo(in);

    }


    @Test
    public void testGetSortJsonBackward() {
        Compatibility.setCompatibility(Version.of(5, 4));
        MediaForm in = new MediaForm();
        MediaSortOrderList list = new MediaSortOrderList();
        list.put(MediaSortField.sortDate, Order.DESC);
        list.put(MediaSortField.creationDate, Order.DESC);
        in.setHighlight(true);
        in.setSortFields(list);
        MediaForm result = Jackson2TestUtil.roundTripAndSimilarAndEquals(in, "{\n" +
            "  \"sort\" : {\n" +
            "    \"sortDate\" : \"DESC\",\n" +
            "    \"creationDate\" : \"DESC\"\n" +
            "  },\n" +
            "  \"highlight\" : true\n" +
            "}");
        assertThat(result.getSortFields()).hasSize(2);
        assertThat(result.getSortFields().get(0).getField()).isEqualTo(MediaSortField.sortDate);
        assertThat(result).isEqualTo(in);
        Compatibility.clearCompatibility();

    }


    @Test
    public void parseWithEmptySort() throws Exception {
        String example = "{\n" +
            "  \"sort\" : {\n" +
            "  }\n" +
            "}\n";
        MediaForm form = Jackson2Mapper.getInstance().readValue(new StringReader(example), MediaForm.class);
        assertThat(form.getSortFields()).isEmpty();
    }

    @Test
    public void testGetTags() {
        MediaForm in = MediaFormBuilder.form().tags(Match.SHOULD, new Tag("XML")).build();
        MediaForm out = roundTripAndSimilar(in,
                "<api:mediaForm  xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                    "    <api:searches>\n" +
                    "        <api:tags match=\"SHOULD\">\n" +
                    "            <api:matcher match=\"SHOULD\">XML</api:matcher>\n" +
                    "        </api:tags>\n" +
                    "    </api:searches>\n" +
                    "</api:mediaForm>"
        );
        assertThat(out.getSearches().getTags().size()).isEqualTo(1);
        assertThat(out.getSearches().getTags().get(0).getMatch()).isEqualTo(Match.SHOULD);
        assertThat(out.getSearches().getTags().get(0).getValue()).isEqualTo("XML");
    }

    @Test
    public void testGetFacets() {
        MediaForm in = MediaFormBuilder.form().broadcasterFacet().scheduleEvents(
            new ScheduleEventSearch(Channel.NED3,
                LocalDate.of(2015, 1, 26).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant(),
                LocalDate.of(2015, 1, 27).atStartOfDay().atZone(Schedule.ZONE_ID).toInstant())
        ).build();
        MediaForm out = roundTripAndSimilar(in,
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<api:mediaForm xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                "    <api:searches>\n" +
                "        <api:scheduleEvents>\n" +
                "            <api:begin>2015-01-26T00:00:00+01:00</api:begin>\n" +
                "            <api:end>2015-01-27T00:00:00+01:00</api:end>\n" +
                "            <api:channel>NED3</api:channel>\n" +
                "        </api:scheduleEvents>\n" +
                "    </api:searches>\n" +
                "    <api:facets>\n" +
                "        <api:broadcasters sort=\"VALUE_ASC\">\n" +
                "            <api:threshold>0</api:threshold>\n" +
                "            <api:max>24</api:max>\n" +
                "        </api:broadcasters>\n" +
                "    </api:facets>\n" +
                "</api:mediaForm>\n");
        assertThat(out.getFacets().getBroadcasters().getSort()).isEqualTo(FacetOrder.VALUE_ASC);
    }

    @Test
    public void testGetFacetsBackwards() {
        MediaForm out = JAXB.unmarshal(new StringReader("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<mediaForm xmlns=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" highlight=\"false\">\n" +
            "    <searches>\n" +
            "        <scheduleEvents inclusiveEnd=\"true\">\n" +
            "            <begin>2015-01-26T00:00:00+01:00</begin>\n" +
            "            <end>2015-01-27T00:00:00+01:00</end>\n" +
            "            <channel>NED3</channel>\n" +
            "        </scheduleEvents>\n" +
            "    </searches>\n" +
            "    <facets>\n" +
            "        <broadcasters sort=\"REVERSE_TERM\">\n" +
            "            <threshold>0</threshold>\n" +
            "            <offset>0</offset>\n" +
            "            <max>24</max>\n" +
            "        </broadcasters>\n" +
            "    </facets>\n" +
            "</mediaForm>"), MediaForm.class);
        assertThat(out.getFacets().getBroadcasters().getSort()).isEqualTo(FacetOrder.VALUE_DESC);
    }


    @Test
    public void testFilterTags() throws IOException {
        String tagForm = "{\n" +
            "    \"facets\": {\n" +
            "        \"tags\": {\n" +
            "            \"filter\": {\n" +
            "                \"tags\":  {\n" +
            "                    \"matchType\": \"WILDCARD\",\n" +
            "                    \"match\": \"MUST\",\n" +
            "                    \"value\": \"Lief*\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"max\": 10000\n" +
            "        }\n" +
            "    },\n" +
            "    \"searches\": {\n" +
            "        \"tags\": {\n" +
            "            \"matchType\": \"WILDCARD\",\n" +
            "            \"match\": \"SHOULD\",\n" +
            "            \"value\": \"Lief*\"\n" +
            "        }\n" +
            "\n" +
            "    }\n" +
            "}";
        MediaForm form = Jackson2Mapper.getInstance().readValue(tagForm, MediaForm.class);
        assertThat(form.getSearches().getTags().get(0).getValue()).isEqualTo("Lief*");
        assertThat(form.getSearches().getTags().get(0).getMatch()).isEqualTo(Match.SHOULD);
        assertThat(form.getSearches().getTags().get(0).getMatchType().getName()).isEqualTo(StandardMatchType.WILDCARD.getName());
        assertThat(form.getFacets().getTags().getFilter().getTags().get(0).getMatch()).isEqualTo(Match.MUST);
        assertThat(form.getFacets().getTags().getFilter().getTags().get(0).getMatchType().getName()).isEqualTo(StandardMatchType.WILDCARD.getName());
    }

    @Test
    public void testSubSearch() {
        String example = "{\n" +
            "    \"facets\": {\n" +
            "        \"relations\":  {\n" +
            "            \"subSearch\": {\n" +
            "                \"broadcasters\": \"VPRO\"\n" +
            "            },\n" +
            "            \"value\": [\n" +
            "                {\n" +
            "                    \"name\": \"labels\",\n" +
            "                    \"sort\": \"COUNT_DESC\",\n" +
            "                    \"max\": 3,\n" +
            "                    \"subSearch\": {\n" +
            "                        \"types\": \"LABEL\"\n" +
            "                    }\n" +
            "                },\n" +
            "                {\n" +
            "                    \"name\": \"artiesten\",\n" +
            "                    \"sort\": \"COUNT_DESC\",\n" +
            "                    \"max\": 3,\n" +
            "                    \"subSearch\": {\n" +
            "                        \"types\": \"ARTIST\"\n" +
            "                    }\n" +
            "                }\n" +
            "            ]\n" +
            "        }\n" +
            "    }\n" +
            "}\n";
        MediaForm form = assertThatJson(MediaForm.class, example).isSimilarTo(example).get();
        assertThat(form.getFacets()).isNotNull();
        assertThat(form.getFacets().getRelations()).isNotNull();
        assertNotNull(form.getFacets().getRelations().getSubSearch());
        assertNotNull(form.getFacets().getRelations().getSubSearch().getBroadcasters());
        assertThat(form.getFacets().getRelations().getSubSearch().getBroadcasters().getMatchers().get(0).getValue()).isEqualTo("VPRO");

        assertThat(form.getFacets().getRelations().getFacets()).hasSize(2);
        assertNotNull(form.getFacets().getRelations().getFacets().get(0).getSubSearch());
        assertThat(form.getFacets().getRelations().getFacets().get(0).getSubSearch().getTypes().get(0).getValue()).isEqualTo("LABEL");


    }

    @Test
    public void testFuzzinessBinding() {
        MediaForm form = MediaForm.builder().fuzzyText("bla").build();
        roundTripAndSimilar(form, "<api:mediaForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:searches>\n" +
            "        <api:text fuzziness=\"AUTO\" match=\"SHOULD\">bla</api:text>\n" +
            "    </api:searches>\n" +
            "</api:mediaForm>");

        Jackson2TestUtil.roundTripAndSimilar(form, "{\n" +
            "  \"searches\" : {\n" +
            "    \"text\" : {\n" +
            "      \"value\" : \"bla\",\n" +
            "      \"match\" : \"SHOULD\",\n" +
            "      \"fuzziness\" : \"AUTO\"\n" +
            "    }\n" +
            "  }\n" +
            "}");


    }


    @Test
    public void testTitleSearch() {
        MediaForm form = MediaForm
            .builder()
            .fuzzyText("bla")
            .build();
        roundTripAndSimilar(form, "<api:mediaForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:searches>\n" +
            "        <api:text fuzziness=\"AUTO\" match=\"SHOULD\">bla</api:text>\n" +
            "    </api:searches>\n" +
            "</api:mediaForm>");

        Jackson2TestUtil.roundTripAndSimilar(form, "{\n" +
            "  \"searches\" : {\n" +
            "    \"text\" : {\n" +
            "      \"value\" : \"bla\",\n" +
            "      \"match\" : \"SHOULD\",\n" +
            "      \"fuzziness\" : \"AUTO\"\n" +
            "    }\n" +
            "  }\n" +
            "}");


    }


    @Test
    public void testTitleSearch2() {
        MediaForm form = MediaForm
            .builder()
            .titles(TitleSearch.builder().type(TextualType.MAIN).value("Flikken").build())
            .build();
        roundTripAndSimilar(form, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<api:mediaForm xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:searches>\n" +
            "        <api:titles type=\"MAIN\">Flikken</api:titles>\n" +
            "    </api:searches>\n" +
            "</api:mediaForm>");

        Jackson2TestUtil.roundTripAndSimilar(form, "{\n" +
            "  \"searches\" : {\n" +
            "    \"titles\" : [ {\n" +
            "      \"type\" : \"MAIN\",\n" +
            "      \"value\" : \"Flikken\"\n" +
            "    } ]\n" +
            "  }\n" +
            "}");


    }

    @Test
    public void testForm() throws IOException {
        MediaForm form = MediaForm.builder()
            .types(Match.MUST, TextMatcher.not("BROADCAST"))
            .fuzzyText("wie is de mol")
            .build();

        Jackson2Mapper.getPrettyInstance().writeValue(LoggerOutputStream.info(log), form);

    }

    private static final String LUNATIC_BACKWARD_COMPATIBLE = "<api:mediaForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
        "    <api:searches>\n" +
        "        <api:durations match=\"MUST\">\n" +
        "            <api:matcher inclusiveEnd=\"false\">\n" +
        "                <api:begin>1970-01-01T01:05:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T01:10:00+01:00</api:end>\n" +
        "            </api:matcher>\n" +
        "        </api:durations>\n" +
        "    </api:searches>\n" +
        "    <api:facets>\n" +
        "        <api:durations>\n" +
        "            <api:range>\n" +
        "                <api:name>0-5m</api:name>\n" +
        "                <api:begin>1970-01-01T01:00:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T01:05:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "            <api:range>\n" +
        "                <api:name>5-10m</api:name>\n" +
        "                <api:begin>1970-01-01T01:05:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T01:10:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "            <api:range>\n" +
        "                <api:name>10m-30m</api:name>\n" +
        "                <api:begin>1970-01-01T01:10:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T01:30:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "            <api:range>\n" +
        "                <api:name>30m-60m</api:name>\n" +
        "                <api:begin>1970-01-01T01:30:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T02:00:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "            <api:range>\n" +
        "                <api:name>60m-∞</api:name>\n" +
        "                <api:begin>1970-01-01T02:00:00.001+01:00</api:begin>\n" +
        "                <api:end>1970-01-01T05:00:00+01:00</api:end>\n" +
        "            </api:range>\n" +
        "        </api:durations>\n" +
        "    </api:facets>\n" +
        "</api:mediaForm>\n";
    @Test
    public void testDurations() throws IOException {
        String json = "{\n" +
            "\n" +
            "    \"searches\" : {\n" +
            "        \"durations\" : [ {\n" +
            "            \"begin\" : 300001,\n" +
            "            \"end\" : 600000\n" +
            "        } ]\n" +
            "    },\n" +
            "    \"facets\" : {\n" +
            "        \"durations\" : [ {\n" +
            "            \"name\" : \"0-5m\",\n" +
            "            \"begin\" : 1,\n" +
            "            \"end\" : 300000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        }, {\n" +
            "            \"name\" : \"5-10m\",\n" +
            "            \"begin\" : 300001,\n" +
            "            \"end\" : 600000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        }, {\n" +
            "            \"name\" : \"10m-30m\",\n" +
            "            \"begin\" : 600001,\n" +
            "            \"end\" : 1800000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        }, {\n" +
            "            \"name\" : \"30m-60m\",\n" +
            "            \"begin\" : 1800001,\n" +
            "            \"end\" : 3600000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        }, {\n" +
            "            \"name\" : \"60m-∞\",\n" +
            "            \"begin\" : 3600001,\n" +
            "            \"end\" : 14400000,\n" +
            "            \"inclusiveEnd\" : true\n" +
            "        } ]\n" +
            "    }\n" +
            "}\n";

        MediaForm fromJson = Jackson2Mapper.STRICT.readerFor(MediaForm.class).readValue(new StringReader(json));
        roundTripAndSimilar(fromJson, "<api:mediaForm xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:searches>\n" +
            "        <api:durations match=\"MUST\">\n" +
            "            <api:matcher>\n" +
            "                <api:begin>PT5M0.001S</api:begin>\n" +
            "                <api:end>PT10M</api:end>\n" +
            "            </api:matcher>\n" +
            "        </api:durations>\n" +
            "    </api:searches>\n" +
            "    <api:facets>\n" +
            "        <api:durations>\n" +
            "            <api:range>\n" +
            "                <api:name>0-5m</api:name>\n" +
            "                <api:begin>PT0.001S</api:begin>\n" +
            "                <api:end>PT5M</api:end>\n" +
            "            </api:range>\n" +
            "            <api:range>\n" +
            "                <api:name>5-10m</api:name>\n" +
            "                <api:begin>PT5M0.001S</api:begin>\n" +
            "                <api:end>PT10M</api:end>\n" +
            "            </api:range>\n" +
            "            <api:range>\n" +
            "                <api:name>10m-30m</api:name>\n" +
            "                <api:begin>PT10M0.001S</api:begin>\n" +
            "                <api:end>PT30M</api:end>\n" +
            "            </api:range>\n" +
            "            <api:range>\n" +
            "                <api:name>30m-60m</api:name>\n" +
            "                <api:begin>PT30M0.001S</api:begin>\n" +
            "                <api:end>PT1H</api:end>\n" +
            "            </api:range>\n" +
            "            <api:range>\n" +
            "                <api:name>60m-∞</api:name>\n" +
            "                <api:begin>PT1H0.001S</api:begin>\n" +
            "                <api:end>PT4H</api:end>\n" +
            "            </api:range>\n" +
            "        </api:durations>\n" +
            "    </api:facets>\n" +
            "</api:mediaForm>\n");
    }
    @Test
    public void testBackwards() {
        MediaForm form = JAXB.unmarshal(new StringReader(LUNATIC_BACKWARD_COMPATIBLE), MediaForm.class);
        assertThat(((DurationRangeFacetItem) form.getFacets().getDurations().getRanges().get(0)).getEnd()).isEqualTo(Duration.ofMinutes(5));
    }

    @Test
    public void testWithTitleFacet() throws IOException {
        MediaForm form = Jackson2Mapper.getInstance().readValue("{\n" +
            "    \"facets\": {\n" +
            "        \"titles\": [\n" +
            "             {\n" +
            "                 \"sort\" : \"COUNT_DESC\",\n" +
            "                 \"max\" : 23\n" +
            "             },\n" +
            "            {\n" +
            "                \"name\": \"a\",\n" +
            "                \"subSearch\": {\n" +
            "                    \"type\": \"MAIN\",\n" +
            "                    \"value\": \"a*\",\n" +
            "                    \"matchType\": \"WILDCARD\"\n" +
            "                }\n" +
            "            },\n" +
            "            {\n" +
            "                \"name\": \"b\",\n" +
            "                \"subSearch\": {\n" +
            "                    \"type\": \"MAIN\",\n" +
            "                    \"value\": \"b*\",\n" +
            "                    \"matchType\": \"WILDCARD\"\n" +
            "                }\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"searches\": {\n" +
            "        \"titles\": [\n" +
            "            {\n" +
            "                \"match\": \"SHOULD\",\n" +
            "                \"type\": \"MAIN\",\n" +
            "                \"value\": \"a*\",\n" +
            "                \"matchType\": \"WILDCARD\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}\n",  MediaForm.class);
        assertThat(form.getFacets().getTitles()).isNotNull();
        assertThat(form.getFacets().getTitles().getMax()).isEqualTo(23);
        assertThat(form.getFacets().getTitles().getFacets()).hasSize(2);

    }

    @Test
    public void facetBroadcasters() {
        MediaForm builder = MediaForm.builder().broadcasterFacet(new MediaFacet()).build();
        Jackson2TestUtil.roundTripAndSimilar(builder, "{\n" +
            "  \"facets\" : {\n" +
            "    \"broadcasters\" : {\n" +
            "      \"sort\" : \"VALUE_ASC\",\n" +
            "      \"max\" : 24\n" +
            "    }\n" +
            "  }\n" +
            "}");
    }

    @Test
    public void withEverythingJson() {
        assertThatCode(() -> {
            Jackson2Mapper.getPrettyInstance()
                .writeValue(LoggerOutputStream.info(log), MediaForm.builder().withEverything().build());
        }).doesNotThrowAnyException();
    }


    @Test
    public void withEverythingXml() {
        assertThatCode(() -> {
            JAXB.marshal(MediaForm.builder().withEverything().build(), System.out);
        }).doesNotThrowAnyException();
    }


    @Test
    public void withSort() throws Exception {

        String sortBackwards = "{\n" +
            "  \"sort\" : {\n" +
            "    \"sortDate\" : \"DESC\"\n" +
            "  }\n" +
            "}";

        MediaForm form = Jackson2Mapper.getLenientInstance().readValue(sortBackwards, MediaForm.class);
        assertThat(form.getSortFields()).containsExactly(MediaSortOrder.desc(MediaSortField.sortDate));

        Jackson2TestUtil.roundTripAndSimilar(MediaForm.builder().sortOrder(MediaSortOrder.desc(MediaSortField.sortDate)).build(), "{\n" +
            "  \"sort\" : {\n" +
            "    \"sortDate\" : \"DESC\"\n" +
            "  }\n" +
            "}");
    }


    @Test
    public void listAsSingural() throws JsonProcessingException {
        String example = "{\n" +
            "  \"searches\" : {\n" +
            "    \"descendantOf\": {\n" +
            // a bit confusing what this meann, is match on the list or on the value!
            "      \"match\": \"MUST\",\n" +
            "      \"value\": \"POMS_S_MAX_059936\"\n" +
            "    }\n" +
            "  }\n" +
            "}";
        MediaForm form = Jackson2Mapper.getLenientInstance().readValue(example, MediaForm.class);
        assertThat(form.getSearches().getDescendantOf().getMatch()).isEqualTo(Match.MUST);
        assertThat(form.getSearches().getDescendantOf().get(0).getMatch()).isEqualTo(Match.MUST);
        log.info("{}", form);

    }

}
