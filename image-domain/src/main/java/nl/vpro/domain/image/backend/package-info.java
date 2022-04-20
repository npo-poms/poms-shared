/**
 * More or less generic image domain objects.
 *
 * Some are more less poms specific, other could be applied more genericly.
 */
@javax.xml.bind.annotation.XmlSchema(namespace = Xmlns.IMAGE_NAMESPACE,
    xmlns=
        {
            @XmlNs(prefix = "image",       namespaceURI = Xmlns.IMAGE_NAMESPACE),
            @XmlNs(prefix = "shared", namespaceURI = Xmlns.SHARED_NAMESPACE)
        },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.image.backend;

import javax.xml.bind.annotation.XmlNs;
import nl.vpro.domain.Xmlns;
