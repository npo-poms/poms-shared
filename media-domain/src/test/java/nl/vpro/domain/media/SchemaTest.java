package nl.vpro.domain.media;

import java.io.*;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import com.google.common.io.Files;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.search.MediaForm;
import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.domain.media.search.MediaSearchResult;
import nl.vpro.domain.media.update.*;
import nl.vpro.domain.media.update.action.MoveAction;
import nl.vpro.domain.media.update.collections.XmlCollection;
import nl.vpro.domain.user.Broadcaster;

import static org.assertj.core.api.Java6Assertions.assertThat;


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
public class SchemaTest {

    private final static File DIR = Files.createTempDir();

    @BeforeClass
    public static void generateXSDs() throws JAXBException, IOException, SAXException {
        generate(
            // media
            Program.class,
            Segment.class,
            Schedule.class,
            Group.class,
            MediaTable.class,
            Broadcaster.class,
            // search
            MediaForm.class,
            MediaSearchResult.class,
            MediaListItem.class,
            // update
            MidAndType.class,
            ProgramUpdate.class,
            GroupUpdate.class,
            SegmentUpdate.class,
            MoveAction.class,
            BulkUpdate.class,
            ImageUpdate.class,
            LocationUpdate.class,
            // no namespace
            XmlCollection.class
            //

        );

    }
    @Test
    public void testMedia() throws IOException, SAXException {
        testNamespace(Xmlns.MEDIA_NAMESPACE);

    }

    @Test
    public void testMediaSearch() throws IOException, SAXException {
        testNamespace(Xmlns.SEARCH_NAMESPACE);
    }

    @Test
    public void testShared() throws IOException, SAXException {
        testNamespace(Xmlns.SHARED_NAMESPACE);
    }

    @Test
    public void testUpdate() throws IOException, SAXException {
        testNamespace(Xmlns.UPDATE_NAMESPACE);
    }

    @Test
    public void testAbsent() throws IOException, SAXException {
        testNamespace("");
    }

    private static File getFile(final String namespace) {
        String filename = namespace;
        if (StringUtils.isEmpty(namespace)) {
            filename = "absentnamespace";
        }
        return new File(DIR, filename + ".xsd");
    }

    private  Schema testNamespace(String namespace) throws IOException, SAXException {
        File file = getFile(namespace);
        InputStream control = getClass().getResourceAsStream("/schema/" + file.getName());
        if (control == null) {
            System.out.println(file.getName());
            IOUtils.copy(new FileInputStream(file), System.out);
            throw new RuntimeException("No file " + file.getName());
        }
        Diff diff = DiffBuilder.compare(control)
            .withTest(file)
            .checkForIdentical()
            .build();

        assertThat(diff.hasDifferences())
            .withFailMessage("" + file + " should be equal to " + getClass().getResource("/schema/" + file.getName())).isFalse();
        return null;
    }

    private static void generate(Class... classes) throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(classes);
        context.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                if (XMLConstants.XML_NS_URI.equals(namespaceUri)) {
                    return null;
                }
                File f = getFile(namespaceUri);
                if (f.exists()) {
                    f = File.createTempFile(namespaceUri, "");
                }
                System.out.println(namespaceUri + " -> " + f);

                StreamResult result = new StreamResult(f);
                result.setSystemId(f);
                FileOutputStream fo = new FileOutputStream(f);
                result.setOutputStream(fo);
                return result;


            }
        });
    }
}
