@XmlSchema(
    xmlns =
        {
            @XmlNs(
                prefix = "subtitles",
                namespaceURI = Xmlns.MEDIA_SUBTITLES_NAMESPACE),
            @XmlNs(
                prefix = "shared",
                namespaceURI = Xmlns.SHARED_NAMESPACE)

        },
    namespace = Xmlns.MEDIA_SUBTITLES_NAMESPACE,
    elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = jakarta.xml.bind.annotation.XmlNsForm.UNQUALIFIED

)
package nl.vpro.domain.subtitles;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
