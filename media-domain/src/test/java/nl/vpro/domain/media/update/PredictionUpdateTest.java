package nl.vpro.domain.media.update;

import java.io.StringReader;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Platform;

import static org.assertj.core.api.Assertions.assertThat;

class PredictionUpdateTest {

    @Test
    public void xml() {
        String xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <prediction xmlns="urn:vpro:media:update:2009" publishStart="2021-01-01T00:00:00.001Z" encryption="NONE">INTERNETVOD</prediction>
            """;
        PredictionUpdate unmarshal = JAXB.unmarshal(new StringReader(xml), PredictionUpdate.class);
        assertThat(unmarshal.getPlatform()).isEqualTo(Platform.INTERNETVOD);
    }

}
