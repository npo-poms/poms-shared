@XmlSchema(
    namespace = Xmlns.API_NAMESPACE,
    xmlns = {
        @XmlNs(prefix = "", namespaceURI = Xmlns.API_NAMESPACE),
        @XmlNs(prefix = "media", namespaceURI = Xmlns.MEDIA_NAMESPACE),
        @XmlNs(prefix = "shared", namespaceURI = Xmlns.SHARED_NAMESPACE)
    },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.api.media;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
