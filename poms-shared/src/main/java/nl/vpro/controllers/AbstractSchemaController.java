package nl.vpro.controllers;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
public class AbstractSchemaController {

	protected static Map<String, Class[]> MAPPING = new HashMap<>();


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
		if (!file.exists() || file.length() == 0) {
			generateXSDs(namespace);
		}
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


	protected void generateXSDs(final String namespace) throws IOException, JAXBException {
		JAXBContext context = JAXBContext.newInstance(MAPPING.get(namespace));
		context.generateSchema(new SchemaOutputResolver() {
			@Override
			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
				File f = getFile(namespaceUri);
				StreamResult result = new StreamResult(f);
				result.setSystemId(f);
				result.setOutputStream(new FileOutputStream(f));
				return result;

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

	protected File getFile(String namespace) throws IOException {

		String fileName = namespace.substring("urn:vpro:".length()).replace(':', '_') + ".xsd";

		File file = new File(getTempDir(), fileName);
		if (file.exists() && file.lastModified() < startTime) {
			file.delete();
		}
		return file;
	}
}
