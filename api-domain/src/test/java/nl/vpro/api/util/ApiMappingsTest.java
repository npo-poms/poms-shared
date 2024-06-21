package nl.vpro.api.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;

import jakarta.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import nl.vpro.domain.Mappings;
import nl.vpro.domain.Xmlns;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class ApiMappingsTest {


    @BeforeEach
    public void reset() {
        Mappings.reset();
    }
/*

    @SneakyThrows
    protected ApiMappings createMappings(WireMockServer server) {
        server.stubFor(get(urlEqualTo("/schema/" + SHARED_XSD_NAME))
            .willReturn(ok(IOUtils.toString(Xmlns.class.getResource("/nl/vpro/domain/media/" + SHARED_XSD_NAME), StandardCharsets.UTF_8))));
        ApiMappings mappings = new ApiMappings(server.baseUrl());
        mappings.setGenerateDocumentation(false);
        return mappings;
    }
*/


    @SneakyThrows
    protected ApiMappings createMappings() {
        ApiMappings mappings = new ApiMappings(null);
        mappings.setGenerateDocumentation(true);
        return mappings;
    }

    @Test
    public void testProfileSchema() {
        testNamespace(createMappings(), Xmlns.PROFILE_NAMESPACE);
    }

    @Test
    public void testProfileSchemaMedia()   {
        testNamespace(createMappings(), Xmlns.MEDIA_CONSTRAINT_NAMESPACE);
    }

    @Test
    public void testProfileSchemaPage() {
        testNamespace(createMappings(), Xmlns.PAGE_CONSTRAINT_NAMESPACE);
    }

    @Test
    public void testProfileConstraint()  {
        testNamespace(createMappings(), Xmlns.CONSTRAINT_NAMESPACE);
    }


    /**
     * @TODO Tested xsd is in pages-domain
     */
    @Test
    public void testPageSchema() {
        testNamespace(createMappings(), Xmlns.PAGE_NAMESPACE);
    }

    @Test
    public void testPageUpdateSchema()   {
        testNamespace(createMappings(), Xmlns.PAGEUPDATE_NAMESPACE);
    }

    @Test
    public void testApiSchema()   {
        testNamespace(createMappings(), Xmlns.API_NAMESPACE);
    }

    @Test
    public void testSubtitlesSchema()  {
        testNamespace(createMappings(), Xmlns.MEDIA_SUBTITLES_NAMESPACE);
    }

    @Test
    @Disabled("no mapping for that checked in here")
    public void testMediaSchema()  {
        testNamespace(createMappings(), Xmlns.MEDIA_NAMESPACE);
    }



    @Test
    public void testUnmarshallers() {
        ApiMappings mappings = createMappings();
        for (String ns : mappings.knownNamespaces()) {
            Unmarshaller schema = mappings.getUnmarshaller(true, ns).get();
        }
    }


    @SneakyThrows
    protected void testNamespace(ApiMappings mappings, String xmlns)  {

        mappings.getUnmarshaller(true, xmlns).get();

        File file = mappings.getXsdFile(xmlns);
        InputStream control = getClass().getResourceAsStream("/xsds/" + file.getName());
        if (control == null) {
            log.info(file.getName());
            IOUtils.copy(new FileInputStream(file), System.out);
            throw new RuntimeException("No file " + file.getName());
        }
        //File xsdFile = mappings.getFileWithDocumentation(xmlns);
        File xsdFile = mappings.getXsdFile(xmlns);
        Diff diff = DiffBuilder.compare(control).withTest(
            new FileInputStream(xsdFile))
            .ignoreWhitespace()
            .ignoreComments()
            .checkForIdentical()
            .build();
        diff.getDifferences();

        URL resource = getClass().getResource("/xsds/" + file.getName());
        log.info("Checking workspace file " + resource);
        log.info("And classpath file " + file);
        assertThat(diff.hasDifferences()).withFailMessage("Not identical " + file + " " + resource + " " + diff.toString()).isFalse();
        log.info("Identical {} {}", file, getClass().getResource("/xsds/" + file.getName()));


    }

}
