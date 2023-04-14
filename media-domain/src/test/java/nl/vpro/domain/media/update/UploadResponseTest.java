package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

class UploadResponseTest {

    @Test
    public void xml() {
       UploadResponse response = new UploadResponse(200, "succes", "foobar");

        JAXBTestUtil.assertThatXml(response).noRoundTrip().isSimilarTo("<uploadResponse statusCode=\"200\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <status>succes</status>\n" +
            "    <response>foobar</response>\n" +
            "</uploadResponse>");

    }

    @Test
    public void json() {
       UploadResponse response = new UploadResponse(200, "succes", "foobar");

        Jackson2TestUtil.assertThatJson(response).isSimilarTo("{\n" +
            "  \"statusCode\" : 200,\n" +
            "  \"status\" : \"succes\",\n" +
            "  \"response\" : \"foobar\"\n" +
            "}");

    }

}
