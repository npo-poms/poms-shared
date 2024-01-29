@jakarta.xml.bind.annotation.XmlSchema(namespace = Xmlns.MEDIA_NAMESPACE,
    xmlns =
        {
            @XmlNs(prefix = XMLConstants.DEFAULT_NS_PREFIX, namespaceURI = Xmlns.MEDIA_NAMESPACE),
        },
    elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = jakarta.xml.bind.annotation.XmlNsForm.UNQUALIFIED
) package nl.vpro.domain.media.bind;

import javax.xml.XMLConstants;
import jakarta.xml.bind.annotation.XmlNs;

import nl.vpro.domain.Xmlns;
