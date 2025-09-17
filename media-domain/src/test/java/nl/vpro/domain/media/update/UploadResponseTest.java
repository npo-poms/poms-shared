package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

class UploadResponseTest {

    @Test
    public void xml() {
       UploadResponse response = new UploadResponse("mid_123", 200, "succes", "foobar", 1000L, null, true);

        JAXBTestUtil.assertThatXml(response).noRoundTrip().isSimilarTo("""
            <uploadResponse statusCode="200" mid="mid_123" xmlns="urn:vpro:media:update:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                <status>succes</status>
                <response>foobar</response>
                <bytes>1000</bytes>
                <retryable>true</retryable>
            </uploadResponse>""");

    }

    @Test
    public void json() {
       UploadResponse response = new UploadResponse("mid_123", 200, "succes", "foobar", 1000L, null, true);

        Jackson2TestUtil.assertThatJson(response).isSimilarTo("""
            {
              "statusCode" : 200,
              "mid" : "mid_123",
              "status" : "succes",
              "response" : "foobar",
              "bytes" : 1000,
              "retryable" : true
            }""");

    }

}
