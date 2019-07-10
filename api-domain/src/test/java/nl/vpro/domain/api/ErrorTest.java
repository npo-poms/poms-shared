package nl.vpro.domain.api;

import java.io.IOException;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import nl.vpro.domain.constraint.Constraints;
import nl.vpro.domain.constraint.media.And;
import nl.vpro.domain.media.Program;
import nl.vpro.i18n.Locales;
import nl.vpro.jackson2.Jackson2Mapper;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static nl.vpro.test.util.jaxb.JAXBTestUtil.assertThatXml;
import static org.junit.Assert.assertEquals;

public class ErrorTest {

    @Before
    public void setup() {
        Locales.setDefault(Locale.US);
    }

    @Test
    public void json() throws IOException {
        assertEquals("{\"status\":404,\"message\":\"bla\"}", Jackson2Mapper.getInstance().writeValueAsString(new Error(404, "bla")));
    }

    @Test
    public void jsonWithPredicate() {
        Error error = new Error(404, new RuntimeException("bla"));
        error.setTestResult(new And(Constraints.alwaysFalse(), Constraints.alwaysTrue()).testWithReason(new Program()));
        error.causeString("cause");
        assertThatJson(error).isSimilarTo("{\n" +
            "  \"status\" : 404,\n" +
            "  \"message\" : \"bla\",\n" +
            "  \"classes\" : [ \"java.lang.RuntimeException\", \"java.lang.Exception\", \"java.lang.Throwable\", \"java.io.Serializable\" ],\n" +
            "   \"cause\" : \"cause\",\n" +
            "  \"testResult\" : {\n" +
            "    \"reason\" : \"And\",\n" +
            "    \"applies\" : false,\n" +
            "    \"description\" : {\n" +
            "      \"value\" : \"(Never matches)\",\n" +
            "      \"lang\" : \"en_US\"\n" +
            "    },\n" +
            "    \"clauses\" : [ {\n" +
            "      \"reason\" : \"AlwaysFalse\",\n" +
            "      \"applies\" : false,\n" +
            "      \"description\" : {\n" +
            "        \"value\" : \"Never matches\",\n" +
            "        \"lang\" : \"en_US\"\n" +
            "      }\n" +
            "    }, {\n" +
            "      \"reason\" : \"AlwaysTrue\",\n" +
            "      \"applies\" : true,\n" +
            "      \"description\" : {\n" +
            "        \"value\" : \"Always matches\",\n" +
            "        \"lang\" : \"en_US\"\n" +
            "      }\n" +
            "    } ]\n" +
            "  }\n" +
            "}").andRounded().isNotNull();
    }

    @Test
    public void xmlWithPredicate() {
        Error error = new Error(404, "bla");
        error.setTestResult(new And(Constraints.alwaysFalse(), Constraints.alwaysTrue()).testWithReason(new Program()));
        assertThatXml(error).isSimilarTo("<api:error status=\"404\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:constraint=\"urn:vpro:api:constraint:2014\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <api:message>bla</api:message>\n" +
            "    <api:testResult xsi:type=\"constraint:andPredicateTestResult\" applies=\"false\" reason=\"And\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            "        <constraint:description xml:lang=\"en_US\">(Never matches)</constraint:description>\n" +
            "        <constraint:clauses>\n" +
            "            <constraint:clause applies=\"false\" reason=\"AlwaysFalse\">\n" +
            "                <constraint:description xml:lang=\"en_US\">Never matches</constraint:description>\n" +
            "            </constraint:clause>\n" +
            "            <constraint:clause applies=\"true\" reason=\"AlwaysTrue\">\n" +
            "                <constraint:description xml:lang=\"en_US\">Always matches</constraint:description>\n" +
            "            </constraint:clause>\n" +
            "        </constraint:clauses>\n" +
            "    </api:testResult>\n" +
            "</api:error>");
    }
}
