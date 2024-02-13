@XmlSchema(
    namespace = Xmlns.API_NAMESPACE,
    xmlns = {
        @XmlNs(prefix = "", namespaceURI = Xmlns.API_NAMESPACE),
        @XmlNs(prefix = "media", namespaceURI = Xmlns.MEDIA_NAMESPACE),
        @XmlNs(prefix = "shared", namespaceURI = Xmlns.SHARED_NAMESPACE)
    },
    elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = jakarta.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.api.media;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
