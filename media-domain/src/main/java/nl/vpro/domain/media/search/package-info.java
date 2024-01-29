@XmlSchema(
    xmlns =
        {
            @XmlNs(
                prefix = "s",
                namespaceURI = Xmlns.SEARCH_NAMESPACE),
            @XmlNs(
                prefix = XMLConstants.DEFAULT_NS_PREFIX,
                namespaceURI = Xmlns.MEDIA_NAMESPACE),
            /* I'd prefer this, but I can't get MediaListTest to work in java 8 then.
            @XmlNs(
                prefix = "",
                namespaceURI = Xmlns.SEARCH_NAMESPACE),
            @XmlNs(
                prefix = "media",
                namespaceURI = Xmlns.MEDIA_NAMESPACE),
                */
            @XmlNs(
                prefix = "update",
                namespaceURI = Xmlns.UPDATE_NAMESPACE),
            @XmlNs(
                prefix = "shared",
                namespaceURI = Xmlns.SHARED_NAMESPACE),
            @XmlNs(
                prefix = "xsd",
                namespaceURI = XMLConstants.W3C_XML_SCHEMA_NS_URI
            )
        },
    namespace = Xmlns.SEARCH_NAMESPACE,
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED
)
package nl.vpro.domain.media.search;

import javax.xml.XMLConstants;
import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
