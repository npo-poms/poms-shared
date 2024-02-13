/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import jakarta.validation.ConstraintViolation;
import jakarta.xml.bind.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.xml.sax.SAXException;

import nl.vpro.domain.ValidationTestHelper;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;

@Slf4j
public class MediaDomainTestHelper extends ValidationTestHelper {
    private static JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = JAXBContext.newInstance("nl.vpro.domain.media");
        } catch(JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Unmarshaller unmarshaller;

    static {
        try {
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch(JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Marshaller marshaller;

    static {
        try {
            marshaller = jaxbContext.createMarshaller();
        } catch(JAXBException e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Validator schemaValidator;

    static {
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        try {
            Schema schema = factory.newSchema(new Source[]{
                new StreamSource(MediaObject.class.getResourceAsStream("/nl/vpro/domain/media/w3/xml.xsd")),
                new StreamSource(MediaObject.class.getResourceAsStream("/nl/vpro/domain/media/vproShared.xsd")),
                new StreamSource(MediaObject.class.getResourceAsStream("/nl/vpro/domain/media/vproMedia.xsd"))});
            schemaValidator = schema.newValidator();
        } catch(SAXException e) {
            log.error(e.getMessage(), e);
        }

    }

    public static Program getXmlValidProgram() {
        Program program = new Program(1);
        program.setMid("TEST_00001");
        program.setType(ProgramType.BROADCAST);
        program.setAVType(AVType.AUDIO);
        program.addBroadcaster(new Broadcaster("BROADCASTER", "BROADCASTER"));
        program.setPredictions(null);
        program.addTitle(new Title("Test program", OwnerType.BROADCASTER, TextualType.MAIN));

        program.setCreationInstant(Instant.ofEpochMilli(0));

        return program;
    }

    public static List<String> getErrorMessagesFromConstraintViolations(Collection constraintViolations) {
        List<String> messages = new ArrayList<>();
        for (Object constraintViolation : constraintViolations) {
            ConstraintViolation violation = (ConstraintViolation) constraintViolation;
            messages.add(violation.getMessage());
        }
        return messages;
    }
}
