@XmlSchema(
        namespace = Xmlns.MEDIA_CONSTRAINT_NAMESPACE,
        xmlns = {
            @XmlNs(prefix = "media", namespaceURI = Xmlns.MEDIA_CONSTRAINT_NAMESPACE),
            @XmlNs(prefix = "constraint", namespaceURI = Xmlns.CONSTRAINT_NAMESPACE),
            @XmlNs(prefix = "m", namespaceURI = Xmlns.MEDIA_NAMESPACE)
        },
        elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        attributeFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.constraint.media;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
