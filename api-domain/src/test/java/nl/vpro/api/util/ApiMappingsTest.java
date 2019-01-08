package nl.vpro.api.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import nl.vpro.domain.Xmlns;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class ApiMappingsTest {

    ApiMappings mappings = new ApiMappings(null);
    {
        mappings.setGenerateDocumentation(false);
    }



    @Test
    public void testProfileSchema() throws Exception {
        testNamespace(Xmlns.PROFILE_NAMESPACE);
    }

    @Test
    public void testProfileSchemaMedia() throws Exception {
        testNamespace(Xmlns.MEDIA_CONSTRAINT_NAMESPACE);
    }

    @Test
    public void testProfileSchemaPage() throws Exception {
        testNamespace(Xmlns.PAGE_CONSTRAINT_NAMESPACE);
    }


    @Test
    public void testProfileConstraint() throws Exception {
        testNamespace(Xmlns.CONSTRAINT_NAMESPACE);
    }


    @Test
    public void testPageSchema() throws Exception {
        testNamespace(Xmlns.PAGE_NAMESPACE);
    }

    @Test
    public void testPageUpdateSchema() throws Exception {
        testNamespace(Xmlns.PAGEUPDATE_NAMESPACE);
    }

    @Test
    public void testApiSchema() throws Exception {
        testNamespace(Xmlns.API_NAMESPACE);
    }

    @Test
    public void testSubtitlesSchema() throws Exception {
        testNamespace(Xmlns.MEDIA_SUBTITLES_NAMESPACE);
    }


    @Test
    public void testSecondscreenSchema() throws Exception {
        testNamespace(Xmlns.SECOND_SCREEN_NAMESPACE);
    }

    @Test
    @Ignore
    public void testMediaSchema() throws Exception {
        testNamespace(Xmlns.MEDIA_NAMESPACE);
    }



    @Test
    public void testUnmarshallers() {
        for (String ns : mappings.knownNamespaces()) {
            Unmarshaller schema = mappings.getUnmarshaller(true, ns).get();
        }
    }


    protected void testNamespace(String xmlns) throws IOException {

        mappings.getUnmarshaller(true, xmlns).get();


        File file = mappings.getFile(xmlns);
        InputStream control = getClass().getResourceAsStream("/xsds/" + file.getName());
        if (control == null) {
            System.out.println(file.getName());
            IOUtils.copy(new FileInputStream(file), System.out);
            throw new RuntimeException("No file " + file.getName());
        }
        Diff diff = DiffBuilder.compare(control).withTest(
            new FileInputStream(file))
            .ignoreWhitespace()
            .ignoreComments()
            .checkForIdentical()
            .build();
        diff.getDifferences();

        URL resource = getClass().getResource("/xsds/" + file.getName());
        log.info("Checking workspace file " + resource);
        log.info("And classpath file " + file);
        assertThat(diff.hasDifferences()).withFailMessage("Not identical " + file + " " + resource).isFalse();
        log.info("Identical {} {}", file, getClass().getResource("/xsds/" + file.getName()));


    }

}
