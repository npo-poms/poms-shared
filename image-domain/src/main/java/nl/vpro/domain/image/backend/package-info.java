/**
 * Image domain that are used by the image server itself.
 */
@jakarta.xml.bind.annotation.XmlSchema(namespace = Xmlns.IMAGE_NAMESPACE,
    xmlns=
        {
            @XmlNs(prefix = "image",       namespaceURI = Xmlns.IMAGE_NAMESPACE),
            @XmlNs(prefix = "shared", namespaceURI = Xmlns.SHARED_NAMESPACE)
        },
    elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = jakarta.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.image.backend;

import jakarta.xml.bind.annotation.XmlNs;
import nl.vpro.domain.Xmlns;
