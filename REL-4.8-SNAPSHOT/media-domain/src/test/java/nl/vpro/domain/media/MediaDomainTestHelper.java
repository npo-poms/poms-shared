/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.*;

import javax.validation.ConstraintViolation;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import nl.vpro.domain.ValidationTestHelper;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;
import nl.vpro.domain.user.Broadcaster;

public class MediaDomainTestHelper extends ValidationTestHelper {
    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance("nl.vpro.domain.media");
        } catch(JAXBException e) {
            e.printStackTrace();
        }
    }

    public static Unmarshaller unmarshaller;

    static {
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch(JAXBException e) {
            e.printStackTrace();
        }
    }

    public static Marshaller marshaller;

    static {
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch(JAXBException e) {
            e.printStackTrace();
        }
    }

    public static Validator schemaValidator;

    static {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = null;
        try {
            schema = factory.newSchema(new Source[]{new StreamSource(factory.getClass().getResourceAsStream("/nl/vpro/domain/media/vproShared.xsd")), new StreamSource(factory.getClass().getResourceAsStream("/nl/vpro/domain/media/vproMedia.xsd"))});
        } catch(SAXException e) {
            e.printStackTrace();
        }
        schemaValidator = schema.newValidator();
    }

    public static Program getXmlValidProgram() {
        Program program = new Program(1);
        program.setMid("TEST_00001");
        program.setType(ProgramType.BROADCAST);
        program.setAVType(AVType.AUDIO);
        program.addBroadcaster(new Broadcaster("BROADCASTER", "BROADCASTER"));
        program.setPredictions(null);
        program.addTitle(new Title("Test program", OwnerType.BROADCASTER, TextualType.MAIN));

        program.setCreationDate(new Date(0));

        return program;
    }

    public static List<String> getErrorMessagesFromConstraintViolations(Collection constraintViolations) {
        List<String> messages = new ArrayList<String>();
        for(Iterator it = constraintViolations.iterator(); it.hasNext(); ) {
            ConstraintViolation violation = (ConstraintViolation)it.next();
            messages.add(violation.getMessage());
        }
        return messages;
    }
}
