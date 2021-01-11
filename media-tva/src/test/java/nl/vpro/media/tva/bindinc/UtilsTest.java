package nl.vpro.media.tva.bindinc;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Slf4j
class UtilsTest {

    @Test
    void parseFileName() {
        Optional<Utils.BindincFile> bindincFile = Utils.parseFileName("1.xml");
        log.info("bla{}", bindincFile);
    }

}
