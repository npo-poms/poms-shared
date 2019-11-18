package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.junit.BeforeClass;
import org.junit.Test;

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
 * @since 5.12
 */
@Slf4j
public class SchemaTest extends AbstractSchemaTest {


    @BeforeClass
    public static void generateXSDs() throws JAXBException, IOException {
        context = generate(
            Subtitles.class
        );

    }
    @Test
    public void testSubtitles() throws IOException {
        testNamespace(Xmlns.MEDIA_SUBTITLES_NAMESPACE);
    }


}
