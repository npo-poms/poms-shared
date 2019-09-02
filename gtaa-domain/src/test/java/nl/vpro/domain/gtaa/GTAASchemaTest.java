package nl.vpro.domain.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.BeforeClass;
import org.junit.Test;

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


    @BeforeClass
    public static void generateXSDs() throws JAXBException, IOException {
        context = generate(Scheme.classesAndNew());
    }
    @Test
    public void testGtaa() throws IOException {
        testNamespace(Xmlns.GTAA_NAMESPACE);
    }

}
