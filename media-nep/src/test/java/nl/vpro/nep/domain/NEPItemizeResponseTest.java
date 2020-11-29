package nl.vpro.nep.domain;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Slf4j
public class NEPItemizeResponseTest {

    @Test
    public void unbind() throws IOException {



        NEPItemizeResponse response = Jackson2Mapper.getLenientInstance().readValue("{'id': 'aaa'}", NEPItemizeResponse.class);
        log.info("" + response.getId());
    }

}
