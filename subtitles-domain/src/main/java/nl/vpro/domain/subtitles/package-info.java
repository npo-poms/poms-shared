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
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED

)
package nl.vpro.domain.subtitles;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
