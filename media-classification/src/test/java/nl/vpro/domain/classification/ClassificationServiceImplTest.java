/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.classification;

import java.io.*;

import javax.xml.bind.JAXB;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.common.io.Files;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class ClassificationServiceImplTest {

    private final ClassificationService classificationService;

    public ClassificationServiceImplTest() {
        //classificationService = new ClassificationServiceImpl(new UrlResource("http://localhost:8060/schema/classification"));
        //classificationService = new ClassificationServiceImpl(new UrlResource("http://publish-dev.poms.omroep.nl/schema/classification"));
        classificationService = ClassificationServiceImpl.fromClassPath("nl/vpro/domain/media/classification/ebu_ContentGenreCS.xml");
        System.out.println(classificationService);
    }


    @Test
    public void testGetTermByIdOnMainGenre() {
        assertThat(classificationService.getTerm("3.0.1.5")).isNotNull();
    }

    @Test
    public void getTermByReference() {
        assertThat(classificationService.getTermsByReference("urn:mis:genre:MOVIE")).isNotNull();
        assertThat(classificationService.getTermsByReference("urn:mis:genre:ENTERTAINMENT")).hasSize(3);
    }
    @Test
    public void testGetTermByIdOnDoubleDigits() {
        assertThat(classificationService.getTerm("3.0.1.2.10").getName()).isEqualTo("Spanning");
    }


    @Test
    public void testGetNext() {
        assertThat(new TermId("3.0.1").next()).isEqualTo(new TermId("3.0.2"));
    }

    @Test
    public void testReferencesUnique() {


    }

    @Test
    public void testGetValues() {
        assertThat(classificationService.valuesOf("3.0.1").iterator().next().getTermId()).isEqualTo("3.0.1.1");
        assertThat(classificationService.valuesOf("3.0.1").size()).isEqualTo(54);
       /* for (Term id : ClassificationService.valuesOf("3.0.1")) {
            System.out.println(id.getTermId());
        }*/

    }

    @Test
    public void testMultipleDirectory() {
        ClassificationService classificationService = ClassificationServiceImpl.fromClassPath(
            "nl/vpro/domain/classification/scan/classifications-1.xml",
            "nl/vpro/domain/classification/scan/classifications-2.xml"
        );

        assertThat(classificationService.values()).hasSize(2);
        assertThat(classificationService.values().iterator().next().getTermId()).isEqualTo("3.0.2");
    }

    @Test
    public void testMultipleDirectoryScan() {
        File dir = new File(getClass().getResource("/nl/vpro/domain/classification/scan/classifications-1.xml").getFile()).getParentFile();

        ClassificationService classificationService = ClassificationServiceImpl.fromFiles(dir);

        assertThat(classificationService.values()).hasSize(2);
        assertThat(classificationService.values().iterator().next().getTermId()).isEqualTo("3.0.2");
    }

    @Test
    public void testMultipleDirectoryScanWatcher() throws IOException, InterruptedException {
        File dir = Files.createTempDir();

        ClassificationServiceImpl classificationService = ClassificationServiceImpl.fromFiles(dir);

        classificationService.setPollIntervalInMillis(100);

        assertThat(classificationService.values()).hasSize(0);

        File file = new File(dir, "classifications-1.tmp");
        file.deleteOnExit();
        FileOutputStream terms = new FileOutputStream(file);
        IOUtils.copy(getClass().getResourceAsStream("/nl/vpro/domain/classification/scan/classifications-1.xml"), terms);
        terms.close();
        File xml = new File(dir, "classifications-1.xml");
        file.renameTo(xml);
        System.out.println("Created " + xml + " " + xml.length());

        for (int i = 0 ; i < 300 ; i++) {
            System.out.print('.');
            if(classificationService.values().size() > 0) {
                break;
            }
            Thread.sleep(1000);
        }
        assertThat(classificationService.values()).hasSize(1);


    }

    @Test
    @Disabled
    public void test() {

        ClassificationService classificationService = ClassificationServiceImpl.fromFiles(
            new File("/Users/michiel/npo/pages/data/terms")
        );
        System.out.println(classificationService.values());

        assertThat(classificationService.values()).hasSize(28);


    }

    //@Test
    public void output() {

        JAXB.marshal(classificationService.getClassificationScheme(), System.out);

    }
}
