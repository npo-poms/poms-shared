/**
 * See https://docs.google.com/document/d/16L_Gp2awzwa3GHOPNIcK-9LNB0iMZgif-JUfFCMOkkg/edit?pref=2&pli=1}
 */
@jakarta.xml.bind.annotation.XmlSchema(namespace = Xmlns.GTAA_NAMESPACE,
    xmlns = {
        @XmlNs(prefix = "rdf",      namespaceURI = Namespaces.RDF),
        @XmlNs(prefix = "skos",     namespaceURI = Namespaces.SKOS),
        @XmlNs(prefix = "skosxl",   namespaceURI = Namespaces.SKOS_XL),
        @XmlNs(prefix = "openskos", namespaceURI = Namespaces.OPEN_SKOS),
        @XmlNs(prefix = "dcterms",  namespaceURI = Namespaces.DC_TERMS),
        @XmlNs(prefix = "dc",       namespaceURI = Namespaces.DC_TERMS_ELEMENTS),
        @XmlNs(prefix = "gtaa",     namespaceURI = Xmlns.GTAA_NAMESPACE),


    },
    elementFormDefault = jakarta.xml.bind.annotation.XmlNsForm.QUALIFIED,
    attributeFormDefault = XmlNsForm.QUALIFIED
)

package nl.vpro.domain.gtaa;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;

import nl.vpro.domain.Xmlns;
import nl.vpro.openarchives.oai.Namespaces;

