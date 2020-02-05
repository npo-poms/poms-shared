/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.w3.rdf;

import lombok.*;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.gtaa.AbstractGTAAObject;
import nl.vpro.domain.gtaa.Scheme;
import nl.vpro.domain.gtaa.Status;
import nl.vpro.domain.gtaa.XLLabel;
import nl.vpro.dublincore.terms.Date;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.openarchives.oai.Note;
import nl.vpro.xml.bind.ZonedDateTimeXmlAdapter;

import static nl.vpro.openarchives.oai.Namespaces.*;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(propOrder = {
    "type",
    "editorialNote",
    "creatorResource",
    "modifiedBy",
    "prefLabel",
    "xlPrefLabel",
    "modified",
    "uuid",
    "set",
    "inScheme",
    "notation",
    "creator",
    "status",
    "tenant",
    "acceptedBy",
    "dateAccepted",
    "dateSubmitted",
    "hiddenLabels",
    "xlHiddenLabels",
    "historyNote",
    "altLabels",
    "xlAltLabels",
    "scopeNote",
    "changeNote"
})
@ToString
@Data
@EqualsAndHashCode(callSuper = true)
@lombok.Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Description extends AbstractGTAAObject {

    private static final String FORWARD = "Forward:";

    @XmlElement
    private ResourceElement type;

    @XmlElement(namespace = OPEN_SKOS)
    private Status status;

    @XmlElement(namespace = OPEN_SKOS)
    private String tenant;

    @XmlElement(namespace = OPEN_SKOS)
    private ResourceElement modifiedBy;

    @XmlElement(namespace = OPEN_SKOS)
    private String acceptedBy;

    @XmlElement(namespace = SKOS)
    private String notation;

    @XmlElement(namespace = DC_TERMS)
    @XmlJavaTypeAdapter(ZonedDateTimeXmlAdapter.class)
    private ZonedDateTime dateAccepted;

    @XmlElement(namespace = DC_TERMS_ELEMENTS)
    private String creator;

    @XmlElement(namespace = DC_TERMS, name = "creator")
    private ResourceElement creatorResource;

    @XmlElement(namespace = DC_TERMS)
    @XmlJavaTypeAdapter(ZonedDateTimeXmlAdapter.class)
    private ZonedDateTime dateSubmitted;

    @XmlElement(namespace = DC_TERMS)
    private Date modified;

    @XmlElement(namespace = OPEN_SKOS)
    private UUID uuid;

    @XmlElement(namespace = OPEN_SKOS)
    private ResourceElement set;

    @XmlElement(namespace = SKOS)
    private ResourceElement inScheme;

    @XmlElement(namespace = SKOS, name = "prefLabel")
    private Label prefLabel;

    @XmlElement(namespace = SKOS_XL, name = "prefLabel")
    private XLLabel xlPrefLabel;

    @XmlElement(namespace = SKOS, name = "hiddenLabel")
    @Singular
    private List<Label> hiddenLabels;

    @XmlElement(namespace = SKOS_XL, name = "hiddenLabel")
    @Singular
    private List<XLLabel> xlHiddenLabels;

    @XmlElement(namespace = SKOS)
    private Note historyNote;

    @XmlElement(namespace = SKOS, name = "altLabel")
    private List<Label> altLabels;

    @XmlElement(namespace = SKOS_XL, name = "altLabel")
    @Singular
    private List<XLLabel> xlAltLabels;

    @XmlElement(namespace = SKOS)
    private List<String> changeNote;

    @XmlElement(namespace = SKOS)
    private List<Label> scopeNote;

    @XmlElement(namespace = SKOS)
    private List<Label> editorialNote;

    public Description() {
    }

    public Description(String prefLabel) {
        this.prefLabel = new Label(prefLabel);
    }

    public boolean isPerson() {
        return getSimpleType().equals(Scheme.person.getId());
    }

    public boolean isGeoLocation() {
        return getSimpleType().equals(Scheme.geographicname.getId());
    }

    public String getSimpleType() {
        return StringUtils.substringAfterLast(getInScheme().getResource(), "/");
    }

    public Optional<URI> getRedirectedFrom() {
        return getCleanChangeNote().filter(note -> StringUtils.contains(note, FORWARD)).map(note -> StringUtils.substringAfter(note, FORWARD)).map(URI::create).findAny();
    }

    private Stream<String> getCleanChangeNote() {
        return getChangeNote().stream().map(StringUtils::deleteWhitespace);
    }

    public static class Builder extends AbstractBuilder<Builder> {

        public Builder type(String type) {
            this.type = ResourceElement.builder().resource(type).build();
            return this;
        }

        public Builder inScheme(String scheme) {
            this.inScheme = ResourceElement.builder().resource(scheme).build();
            return this;
        }

        public Builder inScheme(Scheme scheme) {
            this.inScheme = ResourceElement.builder().resource(scheme.getUrl()).build();
            return this;
        }

        public Builder prefLabelOrXL(boolean useXlLablel, String label, String tenant) {
            if (useXlLablel) {
                return xlPrefLabel(new XLLabel(label, tenant));
            } else {
                return prefLabel(new Label(label));
            }
        }

    }

    public void addChangeNote(String note) {
        if (changeNote == null) {
            this.changeNote = new ArrayList<>();
        }
        this.changeNote.add(note);
    }
}
