@XmlSchema(
    namespace = Xmlns.UPDATE_NAMESPACE,
    xmlns =
        {
            @XmlNs(prefix = "",      namespaceURI = Xmlns.UPDATE_NAMESPACE),
            @XmlNs(prefix = "media", namespaceURI = Xmlns.MEDIA_NAMESPACE),
            @XmlNs(prefix = "shared",namespaceURI = Xmlns.SHARED_NAMESPACE)
        },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED

)
package nl.vpro.domain.media.update;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
