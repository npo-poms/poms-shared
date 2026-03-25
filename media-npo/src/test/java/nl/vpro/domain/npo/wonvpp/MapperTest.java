package nl.vpro.domain.npo.wonvpp;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nl.vpro.domain.media.MediaTable;
import nl.vpro.domain.user.ServiceLocator;
import nl.vpro.media.broadcaster.BroadcasterServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
class MapperTest {

    Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    static {
        ServiceLocator.setBroadcasterService(new BroadcasterServiceImpl("https://poms.omroep.nl/broadcasters"));
    }


    @ParameterizedTest
    @ValueSource(strings = {"wonvpp.example.json", "wonvpp.example2.json"})

    public void map(String file) throws IOException {

        MediaTable table = Mapper.mapToPoms(Utils.unmarshal(getClass().getResourceAsStream("/wonvpp/%s".formatted(file))));
        log.info("" + table);
        assertThat(validator.validate(table)).isEmpty();
    }

}
