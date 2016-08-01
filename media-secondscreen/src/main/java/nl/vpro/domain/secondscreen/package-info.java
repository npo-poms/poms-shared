@XmlSchema(namespace = Xmlns.SECOND_SCREEN_NAMESPACE,
    xmlns=
        {
            @XmlNs(prefix = "screen",       namespaceURI = Xmlns.SECOND_SCREEN_NAMESPACE),
            @XmlNs(prefix = "shared", namespaceURI = Xmlns.SHARED_NAMESPACE)
        },
    elementFormDefault = XmlNsForm.QUALIFIED,
    attributeFormDefault = XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.secondscreen;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
