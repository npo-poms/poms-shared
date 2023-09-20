/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa;

import lombok.Data;

import java.net.URI;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import nl.vpro.openarchives.oai.Namespaces;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@Data
public abstract class AbstractGTAAObject {

    private UUID uuid;

    @XmlAttribute(name = "about", namespace = Namespaces.RDF)
    private URI about;

    protected AbstractGTAAObject() {
    }

    protected AbstractGTAAObject(UUID uuid, URI about) {
        this.uuid = uuid;
        this.about = about;
    }

    protected static class AbstractBuilder<T extends AbstractBuilder<T>> {

        UUID uuid;
        URI about;

        public T uuid(UUID uuid) {
            this.uuid = uuid;
            return (T) this;
        }

        public T about(URI a) {
            this.about = a;
            return (T) this;
        }

    }
}
