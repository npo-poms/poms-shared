package nl.vpro.domain.page.update;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

class SaveResultListTest {

    @Test
    public void xml() {
        SaveResultList result = new SaveResultList(
            List.of(
                SaveResult.builder()
                    .future(null)
                    .message("foo")
                    .build(),
                SaveResult.builder()
                    .future(null)
                    .message("bar")
                    .creationDate(Instant.ofEpochMilli(100))
                    .build()
            )
        );
        JAXBTestUtil.roundTripAndSimilar(result, """
                <saveResults xmlns="urn:vpro:pages:update:2013">
                    <saveResult success="false">
                      <message>foo</message>
                    </saveResult>
                    <saveResult success="false" creationDate="1970-01-01T01:00:00.100+01:00">
                      <message>bar</message>
                    </saveResult>
                  </saveResults>
                """);
    }

}
