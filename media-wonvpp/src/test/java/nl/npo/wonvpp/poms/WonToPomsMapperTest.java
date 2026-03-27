package nl.npo.wonvpp.poms;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.meeuw.time.TestClock;

import nl.npo.wonvpp.domain.Utils;
import nl.vpro.domain.media.MediaTable;
import nl.vpro.media.broadcaster.BroadcasterServiceLocator;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
class WonToPomsMapperTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static WonToPomsMapper  mapper;
    static {
        mapper = new WonToPomsMapper(BroadcasterServiceLocator.getInstance(false, true));
        mapper.clock = TestClock.twentyTwenty();
    }


    @ParameterizedTest
    @ValueSource(strings = {
        "20260224151601-CatalogEPG-POW_04882561-npo-svod.json",
        "20260224151602-CatalogEPG-POW_02934251-npo-svod.json",
        "20260224151602-CatalogEPG-POW_05880359-npo-svod.json",
        "20260305105904-CatalogEPG-POW_06213692-npo-fvod.json"
    })

    public void map(String file) throws IOException {

        MediaTable table = mapper.mapToPoms(Utils.unmarshal(getClass().getResourceAsStream("/wonvpp/%s".formatted(file))));
        log.info("" + table);
        assertThat(validator.validate(table)).isEmpty();

        JAXBTestUtil.registerNamespace(
            "urn:vpro:media:2009", "https://poms-test.omroep.nl/schema/vproMedia.xsd"
        );

        JAXBTestUtil.assertThatXml(table).isSimilarTo(getClass().getResourceAsStream("/wonvpp/%s.xml".formatted(file)));
    }

}
