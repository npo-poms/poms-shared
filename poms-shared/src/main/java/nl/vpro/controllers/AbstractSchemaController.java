package nl.vpro.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
public class AbstractSchemaController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractSchemaController.class);


    protected static Map<String, Class[]> MAPPING = new LinkedHashMap<>();

    @PostConstruct
    public void init() throws IOException, JAXBException {
        Set<Class> classes = new LinkedHashSet<>();
        for (Class[] c : MAPPING.values()) {
            classes.addAll(Arrays.asList(c));
        }
        generateXSDs(new ArrayList<>(classes).toArray(new Class[classes.size()]));
    }


	protected final long startTime = System.currentTimeMillis();


	protected void el(XMLStreamWriter w, String name, String chars) throws XMLStreamException {
		w.writeStartElement(name);
		w.writeCharacters(chars);
		w.writeEndElement();
	}

	protected void h2(XMLStreamWriter w, String chars) throws XMLStreamException {
		el(w, "h2", chars);
	}

	protected void a(XMLStreamWriter w, String href, String chars) throws XMLStreamException {
		w.writeStartElement("a");
		w.writeAttribute("href", href);
		w.writeCharacters(chars);
		w.writeEndElement();
	}

	protected void li_a(XMLStreamWriter w, String href, String chars) throws XMLStreamException {
		w.writeStartElement("li");
		a(w, href, chars);
		w.writeEndElement();
	}

	protected void getXSD(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final String namespace) throws JAXBException, IOException {
		File file = getFile(namespace);
		serveXml(file, request, response);
	}


	protected void serveXml(File file, HttpServletRequest request, HttpServletResponse response) throws IOException {
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		Date fileDate = DateUtils.round(new Date(file.lastModified()), Calendar.SECOND);
		if (ifModifiedSince > fileDate.getTime()) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			response.setDateHeader("Last-Modified", fileDate.getTime());
		} else {
			response.setContentType("application/xml");
			response.setDateHeader("Last-Modified", fileDate.getTime());
			IOUtils.copy(new FileInputStream(file), response.getOutputStream());
		}
	}


	protected void generateXSDs(Class... classes) throws IOException, JAXBException {
        LOG.info("Generating xsds in {}", Arrays.asList(classes), getTempDir());
		JAXBContext context = JAXBContext.newInstance(classes);
		context.generateSchema(new SchemaOutputResolver() {
			@Override
			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                File f;
                if (StringUtils.isEmpty(namespaceUri)) {
                    f = new File(getTempDir(), suggestedFileName);
                } else {
                    f = getFile(namespaceUri);
                }
                if (! f.exists()) {
                    LOG.debug("Creating {} -> {}", namespaceUri, f);

                    StreamResult result = new StreamResult(f);
                    result.setSystemId(f);
                    FileOutputStream fo = new FileOutputStream(f);
                    result.setOutputStream(fo);
                    return result;
                } else {
                    LOG.debug("{} -> {} Was already generated", namespaceUri, f);
                    return null;
                }

			}
		});

	}


	protected Path tempDir;

	protected File getTempDir() throws IOException {
		if (tempDir == null) {
			tempDir = Files.createTempDirectory("schemas");
		}
		return tempDir.toFile();
	}

	public File getFile(String namespace) throws IOException {
		String fileName = namespace.substring("urn:vpro:".length()).replace(':', '_') + ".xsd";
		File file = new File(getTempDir(), fileName);
        // last modified on fs only granalur to seconds.
        if (file.exists() && TimeUnit.SECONDS.convert(file.lastModified(), TimeUnit.MILLISECONDS) < TimeUnit.SECONDS.convert(startTime, TimeUnit.MILLISECONDS)) {
            LOG.info("Deleting {}, it is old {} < {}", file, file.lastModified(), startTime);
            file.delete();
        }
		return file;
	}
}
