@XmlSchema(
    namespace = "",
    xmlns =
        {
            @XmlNs(prefix = "", namespaceURI = ""),
            @XmlNs(prefix = "update", namespaceURI = Xmlns.UPDATE_NAMESPACE),
            @XmlNs(prefix = "media",  namespaceURI = Xmlns.MEDIA_NAMESPACE),
            @XmlNs(prefix = "shared", namespaceURI = Xmlns.SHARED_NAMESPACE)

        },
    elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED

)
package nl.vpro.domain.media.update.collections;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
/*
 * XmlCollection does the same as JAXB would do for lists. We use this package info just to define the prefixes for the other namespaces.
 * (MSE-2612)
 */

