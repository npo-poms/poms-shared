@XmlSchema(namespace = Xmlns.PAGEUPDATE_NAMESPACE,
    xmlns = {
        @XmlNs(prefix = "pageUpdate", namespaceURI = Xmlns.PAGEUPDATE_NAMESPACE),
        @XmlNs(prefix = "shared",     namespaceURI = Xmlns.SHARED_NAMESPACE),
        @XmlNs(prefix = "page",       namespaceURI = Xmlns.PAGE_NAMESPACE)
    },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED
) package nl.vpro.domain.page.update;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
