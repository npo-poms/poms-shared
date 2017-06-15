package nl.vpro.spring.web.controllers.schema;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.support.ResourceResolver;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * This servlet produces the XSD's and some other XML's which are used by the mediaServer
 * @author Michiel Meeuwissen
 */
@Controller
@RequestMapping(value = "")
public class SchemaController {

    private static final String PACKAGE = "nl/vpro/domain/media/";

    @RequestMapping(value = "",  method = RequestMethod.GET)
    public void list(HttpServletResponse response) throws IOException, XMLStreamException {
        response.setContentType("text/html");
        response.setHeader("Cache-Control", "public, max-age=86400");
        write(response.getOutputStream());
    }
    
    protected void write(OutputStream output) throws XMLStreamException, UnsupportedEncodingException {
        XMLStreamWriter w = XMLOutputFactory.newInstance().createXMLStreamWriter(output);

        w.writeStartDocument();
        w.writeProcessingInstruction("DOCTYPE", "html");
        w.writeStartElement("html");
        {
            w.writeStartElement("head");
            {
                el(w, "title", "POMS xsd schemas");
            }
            w.writeEndElement();
            w.writeStartElement("body");
            {
                h2(w, "Schemas");
                w.writeStartElement("ul");
                {
                    for (Map.Entry<String, URL> entry : ResourceResolver.getSchemas().entrySet()) {
                        li_a(w, URLEncoder.encode(entry.getKey(), "UTF-8"), entry.getKey());
                    }
                    li_a(w, "./absentnamespace.xsd", "Used in POMS without namespace");
                    li_a(w, "./combined.xsd", "One XSD combining the relevant poms XSD's");
                    li_a(w, "./projectm/metadata_v2_1.xsd", "Project M v2.1"); // (has no namespace)
                    li_a(w, "./projectm/metadata_v3_2.xsd", "Project M v3.2"); // (has no namespace)
                }
                w.writeEndElement();
                h2(w, "XML's");
                w.writeStartElement("ul");
                {
                    li_a(w, "./classification", "classification");
                    li_a(w, "./classification/ebu_ContentGenreCS.xml", "classification (source xml)");
                }
                w.writeEndElement();
                h2(w, "XSL's");
                w.writeStartElement("ul");
                {
                    li_a(w, "./tva/tvaTransformer.xsl", "TVA-XML to POMS transformer");
                }
                w.writeEndElement();

            }
            w.writeEndElement();
        }
        w.writeEndElement();
        w.writeEndDocument();
    }


    private void el(XMLStreamWriter w, String name, String chars) throws XMLStreamException {
        w.writeStartElement(name);
        w.writeCharacters(chars);
        w.writeEndElement();
    }
    private void h2(XMLStreamWriter w, String chars) throws XMLStreamException {
        el(w, "h2", chars);
    }

    private void a(XMLStreamWriter w, String href, String chars) throws XMLStreamException {
        w.writeStartElement("a");
        w.writeAttribute("href", href);
        w.writeCharacters(chars);
        w.writeEndElement();
    }

    private void li_a(XMLStreamWriter w, String href, String chars) throws XMLStreamException {
        w.writeStartElement("li");
        a(w, href, chars);
        w.writeEndElement();
    }

    @RequestMapping(value = "/classification", method = RequestMethod.GET)
    public void getSchema(
        HttpServletResponse response) throws IOException {
        response.setContentType("application/xml");
        response.setDateHeader("Last-Modified", ClassificationServiceLocator.getInstance().getLastModified().toEpochMilli());
        JAXB.marshal(ClassificationServiceLocator.getInstance().getClassificationScheme(), response.getOutputStream());
    }


    @RequestMapping(value = "/urn:{namespace}", method = RequestMethod.GET)
    public void getSchema(
        @PathVariable(value = "namespace") String xmlns,
        HttpServletResponse response) throws IOException {
        
        URL url = ResourceResolver.resolveToURL("urn:" + xmlns);
        if (url != null) {
            int i = url.toString().indexOf(PACKAGE);
            response.sendRedirect(url.toString().substring(i + PACKAGE.length()));
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    @RequestMapping(value = "/{name}.{extension:xsd|xsl|xml}", method = RequestMethod.GET)
    public void getResource(
        @PathVariable(value = "name") String name,
        @PathVariable(value = "extension") String extension,
        HttpServletResponse response) throws IOException {
        getResource(PACKAGE + name + "." + extension, response);
    }



    @RequestMapping(value = "/{path}/{name}.{extension:xsd|xsl|xml}", method = RequestMethod.GET)
    public void getPathResource(
        @PathVariable(value = "path") String path,
        @PathVariable(value = "name") String name,
        @PathVariable(value = "extension") String extension,
        HttpServletResponse response) throws IOException {
        getResource(PACKAGE + path + "/" + name + "." + extension, response);
    }


    @RequestMapping(value = "/{path1}/{path2}/{name}.{extension:xsd|xsl|xml}", method = RequestMethod.GET)
    public void getPath2Resource(
        @PathVariable(value = "path1") String path1,
        @PathVariable(value = "path2") String path2,
        @PathVariable(value = "name") String name,
        @PathVariable(value = "extension") String extension,
        HttpServletResponse response) throws IOException {
        getResource(PACKAGE + path1 + "/" + path2 + "/" + name + "." + extension, response);
    }


    @RequestMapping(value = "/{path1}/{path2}/{path3}/{name}.{extension:xsd|xsl|xml}", method = RequestMethod.GET)
    public void getPath3Resource(
        @PathVariable(value = "path1") String path1,
        @PathVariable(value = "path2") String path2,
        @PathVariable(value = "path3") String path3,
        @PathVariable(value = "name") String name,
        @PathVariable(value = "extension") String extension,
        HttpServletResponse response) throws IOException {
        getResource(PACKAGE + path1 + "/" + path2 + "/" + path3 + "/" + name + "." + extension, response);
    }


    protected void getResource(String resource, HttpServletResponse response) throws IOException {
        response.setContentType("application/xml");
        response.setHeader("Cache-Control", "public, max-age=86400");
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        if (inputStream == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            IOUtils.copy(inputStream, response.getOutputStream());
        }
    }

}
