/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.PersonInterface;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.openarchives.oai.MetaData;
import nl.vpro.validation.NoHtml;
import nl.vpro.w3.rdf.Description;

/**
 * A representation of the gtaa concept {@link Scheme#person}.
 *
 * We have the small distinction what we try to make sense of {@link #givenName} and {@link #familyName}. It's doubtful that this is culturally neutral, but for the use cases at hand it's probably sensible.
 *
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Slf4j
@XmlType(
    name = "person",
    propOrder = {
        "name",
    "givenName",
    "familyName",
    "scopeNotes",
    "knownAs",
    "redirectedFrom"

})
@JsonPropertyOrder({
    "objectType",
    "id",
    "value",
    "givenName",
    "familyName",
    "scopeNotes",
    "knownAs",
    "redirectedFrom"
}

)
@XmlAccessorType(XmlAccessType.NONE)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@XmlRootElement(name = "person")
@GTAAScheme(Scheme.person)
public class GTAAPerson extends AbstractGTAAConcept implements  PersonInterface, Serializable {

    private static final long serialVersionUID = 1L;

    @NoHtml
    @Getter
    @Setter
    @XmlElement
    protected String givenName;

    @NoHtml
    @Getter
    @Setter
    @XmlElement
    protected String familyName;


    @Getter
    @Setter
    @XmlElement
    private List<Names> knownAs;


    public GTAAPerson() {

    }

    @lombok.Builder(builderClassName = "Builder")
    public GTAAPerson(URI id, @Singular  List<String> scopeNotes, String value, URI redirectedFrom, Status status, Instant lastModified, @NoHtml String givenName, @NoHtml String familyName, List<Names> knownAs) {
        super(id, scopeNotes, value, redirectedFrom, status, lastModified);
        this.givenName = givenName;
        this.familyName = familyName;
        this.knownAs = knownAs;
    }

    public GTAAPerson(String givenName, String familyName, Status status) {

        super();
        this.givenName = givenName;
        this.familyName = familyName;
        this.status = status;
    }

    public GTAAPerson(GTAANewPerson newPerson) {
        this(newPerson.getGivenName(), newPerson.getFamilyName(), (Status) null);
        this.setScopeNotes(newPerson.getScopeNotes());
    }

    public static GTAAPerson create(MetaData metaData) {
        if (metaData == null) {
            log.info("No metadata");
            return null;
        }
        return create(metaData.getFirstDescription());
    }
    public static GTAAPerson create(Description description) {

        return create(description, null);
    }

    @Override
    @XmlElement
    public String getName() {
        return PersonInterface.stringValue(givenName, familyName);
    }

    @Override
    public void setName(String v) {
        // ignore
    }



    public static GTAAPerson create(Description description, String submittedPrefLabel) {
        if (description == null) {
            log.info("Description is null");
            return null;
        }

        final GTAAPerson answer = new GTAAPerson();

        final Names prefName;
        if (description.getPrefLabel() != null) {
            String label = description.getPrefLabel().getValue();
            if (submittedPrefLabel != null && !submittedPrefLabel.equals(label)) {
                log.warn("Using different submitted label {} in stead of {}", submittedPrefLabel, label);
                label = submittedPrefLabel;
            }
            prefName = Names.of(label);
        } else {
            prefName = Names.of(submittedPrefLabel);
            log.warn("Description has no prefLabel {}. Taking it {}", description, prefName);

        }
        if (prefName != null) {
            answer.givenName = prefName.getGivenName();
            answer.familyName = prefName.getFamilyName();
        }

        answer.scopeNotes = description.getScopeNote() == null ? null : description.getScopeNote().stream().map(Label::getValue).collect(Collectors.toList());
        answer.lastModified = description.getModified() == null ? null : description.getModified().getValue().toInstant();

        if (description.getAltLabels() != null && !description.getAltLabels().isEmpty()) {
            final List<Names> altNames = description.getAltLabels().stream().map(Names::of)
                    .collect(Collectors.toList());

            if (answer.knownAs == null) {
                answer.knownAs = altNames;
            } else {
                answer.knownAs.addAll(altNames);
            }
        }

        answer.setStatus(description.getStatus());
        answer.setId(description.getAbout());

        return answer;
    }

    @Override
    public String getGtaaUri() {
        return getId().toString();
    }

    public static class Builder {

        public Builder gtaaUri(String id) {
            return id(URI.create(id));
        }

    }

}
