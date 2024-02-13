@XmlSchema(namespace = Xmlns.PAGE_NAMESPACE,
        xmlns = {
            @XmlNs(prefix = "pages", namespaceURI = Xmlns.PAGE_NAMESPACE),
            @XmlNs(prefix = "media", namespaceURI = Xmlns.MEDIA_NAMESPACE),
            @XmlNs(prefix = "shared", namespaceURI = Xmlns.SHARED_NAMESPACE)

},
        elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED
)
package nl.vpro.domain.page;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
