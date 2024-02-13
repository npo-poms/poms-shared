/**
 * More or less generic image domain objects.
 * <p>
 * Some are more or less poms specific, other could be applied more genericly.
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
package nl.vpro.domain.image;

import jakarta.xml.bind.annotation.XmlNs;

import nl.vpro.domain.Xmlns;
