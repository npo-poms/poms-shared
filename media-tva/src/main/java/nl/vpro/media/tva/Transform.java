package nl.vpro.media.tva;

import net.sf.saxon.TransformerFactoryImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

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

    static Transformer mediaTableToTva;

    public static synchronized Transformer tvaToMediaTable(Map<String, Object> parameters) throws TransformerConfigurationException, ParserConfigurationException, SAXException, IOException {
        if (! tvaFactoryConfigured) {
            throw new IllegalStateException("No SaxonConfiguration set. Please call setTVAConfiguration first.");
        }
        final Transformer tvaToMediaTable = getTransformer(TVA_FACTORY, "/nl/vpro/media/tva/tvaTransformer.xsl");
        tvaToMediaTable.setParameter(
            XSL_PARAM_CHANNELMAPPING,
            createChannelMapping(Constants.ChannelIdType.PD));
        tvaToMediaTable.setParameter(
            XSL_PARAM_WORKFLOW,
            Workflow.FOR_REPUBLICATION.getXmlValue()
        );
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            tvaToMediaTable.setParameter(entry.getKey(), entry.getValue());
        }
        return tvaToMediaTable;
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

    public static synchronized Transformer mediaTableToTVA() throws TransformerConfigurationException {
        if (mediaTableToTva == null) {
            mediaTableToTva = getTransformer(MEDIATABLE_FACTORY, "/nl/vpro/media/tva/pomsToTVATransformer.xsl");
        }
        return  mediaTableToTva;
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



}
