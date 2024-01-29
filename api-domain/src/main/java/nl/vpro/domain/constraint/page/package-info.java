@XmlSchema(
        namespace = Xmlns.PAGE_CONSTRAINT_NAMESPACE,
        xmlns = {
            @XmlNs(prefix = "page", namespaceURI = Xmlns.PAGE_CONSTRAINT_NAMESPACE)
        },
        elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
        attributeFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED
)
package nl.vpro.domain.constraint.page;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlSchema;

import nl.vpro.domain.Xmlns;
