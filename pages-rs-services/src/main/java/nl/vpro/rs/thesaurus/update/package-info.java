/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@javax.xml.bind.annotation.XmlSchema(namespace = Xmlns.GTAA_NAMESPACE,
    xmlns = {
        @XmlNs(prefix = "gtaa", namespaceURI = Xmlns.GTAA_NAMESPACE),
    },
    elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = XmlNsForm.QUALIFIED
)
package nl.vpro.rs.thesaurus.update;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;

import nl.vpro.domain.Xmlns;
