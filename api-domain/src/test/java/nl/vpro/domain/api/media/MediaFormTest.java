/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.extern.slf4j.Slf4j;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;

import java.io.IOException;
import java.io.StringReader;
import java.time.*;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;
import org.meeuw.theories.BasicObjectTheory;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.Channel;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.media.support.Tag;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.logging.LoggerOutputStream;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.Version;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static nl.vpro.test.util.jaxb.JAXBTestUtil.roundTripAndSimilar;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@Slf4j
public class MediaFormTest implements BasicObjectTheory<MediaForm> {

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
            """
                <api:mediaForm highlight="true" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                    <api:sortFields>
                        <api:sort order="DESC">sortDate</api:sort>
                        <api:sort order="ASC">title</api:sort>
                        <api:titleSort type="LEXICO" order="ASC" />
                    </api:sortFields>
                </api:mediaForm>"""
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
        MediaForm result = Jackson2TestUtil.roundTripAndSimilarAndEquals(in, """
            {
              "sort" : [ {
                "order" : "DESC",
                "field" : "sortDate"
              }, "title", {
                "order" : "ASC",
                "type" : "LEXICO",
                "field" : "title"
              } ],
              "highlight" : true
            }""");
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
        MediaForm result = Jackson2TestUtil.roundTripAndSimilarAndEquals(in, """
            {
              "sort" : {
                "sortDate" : "DESC",
                "creationDate" : "DESC"
              },
              "highlight" : true
            }""");
        assertThat(result.getSortFields()).hasSize(2);
        assertThat(result.getSortFields().get(0).getField()).isEqualTo(MediaSortField.sortDate);
        assertThat(result).isEqualTo(in);
        Compatibility.clearCompatibility();

    }


    @Test
    public void parseWithEmptySort() throws Exception {
        String example = """
            {
              "sort" : {
              }
            }
            """;
        MediaForm form = Jackson2Mapper.getInstance().readValue(new StringReader(example), MediaForm.class);
        assertThat(form.getSortFields()).isEmpty();
    }

    @Test
    public void testGetTags() {
        MediaForm in = MediaFormBuilder.form().tags(Match.SHOULD, new Tag("XML")).build();
        MediaForm out = roundTripAndSimilar(in,
            """
                <api:mediaForm  xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                    <api:searches>
                        <api:tags match="SHOULD">
                            <api:matcher match="SHOULD">XML</api:matcher>
                        </api:tags>
                    </api:searches>
                </api:mediaForm>"""
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
            """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <api:mediaForm xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                    <api:searches>
                        <api:scheduleEvents>
                            <api:begin>2015-01-26T00:00:00+01:00</api:begin>
                            <api:end>2015-01-27T00:00:00+01:00</api:end>
                            <api:channel>NED3</api:channel>
                        </api:scheduleEvents>
                    </api:searches>
                    <api:facets>
                        <api:broadcasters sort="VALUE_ASC">
                            <api:threshold>0</api:threshold>
                            <api:max>24</api:max>
                        </api:broadcasters>
                    </api:facets>
                </api:mediaForm>
                """);
        assertThat(out.getFacets().getBroadcasters().getSort()).isEqualTo(FacetOrder.VALUE_ASC);
    }

    @Test
    public void testGetFacetsBackwards() {
        MediaForm out = JAXB.unmarshal(new StringReader("""
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <mediaForm xmlns="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009" highlight="false">
                <searches>
                    <scheduleEvents inclusiveEnd="true">
                        <begin>2015-01-26T00:00:00+01:00</begin>
                        <end>2015-01-27T00:00:00+01:00</end>
                        <channel>NED3</channel>
                    </scheduleEvents>
                </searches>
                <facets>
                    <broadcasters sort="REVERSE_TERM">
                        <threshold>0</threshold>
                        <offset>0</offset>
                        <max>24</max>
                    </broadcasters>
                </facets>
            </mediaForm>"""), MediaForm.class);
        assertThat(out.getFacets().getBroadcasters().getSort()).isEqualTo(FacetOrder.VALUE_DESC);
    }


    @Test
    public void testFilterTags() throws IOException {
        String tagForm = """
            {
                "facets": {
                    "tags": {
                        "filter": {
                            "tags":  {
                                "matchType": "WILDCARD",
                                "match": "MUST",
                                "value": "Lief*"
                            }
                        },
                        "max": 10000
                    }
                },
                "searches": {
                    "tags": {
                        "matchType": "WILDCARD",
                        "match": "SHOULD",
                        "value": "Lief*"
                    }

                }
            }""";
        MediaForm form = Jackson2Mapper.getInstance().readValue(tagForm, MediaForm.class);
        assertThat(form.getSearches().getTags().get(0).getValue()).isEqualTo("Lief*");
        assertThat(form.getSearches().getTags().get(0).getMatch()).isEqualTo(Match.SHOULD);
        assertThat(form.getSearches().getTags().get(0).getMatchType().getName()).isEqualTo(StandardMatchType.WILDCARD.getName());
        assertThat(form.getFacets().getTags().getFilter().getTags().get(0).getMatch()).isEqualTo(Match.MUST);
        assertThat(form.getFacets().getTags().getFilter().getTags().get(0).getMatchType().getName()).isEqualTo(StandardMatchType.WILDCARD.getName());
    }

    @Test
    public void testSubSearch() {
        String example = """
            {
                "facets": {
                    "relations":  {
                        "subSearch": {
                            "broadcasters": "VPRO"
                        },
                        "value": [
                            {
                                "name": "labels",
                                "sort": "COUNT_DESC",
                                "max": 3,
                                "subSearch": {
                                    "types": "LABEL"
                                }
                            },
                            {
                                "name": "artiesten",
                                "sort": "COUNT_DESC",
                                "max": 3,
                                "subSearch": {
                                    "types": "ARTIST"
                                }
                            }
                        ]
                    }
                }
            }
            """;
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
        roundTripAndSimilar(form, """
            <api:mediaForm xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:searches>
                    <api:text fuzziness="AUTO" match="SHOULD">bla</api:text>
                </api:searches>
            </api:mediaForm>""");

        Jackson2TestUtil.roundTripAndSimilar(form, """
            {
              "searches" : {
                "text" : {
                  "value" : "bla",
                  "match" : "SHOULD",
                  "fuzziness" : "AUTO"
                }
              }
            }""");


    }


    @Test
    public void testTitleSearch() {
        MediaForm form = MediaForm
            .builder()
            .fuzzyText("bla")
            .build();
        roundTripAndSimilar(form, """
            <api:mediaForm xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:searches>
                    <api:text fuzziness="AUTO" match="SHOULD">bla</api:text>
                </api:searches>
            </api:mediaForm>""");

        Jackson2TestUtil.roundTripAndSimilar(form, """
            {
              "searches" : {
                "text" : {
                  "value" : "bla",
                  "match" : "SHOULD",
                  "fuzziness" : "AUTO"
                }
              }
            }""");


    }


    @Test
    public void testTitleSearch2() {
        MediaForm form = MediaForm
            .builder()
            .titles(TitleSearch.builder().type(TextualType.MAIN).value("Flikken").build())
            .build();
        roundTripAndSimilar(form, """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <api:mediaForm xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:searches>
                    <api:titles type="MAIN">Flikken</api:titles>
                </api:searches>
            </api:mediaForm>""");

        Jackson2TestUtil.roundTripAndSimilar(form, """
            {
              "searches" : {
                "titles" : [ {
                  "type" : "MAIN",
                  "value" : "Flikken"
                } ]
              }
            }""");


    }

    @Test
    public void testForm() throws IOException {
        MediaForm form = MediaForm.builder()
            .types(Match.MUST, TextMatcher.not("BROADCAST"))
            .fuzzyText("wie is de mol")
            .build();

        Jackson2Mapper.getPrettyInstance().writeValue(LoggerOutputStream.info(log), form);
    }

    private static final String LUNATIC_BACKWARD_COMPATIBLE = """
        <api:mediaForm xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
            <api:searches>
                <api:durations match="MUST">
                    <api:matcher inclusiveEnd="false">
                        <api:begin>1970-01-01T01:05:00.001+01:00</api:begin>
                        <api:end>1970-01-01T01:10:00+01:00</api:end>
                    </api:matcher>
                </api:durations>
            </api:searches>
            <api:facets>
                <api:durations>
                    <api:range>
                        <api:name>0-5m</api:name>
                        <api:begin>1970-01-01T01:00:00.001+01:00</api:begin>
                        <api:end>1970-01-01T01:05:00+01:00</api:end>
                    </api:range>
                    <api:range>
                        <api:name>5-10m</api:name>
                        <api:begin>1970-01-01T01:05:00.001+01:00</api:begin>
                        <api:end>1970-01-01T01:10:00+01:00</api:end>
                    </api:range>
                    <api:range>
                        <api:name>10m-30m</api:name>
                        <api:begin>1970-01-01T01:10:00.001+01:00</api:begin>
                        <api:end>1970-01-01T01:30:00+01:00</api:end>
                    </api:range>
                    <api:range>
                        <api:name>30m-60m</api:name>
                        <api:begin>1970-01-01T01:30:00.001+01:00</api:begin>
                        <api:end>1970-01-01T02:00:00+01:00</api:end>
                    </api:range>
                    <api:range>
                        <api:name>60m-∞</api:name>
                        <api:begin>1970-01-01T02:00:00.001+01:00</api:begin>
                        <api:end>1970-01-01T05:00:00+01:00</api:end>
                    </api:range>
                </api:durations>
            </api:facets>
        </api:mediaForm>
        """;
    @Test
    public void testDurations() throws IOException {
        String json = """
            {

                "searches" : {
                    "durations" : [ {
                        "begin" : 300001,
                        "end" : 600000
                    } ]
                },
                "facets" : {
                    "durations" : [ {
                        "name" : "0-5m",
                        "begin" : 1,
                        "end" : 300000
                    }, {
                        "name" : "5-10m",
                        "begin" : 300001,
                        "end" : 600000
                    }, {
                        "name" : "10m-30m",
                        "begin" : 600001,
                        "end" : 1800000
                    }, {
                        "name" : "30m-60m",
                        "begin" : 1800001,
                        "end" : 3600000
                    }, {
                        "name" : "60m-∞",
                        "begin" : 3600001,
                        "end" : 14400000
                    } ]
                }
            }
            """;

        MediaForm fromJson = Jackson2Mapper.getStrictInstance().readerFor(MediaForm.class).readValue(new StringReader(json));
        roundTripAndSimilar(fromJson, """
            <api:mediaForm xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:searches>
                    <api:durations match="MUST">
                        <api:matcher>
                            <api:begin>PT5M0.001S</api:begin>
                            <api:end>PT10M</api:end>
                        </api:matcher>
                    </api:durations>
                </api:searches>
                <api:facets>
                    <api:durations>
                        <api:range>
                            <api:name>0-5m</api:name>
                            <api:begin>PT0.001S</api:begin>
                            <api:end>PT5M</api:end>
                        </api:range>
                        <api:range>
                            <api:name>5-10m</api:name>
                            <api:begin>PT5M0.001S</api:begin>
                            <api:end>PT10M</api:end>
                        </api:range>
                        <api:range>
                            <api:name>10m-30m</api:name>
                            <api:begin>PT10M0.001S</api:begin>
                            <api:end>PT30M</api:end>
                        </api:range>
                        <api:range>
                            <api:name>30m-60m</api:name>
                            <api:begin>PT30M0.001S</api:begin>
                            <api:end>PT1H</api:end>
                        </api:range>
                        <api:range>
                            <api:name>60m-∞</api:name>
                            <api:begin>PT1H0.001S</api:begin>
                            <api:end>PT4H</api:end>
                        </api:range>
                    </api:durations>
                </api:facets>
            </api:mediaForm>
            """);
    }
    @Test
    public void testBackwards() {
        MediaForm form = JAXB.unmarshal(new StringReader(LUNATIC_BACKWARD_COMPATIBLE), MediaForm.class);
        assertThat(((DurationRangeFacetItem) form.getFacets().getDurations().getRanges().get(0)).getEnd()).isEqualTo(Duration.ofMinutes(5));
    }

    @Test
    public void testWithTitleFacet() throws IOException {
        MediaForm form = Jackson2Mapper.getInstance().readValue("""
            {
                "facets": {
                    "titles": [
                         {
                             "sort" : "COUNT_DESC",
                             "max" : 23
                         },
                        {
                            "name": "a",
                            "subSearch": {
                                "type": "MAIN",
                                "value": "a*",
                                "matchType": "WILDCARD"
                            }
                        },
                        {
                            "name": "b",
                            "subSearch": {
                                "type": "MAIN",
                                "value": "b*",
                                "matchType": "WILDCARD"
                            }
                        }
                    ]
                },
                "searches": {
                    "titles": [
                        {
                            "match": "SHOULD",
                            "type": "MAIN",
                            "value": "a*",
                            "matchType": "WILDCARD"
                        }
                    ]
                }
            }
            """,  MediaForm.class);
        assertThat(form.getFacets().getTitles()).isNotNull();
        assertThat(form.getFacets().getTitles().getMax()).isEqualTo(23);
        assertThat(form.getFacets().getTitles().getFacets()).hasSize(2);

    }

    @Test
    public void facetBroadcasters() {
        MediaForm builder = MediaForm.builder().broadcasterFacet(new MediaFacet()).build();
        Jackson2TestUtil.roundTripAndSimilar(builder, """
            {
              "facets" : {
                "broadcasters" : {
                  "sort" : "VALUE_ASC",
                  "max" : 24
                }
              }
            }""");
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

        String sortBackwards = """
            {
              "sort" : {
                "sortDate" : "DESC"
              }
            }""";

        MediaForm form = Jackson2Mapper.getLenientInstance().readValue(sortBackwards, MediaForm.class);
        assertThat(form.getSortFields()).containsExactly(MediaSortOrder.desc(MediaSortField.sortDate));

        Jackson2TestUtil.roundTripAndSimilar(MediaForm.builder().sortOrder(MediaSortOrder.desc(MediaSortField.sortDate)).build(), """
            {
              "sort" : {
                "sortDate" : "DESC"
              }
            }""");
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

    MediaForm rad1 =   MediaFormBuilder.form().scheduleEvents(ScheduleEventSearch.builder().channel(Channel.RAD1).begin(Instant.ofEpochMilli(0)).build()).build();
    MediaForm rad1_2 = MediaFormBuilder.form().scheduleEvents(ScheduleEventSearch.builder().channel(Channel.RAD1).begin(Instant.ofEpochMilli(1)).build()).build();
    MediaForm rad1_3 = MediaFormBuilder.form().scheduleEvents(ScheduleEventSearch.builder().channel(Channel.RAD1).begin(Instant.ofEpochMilli(0)).build()).build();
    MediaForm rad2 =   MediaFormBuilder.form().scheduleEvents(ScheduleEventSearch.builder().channel(Channel.RAD2).begin(Instant.ofEpochMilli(0)).build()).build();

    @Test
    public void API_593() {
        assertThat(rad1.equals(rad1_3)).isTrue();
        assertThat(rad1.equals(rad1_2)).isFalse();
        assertThat(rad1.equals(rad2)).isFalse();
    }

    @Test
    public void form() {
        MediaForm form = new MediaForm();
        MediaSearch search = MediaSearch.builder()
            .locations(new TextMatcherList(Match.MUST, TextMatcher.must("https://radiobox2.*", StandardMatchType.WILDCARD)))
            .build();
        form.setSearches(search);
        Jackson2TestUtil.roundTripAndSimilar(form, """
            {
              "searches" : {
                "locations" : {
                  "value" : "https://radiobox2.*",
                  "matchType" : "WILDCARD"
                }
              }
            }""");

    }


    @Test
    public void form2() {
        MediaForm form = new MediaForm();
        MediaSearch search = MediaSearch.builder()
            .types(new TextMatcherList(Match.MUST, TextMatcher.should("BROADCAST"), TextMatcher.should("CLIP")))
            .build();
        form.setSearches(search);
        Jackson2TestUtil.roundTripAndSimilar(form, """
            {
                 "searches" : {
                   "types" : [ {
                     "value" : "BROADCAST",
                     "match" : "SHOULD"
                   }, {
                     "value" : "CLIP",
                     "match" : "SHOULD"
                   } ]
                 }
               }""");

    }


    @Test
    public void formxml() {
        MediaForm form = new MediaForm();
        MediaSearch search = MediaSearch.builder()
            .locations(new TextMatcherList(Match.MUST, TextMatcher.must("https://radiobox2.*", StandardMatchType.WILDCARD)))
            .build();
        form.setSearches(search);
        JAXBTestUtil.roundTripAndSimilar(form, """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <api:mediaForm xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                  <api:searches>
                      <api:locations match="MUST">
                          <api:matcher matchType="WILDCARD">https://radiobox2.*</api:matcher>
                      </api:locations>
                  </api:searches>
            </api:mediaForm>
            """);

    }

    @Override
    public Arbitrary<MediaForm> datapoints() {
        return Arbitraries.of(
            MediaFormBuilder.emptyForm(),
            rad1,
            rad1_2,
            rad1_3,
            rad2
        );
    }
}
