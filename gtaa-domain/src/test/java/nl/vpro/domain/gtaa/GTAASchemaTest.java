package nl.vpro.domain.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.Xmlns;
import nl.vpro.test.util.jaxb.AbstractSchemaTest;


/**
 *
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class GTAASchemaTest extends AbstractSchemaTest {


    @Override
    protected Class<?>[] getClasses() {
        return Scheme.classesAndNew();
    }

    @Test
    public void testGtaa() throws IOException {
        testNamespace(Xmlns.GTAA_NAMESPACE);
    }

}
