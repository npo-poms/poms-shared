package nl.vpro.domain.api.page;

import java.io.*;
import java.time.LocalDateTime;

import jakarta.xml.bind.JAXB;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.transform.stream.StreamSource;

import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import com.fasterxml.jackson.databind.DeserializationFeature;

import nl.vpro.api.util.ApiMappings;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.api.*;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.page.LinkType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static nl.vpro.test.util.jaxb.JAXBTestUtil.assertThatXml;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class PageFormTest {

    @Test
    public void testGetText() {
        PageForm form = new PageForm();
        PageSearch pageSearch = new PageSearch();
        pageSearch.setText(new SimpleTextMatcher("bla bla"));
        form.setSearches(pageSearch);
        assertEquals("bla bla", form.getSearches().getText().getValue());
    }

    @Test
    public void testMarshal() throws IOException {

        PageFormBuilder builder = PageFormBuilder.form().sectionFacet().addPortals("WETENSCHAP24");
        StringWriter writer = new StringWriter();
        Jackson2Mapper.getInstance().writeValue(writer, builder.build());
        assertThatJson(writer.toString())
            .isSimilarTo("{\"searches\":{\"portals\":\"WETENSCHAP24\"},\"facets\":{\"sections\":{\"sort\":\"COUNT_DESC\",\"max\":24}}}");
    }

    @Test
    public void testMarshalWithSort() throws IOException {

        PageFormBuilder builder = PageFormBuilder.form()
            .addSortField("lastModified", Order.DESC)
            .sortDate(null, LocalDateTime.of(2017, 6, 12, 15, 0).atZone(Schedule.ZONE_ID).toInstant());
        StringWriter writer = new StringWriter();
        Jackson2Mapper.getInstance().writeValue(writer, builder.build());
        System.out.println(writer.toString());
        assertThatJson(writer.toString())
            .isEqualTo("{\"searches\":{\"sortDates\":[{\"end\":1497272400000,\"inclusiveEnd\":false}]},\"sort\":{\"lastModified\":\"DESC\"}}");
    }


    @Test
    public void testMarshalWithSortXml() {

        PageFormBuilder builder = PageFormBuilder.form()
            .addSortField("lastModified", Order.DESC)
            .sortDate(null, LocalDateTime.of(2017, 6, 12, 15, 0).atZone(Schedule.ZONE_ID).toInstant());
        StringWriter writer = new StringWriter();
        JAXB.marshal(builder.build(), writer);
        System.out.println(writer.toString());
        assertThatXml(writer.toString())
            .isSimilarTo("""
                <api:pagesForm xmlns:pages="urn:vpro:pages:2013" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                    <api:searches>
                        <api:sortDates match="MUST">
                            <api:matcher inclusiveEnd="false">
                                <api:end>2017-06-12T15:00:00+02:00</api:end>
                            </api:matcher>
                        </api:sortDates>
                    </api:searches>
                    <api:sortFields>
                        <api:sort order="DESC">lastModified</api:sort>
                    </api:sortFields>
                </api:pagesForm>""");
    }
    @Test
    public void unmarshal() throws IOException {
        String string = """
            {
                "facets": {
                    "sections": {
                        }
                },
                "highlight": true,
                "searches": {
                 "sortDates": { "begin": 0}
                }
            }""";

        PageForm form  = Jackson2Mapper.getInstance().readValue(new StringReader(string), PageForm.class);

        assertThat(form.getSearches().getSortDates().asList()).hasSize(1);
    }


    @Test
    public void unmarshalBackwards() throws IOException {
        String string = """
            {
                "facets": {
                    "sections": {
                        "sort": "COUNT"
                        }
                }
            }""";

        PageForm form = Jackson2Mapper.getInstance().readValue(new StringReader(string), PageForm.class);

        assertThat(form.getFacets().getSections().getSort()).isEqualTo(FacetOrder.COUNT_DESC);
    }

    @Test
    public void toXml() throws IOException {
        String json = """
            {
                "searches" : {

                },

             "sort": {"sortDate": "ASC" },
                "facets" : {
                  "sortDates" : [ "YEAR" ]
                }
            }
            """;
        PageForm form = Jackson2Mapper.getInstance().readValue(new StringReader(json), PageForm.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(form, out);
        String expected =
            """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <api:pagesForm xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                    <api:searches/>
                    <api:sortFields>
                        <api:sort order="ASC">sortDate</api:sort>
                    </api:sortFields>
                    <api:facets>
                        <api:sortDates>
                            <api:interval>YEAR</api:interval>
                        </api:sortDates>
                    </api:facets>
                </api:pagesForm>""";

        System.out.println(out.toString());
        Diff diff = DiffBuilder.compare(expected).withTest(out.toString()).build();
        if (diff.hasDifferences()) {
            assertThat(out.toString()).isEqualTo(expected);
        }

    }
    @Test
    public void toXmlValidated() throws IOException, JAXBException {
        String json = "{\"searches\":{\"types\":[\"PLAYER\"]},\"sort\":{\"sortDate\":\"DESC\"},\"facets\":{\"keywords\":{\"threshold\":0,\"sort\":\"COUNT_DESC\",\"offset\":0,\"max\":24},\"genres\":{\"threshold\":0,\"sort\":\"COUNT_DESC\",\"offset\":0,\"max\":24},\"sections\":{\"threshold\":0,\"sort\":\"COUNT_DESC\",\"offset\":0,\"max\":24}},\"mediaForm\":{\"facets\":{\"avTypes\":{\"threshold\":0,\"sort\":\"COUNT_DESC\",\"offset\":0,\"max\":24}},\"highlight\":false},\"highlight\":false}";
        PageForm form = Jackson2Mapper.getInstance().readValue(new StringReader(json), PageForm.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(form, out);
        System.out.println(out.toString());
        ApiMappings mappings = new ApiMappings(null);
        PageForm validated = (PageForm) mappings.getUnmarshaller(true, Xmlns.API_NAMESPACE).get().unmarshal(new StreamSource(new ByteArrayInputStream(out.toByteArray())));
    }

    @Test
    public void testReferralsJson() {
        PageForm form = PageFormBuilder.form().referrals(AssociationSearch.of(LinkType.TOP_STORY)).build();
        String json = "{\"searches\":{\"referrals\":{\"types\":\"TOP_STORY\"}}}";

        PageForm rounded = Jackson2TestUtil.roundTripAndSimilar(form, json);

        assertThat(rounded.getSearches().getReferrals().asList().get(0).getTypes().asList().get(0).getValue()).isEqualTo("TOP_STORY");
    }


    @Test
    public void testReferralsJsonShould() {
        PageForm form = PageFormBuilder.form().referrals(AssociationSearch.of(LinkType.TOP_STORY, Match.SHOULD)).build();
        String json = "{\"searches\":{\"referrals\":{\"match\":\"SHOULD\",\"types\":\"TOP_STORY\"}}}";

        PageForm rounded = Jackson2TestUtil.roundTripAndSimilar(form, json);

        assertThat(rounded.getSearches().getReferrals().asList().get(0).getTypes().asList().get(0).getValue()).isEqualTo("TOP_STORY");
    }


    @Test
    public void testReferralsXml() {
        PageForm form = PageFormBuilder.form().referrals(AssociationSearch.of(LinkType.TOP_STORY)).build();
        String xml = """
            <api:pagesForm xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:searches>
                    <api:referrals>
                        <api:search>
                            <api:types match="MUST">
                                <api:matcher>TOP_STORY</api:matcher>
                            </api:types>
                        </api:search>
                    </api:referrals>
                </api:searches>
            </api:pagesForm>""";

        PageForm rounded = JAXBTestUtil.roundTripAndSimilar(form, xml);

        assertThat(rounded.getSearches().getReferrals().asList().get(0).getTypes().asList().get(0).getValue()).isEqualTo("TOP_STORY");
    }

    @Test
    public void getLinksJson() {
        PageForm form = PageFormBuilder.form().links(AssociationSearch.of(LinkType.TOP_STORY)).highlight(true).build();
        String json = "{\"searches\":{\"links\":{\"types\":\"TOP_STORY\"}},  \"highlight\" : true}";

        PageForm rounded = Jackson2TestUtil.roundTripAndSimilar(form, json);

        assertThat(rounded.getSearches().getLinks().asList().get(0).getTypes().asList().get(0).getValue()).isEqualTo("TOP_STORY");
    }



    @Test
    public void testUnmarshal() throws IOException {
        Jackson2Mapper.getInstance().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        PageForm form = Jackson2Mapper.getInstance().readValue(getClass().getResourceAsStream("/NPA-281.json"), PageForm.class);
        Jackson2Mapper.getInstance().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }



}
