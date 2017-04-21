package nl.vpro.domain.api.page;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import com.fasterxml.jackson.databind.DeserializationFeature;

import nl.vpro.domain.api.FacetOrder;
import nl.vpro.domain.api.Match;
import nl.vpro.domain.api.Order;
import nl.vpro.domain.api.SimpleTextMatcher;
import nl.vpro.domain.page.LinkType;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class PageFormTest {

    @Test
    public void testGetText() throws Exception {
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
            .isSimilarTo("{\"searches\":{\"portals\":\"WETENSCHAP24\"},\"facets\":{\"sections\":{\"sort\":\"COUNT_DESC\",\"max\":24}},\"highlight\":false}");
    }

    @Test
    public void testMarshalWithSort() throws IOException {

        PageFormBuilder builder = PageFormBuilder.form().addSortField("lastModified", Order.DESC);
        StringWriter writer = new StringWriter();
        Jackson2Mapper.getInstance().writeValue(writer, builder.build());
        System.out.println(writer.toString());
        assertThatJson(writer.toString())
            .isEqualTo("{\"sort\":{\"lastModified\":\"DESC\"},\"highlight\":false}");
    }

    @Test
    public void unmarshal() throws IOException {
        String string = "{\n" +
            "    \"facets\": {\n" +
            "        \"sections\": {\n" +
            "            }\n" +
            "    },\n" +
            "    \"highlight\": true,\n" +
            "    \"searches\": {\n" +
            "     \"sortDates\": { \"begin\": 0}\n" +
            "    }\n" +
            "}";

        PageForm form  = Jackson2Mapper.getInstance().readValue(new StringReader(string), PageForm.class);

        assertThat(form.getSearches().getSortDates().asList()).hasSize(1);
    }


    @Test
    public void unmarshalBackwards() throws IOException {
        String string = "{\n" +
            "    \"facets\": {\n" +
            "        \"sections\": {\n" +
            "            \"sort\": \"COUNT\"\n" +
            "            }\n" +
            "    }\n" +
            "}";

        PageForm form = Jackson2Mapper.getInstance().readValue(new StringReader(string), PageForm.class);

        assertThat(form.getFacets().getSections().getSort()).isEqualTo(FacetOrder.COUNT_DESC);
    }

    @Test
    public void toXml() throws IOException, SAXException {
        String json = "{\n" +
            "    \"searches\" : {\n" +
            "\n" +
            "    },\n" +
            "\n" +
            " \"sort\": {\"sortDate\": \"ASC\" },\n" +
            "    \"facets\" : {\n" +
            "      \"sortDates\" : [ \"YEAR\" ]\n" +
            "    }\n" +
            "}\n";
        PageForm form = Jackson2Mapper.INSTANCE.readValue(new StringReader(json), PageForm.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(form, out);
        String expected =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<api:pagesForm xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" highlight=\"false\">\n" +
                "    <api:searches/>\n" +
                "    <api:sortFields>\n" +
                "        <api:sort order=\"ASC\">sortDate</api:sort>\n" +
                "    </api:sortFields>\n" +
                "    <api:facets>\n" +
                "        <api:sortDates>\n" +
                "            <api:interval>YEAR</api:interval>\n" +
                "        </api:sortDates>\n" +
                "    </api:facets>\n" +
                "</api:pagesForm>";

        System.out.println(out.toString());
        Diff diff = DiffBuilder.compare(expected).withTest(out.toString()).build();
        if (diff.hasDifferences()) {
            assertThat(out.toString()).isEqualTo(expected);
        }

    }
    @Test
    public void toXml2() throws IOException, JAXBException, SAXException {
        String json = "{\"searches\":{\"types\":[\"PLAYER\"]},\"sort\":{\"sortDate\":\"DESC\"},\"facets\":{\"keywords\":{\"threshold\":0,\"sort\":\"COUNT_DESC\",\"offset\":0,\"max\":24},\"genres\":{\"threshold\":0,\"sort\":\"COUNT_DESC\",\"offset\":0,\"max\":24},\"sections\":{\"threshold\":0,\"sort\":\"COUNT_DESC\",\"offset\":0,\"max\":24}},\"mediaForm\":{\"facets\":{\"avTypes\":{\"threshold\":0,\"sort\":\"COUNT_DESC\",\"offset\":0,\"max\":24}},\"highlight\":false},\"highlight\":false}";
        PageForm form = Jackson2Mapper.INSTANCE.readValue(new StringReader(json), PageForm.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(form, out);
        System.out.println(out.toString());
        PageForm validated = (PageForm) getUnmarshaller(PageForm.class).unmarshal(new StringReader(out.toString()));
    }

    @Test
    public void testReferralsJson() throws Exception {
        PageForm form = PageFormBuilder.form().referrals(AssociationSearch.of(LinkType.TOP_STORY)).build();
        String json = "{\"searches\":{\"referrals\":{\"types\":\"TOP_STORY\"}},\"highlight\":false}";

        PageForm rounded = Jackson2TestUtil.roundTripAndSimilar(form, json);

        assertThat(rounded.getSearches().getReferrals().asList().get(0).getTypes().asList().get(0).getValue()).isEqualTo("TOP_STORY");
    }


    @Test
    public void testReferralsJsonShould() throws Exception {
        PageForm form = PageFormBuilder.form().referrals(AssociationSearch.of(LinkType.TOP_STORY, Match.SHOULD)).build();
        String json = "{\"searches\":{\"referrals\":{\"match\":\"SHOULD\",\"types\":\"TOP_STORY\"}},\"highlight\":false}";

        PageForm rounded = Jackson2TestUtil.roundTripAndSimilar(form, json);

        assertThat(rounded.getSearches().getReferrals().asList().get(0).getTypes().asList().get(0).getValue()).isEqualTo("TOP_STORY");
    }


    @Test
    public void testReferralsXml() throws Exception {
        PageForm form = PageFormBuilder.form().referrals(AssociationSearch.of(LinkType.TOP_STORY)).build();
        String xml = "<api:pagesForm highlight=\"false\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:searches>\n" +
            "        <api:referrals>\n" +
            "            <api:search>\n" +
            "                <api:types match=\"MUST\">\n" +
            "                    <api:matcher>TOP_STORY</api:matcher>\n" +
            "                </api:types>\n" +
            "            </api:search>\n" +
            "        </api:referrals>\n" +
            "    </api:searches>\n" +
            "</api:pagesForm>";

        PageForm rounded = JAXBTestUtil.roundTripAndSimilar(form, xml);

        assertThat(rounded.getSearches().getReferrals().asList().get(0).getTypes().asList().get(0).getValue()).isEqualTo("TOP_STORY");
    }

    @Test
    public void getLinksJson() throws Exception {
        PageForm form = PageFormBuilder.form().links(AssociationSearch.of(LinkType.TOP_STORY)).build();
        String json = "{\"searches\":{\"links\":{\"types\":\"TOP_STORY\"}},\"highlight\":false}";

        PageForm rounded = Jackson2TestUtil.roundTripAndSimilar(form, json);

        assertThat(rounded.getSearches().getLinks().asList().get(0).getTypes().asList().get(0).getValue()).isEqualTo("TOP_STORY");
    }



    @Test
    public void testUnmarshal() throws IOException {
        Jackson2Mapper.getInstance().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        PageForm form = Jackson2Mapper.getInstance().readValue(getClass().getResourceAsStream("/NPA-281.json"), PageForm.class);
        Jackson2Mapper.getInstance().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }



    private Unmarshaller getUnmarshaller(Class clazz) throws JAXBException, IOException, SAXException {
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        JAXBContext context = JAXBContext.newInstance(clazz);
        final DOMResult[] result = new DOMResult[1];
        result[0] = new DOMResult();
        context.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                result[0].setSystemId(namespaceUri);
                return result[0];
            }
        });
        Schema schema = sf.newSchema(new DOMSource(result[0].getNode()));
        Unmarshaller unmarshaller = JAXBContext.newInstance(PageForm.class).createUnmarshaller();
        unmarshaller.setSchema(schema);
        return unmarshaller;
    }
}
