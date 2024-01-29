@XmlSchema(
        namespace = Xmlns.PROFILE_NAMESPACE,
        xmlns = {
            @XmlNs(prefix = "",        namespaceURI = Xmlns.PROFILE_NAMESPACE),
            @XmlNs(prefix = "media",   namespaceURI = Xmlns.MEDIA_CONSTRAINT_NAMESPACE),
            @XmlNs(prefix = "page",    namespaceURI = Xmlns.PAGE_CONSTRAINT_NAMESPACE),
            @XmlNs(prefix = "shared",  namespaceURI = Xmlns.SHARED_NAMESPACE),
        },
        elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        attributeFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.api.profile;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
