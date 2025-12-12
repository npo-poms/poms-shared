package nl.vpro.media.tva;

import net.sf.saxon.TransformerFactoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.util.JAXBResult;
import jakarta.xml.bind.util.JAXBSource;

import org.xml.sax.SAXException;

import nl.vpro.domain.media.MediaTable;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.media.tva.saxon.extension.SaxonConfiguration;

import static nl.vpro.media.tva.Constants.*;

/**
 * Tools to transform POMS to TVA and back.
 * <p>
 * Not (yet?) used by PDDropBox itself. That sets up its own transformer. But this is used in test-cases and for 'tva' endpoint in the schedule view of poms itself.
 *
 * @since 8.12
 */

public class Transform {


    static final TransformerFactoryImpl TVA_FACTORY = new TransformerFactoryImpl();
    static boolean tvaFactoryConfigured = false;
    static final TransformerFactoryImpl MEDIATABLE_FACTORY = new TransformerFactoryImpl();

    // Cached compiled templates
    private static Templates TVA_TEMPLATES;
    private static Templates MEDIATABLE_TEMPLATES;

    // Cached JAXBContext
    private static JAXBContext MEDIA_TABLE_CONTEXT;

    public static Transformer tvaToMediaTable(Map<String, Object> parameters) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        if (! tvaFactoryConfigured) {
            throw new IllegalStateException("No SaxonConfiguration set. Please call setTVAConfiguration first.");
        }
        ensureTVATemplates();

        Transformer transformer = TVA_TEMPLATES.newTransformer();
        transformer.setParameter(
            XSL_PARAM_CHANNELMAPPING,
            createChannelMapping(Constants.ChannelIdType.PD));
        transformer.setParameter(
            XSL_PARAM_WORKFLOW,
            Workflow.FOR_REPUBLICATION.getXmlValue()
        );
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            transformer.setParameter(entry.getKey(), entry.getValue());
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        return transformer;
    }

    public static void setTVAConfiguration(SaxonConfiguration configuration) {
        TVA_FACTORY.setConfiguration(configuration);
        tvaFactoryConfigured = true;
    }

    public static MediaTable toMediaTable(InputStream input, Map<String, Object> parameters) throws JAXBException, TransformerException, ParserConfigurationException, IOException, SAXException {
        JAXBResult result = new JAXBResult(JAXBContext.newInstance(MediaTable.class));
        tvaToMediaTable(parameters).transform(new StreamSource(input), result);
        return (MediaTable) result.getResult();
    }
    private static MediaTable toMediaTable(InputStream input) throws TransformerException, IOException, SAXException, ParserConfigurationException, JAXBException {
        return toMediaTable(input, Map.of());
    }

    public static Transformer mediaTableToTVA() throws TransformerConfigurationException {
        ensureMediaTableTemplates();
        Transformer transformer = MEDIATABLE_TEMPLATES.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        return  transformer;
    }

    public static void toTVA(MediaTable mediaTable, Result result) throws JAXBException, TransformerException {
        mediaTableToTVA().transform(new JAXBSource(JAXBContext.newInstance(MediaTable.class), mediaTable), result);
    }


    private static Transformer getTransformer(TransformerFactoryImpl factory, String resource) throws TransformerConfigurationException {
        StreamSource stylesource = new StreamSource(Transform.class.getResourceAsStream(resource));
        Transformer transformer = factory.newTransformer(stylesource);

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        return transformer;
    }



    private static synchronized void ensureTVATemplates() throws TransformerConfigurationException {
        if (TVA_TEMPLATES != null) {
            return;
        }
        synchronized (Transform.class) {
            if (TVA_TEMPLATES == null) {
                try (InputStream is = Transform.class.getResourceAsStream("/nl/vpro/media/tva/tvaTransformer.xsl")) {
                    Objects.requireNonNull(is, "Resource not found: /nl/vpro/media/tva/tvaTransformer.xsl");
                    StreamSource stylesource = new StreamSource(is);
                    TVA_TEMPLATES = TVA_FACTORY.newTemplates(stylesource);
                } catch (IOException e) {
                    // IOException won't actually be thrown by newTemplates, but keep signature-compatible
                    throw new TransformerConfigurationException("Failed to load TVA stylesheet", e);
                }
            }
        }
    }

    private static void ensureMediaTableTemplates() throws TransformerConfigurationException {
        if (MEDIATABLE_TEMPLATES != null) {
            return;
        }
        synchronized (Transform.class) {
            if (MEDIATABLE_TEMPLATES == null) {
                try (InputStream is = Transform.class.getResourceAsStream("/nl/vpro/media/tva/pomsToTVATransformer.xsl")) {
                    Objects.requireNonNull(is, "Resource not found: /nl/vpro/media/tva/pomsToTVATransformer.xsl");
                    StreamSource stylesource = new StreamSource(is);
                    MEDIATABLE_TEMPLATES = MEDIATABLE_FACTORY.newTemplates(stylesource);
                } catch (IOException e) {
                    throw new TransformerConfigurationException("Failed to load mediaTable stylesheet", e);
                }
            }
        }
    }



}
