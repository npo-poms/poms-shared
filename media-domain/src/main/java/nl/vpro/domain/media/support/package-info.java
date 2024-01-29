@jakarta.xml.bind.annotation.XmlSchema(namespace = Xmlns.MEDIA_NAMESPACE,
    xmlns=
        {
            @XmlNs(prefix = "",       namespaceURI = Xmlns.MEDIA_NAMESPACE),
            @XmlNs(prefix = "shared", namespaceURI = Xmlns.SHARED_NAMESPACE)
        },
    elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = jakarta.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.media.support;

import jakarta.xml.bind.annotation.XmlNs;

import nl.vpro.domain.Xmlns;
