@XmlSchema(
    xmlns =
        {
            @XmlNs(
                prefix = "",
                namespaceURI = Xmlns.MEDIA_SUBTITLES_NAMESPACE)

        },
    namespace = Xmlns.MEDIA_SUBTITLES_NAMESPACE,
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED
)
package nl.vpro.domain.subtitles;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
