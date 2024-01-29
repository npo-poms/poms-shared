/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@jakarta.xml.bind.annotation.XmlSchema(namespace = Xmlns.GTAA_NAMESPACE,
    xmlns = {
        @XmlNs(prefix = "gtaa", namespaceURI = Xmlns.GTAA_NAMESPACE),
    },
    elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = XmlNsForm.QUALIFIED
)
package nl.vpro.rs.thesaurus.update;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;

import nl.vpro.domain.Xmlns;
