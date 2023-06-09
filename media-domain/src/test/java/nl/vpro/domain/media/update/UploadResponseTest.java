package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

class UploadResponseTest {

    @Test
    public void xml() {
       UploadResponse response = new UploadResponse("mid_123", 200, "succes", "foobar", 1000L);

        JAXBTestUtil.assertThatXml(response).noRoundTrip().isSimilarTo("<uploadResponse statusCode=\"200\" mid=\"mid_123\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                                                                       "    <status>succes</status>\n" +
                                                                       "    <response>foobar</response>\n" +
                                                                       "    <bytes>1000</bytes>\n" +
                                                                       "</uploadResponse>");

    }

    @Test
    public void json() {
       UploadResponse response = new UploadResponse("mid_123", 200, "succes", "foobar", 1000L);

        Jackson2TestUtil.assertThatJson(response).isSimilarTo("{\n" +
                                                              "  \"statusCode\" : 200,\n" +
                                                              "  \"mid\" : \"mid_123\",\n" +
                                                              "  \"status\" : \"succes\",\n" +
                                                              "  \"response\" : \"foobar\",\n" +
                                                              "  \"bytes\" : 1000\n" +
                                                              "}");

    }

}
