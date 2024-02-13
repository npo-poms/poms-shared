@jakarta.xml.bind.annotation.XmlSchema(namespace = Namespaces.RDF,
    elementFormDefault = XmlNsForm.QUALIFIED,
    attributeFormDefault = XmlNsForm.QUALIFIED,
    xmlns = {
        @XmlNs(prefix = "rdf", namespaceURI = Namespaces.RDF),
        @XmlNs(prefix = "", namespaceURI = Namespaces.OAI),
        @XmlNs(prefix = "skos", namespaceURI = Namespaces.SKOS),
        @XmlNs(prefix = "skosxl", namespaceURI = Namespaces.SKOS_XL),
        @XmlNs(prefix = "openskos", namespaceURI = Namespaces.OPEN_SKOS),
        @XmlNs(prefix = "dcterms", namespaceURI = Namespaces.DC_TERMS)

    }
) package nl.vpro.w3.rdf;

import jakarta.xml.bind.annotation.XmlNs;
import jakarta.xml.bind.annotation.XmlNsForm;

import nl.vpro.openarchives.oai.Namespaces;

// See https://docs.google.com/document/d/16L_Gp2awzwa3GHOPNIcK-9LNB0iMZgif-JUfFCMOkkg/edit?pref=2&pli=1
