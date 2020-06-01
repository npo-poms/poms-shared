package nl.vpro.domain.page;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.Xmlns;
import nl.vpro.test.util.jaxb.AbstractSchemaTest;


/**
 * Tests whether the POMS schema's are changed. If tests-cases fail here, fix them, but <em>also don't forget to make the changes to the XSD's also to the manually maintained ones</em>.
 * in nl/vpro/domain/media
 *
 * E.g. in nl/vpro/domain/media/vproMedia.xsd
 *
 * So normally you'd have to change <em>two</em> XSD's.
 *
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@Slf4j
public class SchemaTest extends AbstractSchemaTest {


    @BeforeAll
    public static void generateXSDs() throws JAXBException, IOException {
        context = generate(

            Page.class
        );

    }
    @Test
    public void testPage() throws IOException {
        testNamespace(Xmlns.PAGE_NAMESPACE);
    }






}
