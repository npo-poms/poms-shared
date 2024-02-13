package nl.vpro.domain.api;

import java.io.IOException;
import java.util.Locale;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.constraint.Constraints;
import nl.vpro.domain.constraint.media.And;
import nl.vpro.domain.media.Program;
import nl.vpro.i18n.Locales;
import nl.vpro.jackson2.Jackson2Mapper;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;
import static nl.vpro.test.util.jaxb.JAXBTestUtil.assertThatXml;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorTest {

    @BeforeEach
    public void setup() {
        Locales.setDefault(Locale.US);
    }

    @Test
    public void json() throws IOException {
        assertEquals("{\"status\":404,\"message\":\"bla\"}", Jackson2Mapper.getInstance().writeValueAsString(new Error(404, "bla")));
    }

    @Test
    public void jsonWithPredicate() {
        Error error = new Error(Response.Status.NOT_FOUND, new RuntimeException("bla"), false, true);
        error.setTestResult(new And(Constraints.alwaysFalse(), Constraints.alwaysTrue()).testWithReason(new Program()));
        assertThatJson(error).isSimilarTo("""
            {
              "status" : 404,
              "message" : "bla",
              "classes" : [ "RuntimeException" ],
              "testResult" : {
                "objectType" : "and",
                "reason" : "And",
                "applies" : false,
                "description" : {
                  "value" : "(Never matches)",
                  "lang" : "en_US"
                },
                "clauses" : [ {
                  "objectType" : "simple",
                  "reason" : "AlwaysFalse",
                  "applies" : false,
                  "description" : {
                    "value" : "Never matches",
                    "lang" : "en_US"
                  }
                }, {
                  "objectType" : "simple",
                  "reason" : "AlwaysTrue",
                  "applies" : true,
                  "description" : {
                    "value" : "Always matches",
                    "lang" : "en_US"
                  }
                } ]
              }
            }""").andRounded().isNotNull();
    }

    @Test
    public void xmlWithPredicate() {
        Error error = new Error(404, "bla");
        error.setTestResult(new And(Constraints.alwaysFalse(), Constraints.alwaysTrue()).testWithReason(new Program()));
        assertThatXml(error).isSimilarTo("""
            <api:error status="404" xmlns:pages="urn:vpro:pages:2013" xmlns:constraint="urn:vpro:api:constraint:2014" xmlns:api="urn:vpro:api:2013" xmlns:media="urn:vpro:media:2009">
                <api:message>bla</api:message>
                <api:testResult xsi:type="constraint:andPredicateTestResult" applies="false" reason="And" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                    <constraint:description xml:lang="en_US">(Never matches)</constraint:description>
                    <constraint:clauses>
                        <constraint:clause xsi:type="constraint:simplePredicateTestResult" applies="false" reason="AlwaysFalse">
                            <constraint:description xml:lang="en_US">Never matches</constraint:description>
                        </constraint:clause>
                        <constraint:clause xsi:type="constraint:simplePredicateTestResult" applies="true" reason="AlwaysTrue">
                            <constraint:description xml:lang="en_US">Always matches</constraint:description>
                        </constraint:clause>
                    </constraint:clauses>
                </api:testResult>
            </api:error>
            """);
    }
}
