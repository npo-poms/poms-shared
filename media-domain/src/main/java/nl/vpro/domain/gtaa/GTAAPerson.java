/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.PersonInterface;
import nl.vpro.domain.media.Person;
import nl.vpro.openarchives.oai.MetaData;
import nl.vpro.validation.NoHtml;
import nl.vpro.w3.rdf.Description;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Slf4j
@XmlType(propOrder = {
    "value",
    "givenName",
    "familyName",
    "notes",
    "knownAs",
    "redirectedFrom"

})
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
@AllArgsConstructor
@Builder
@XmlRootElement(name = "person")
@GTAAScheme(Scheme.PERSOONSNAMEN)
public class GTAAPerson extends AbstractThesaurusItem implements  PersonInterface, Serializable {

    private static final long serialVersionUID = 1L;

    @NoHtml
    @XmlElement
    @Getter
    @Setter
    protected String givenName;

    @NoHtml
    @XmlElement
    @Getter
    @Setter
    protected String familyName;


    @Getter
    @Setter
    private List<Names> knownAs;


    public GTAAPerson() {

    }

    public GTAAPerson(Person person) {
        this.givenName = person.getGivenName();
        this.familyName = person.getFamilyName();
        status = person.getGtaaRecord() == null ? null : person.getGtaaRecord().getStatus();
    }

    public GTAAPerson(GTAANewPerson newPerson) {
        this(Person.builder()
            .givenName(newPerson.getGivenName())
            .familyName(newPerson.getFamilyName())
            .build());
        this.setNotes(newPerson.getNotesAsLabel());
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
    public String getValue() {
        return familyName + (givenName == null ? "":  ", " + givenName);
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

        answer.notes = description.getScopeNote();
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
        return getId();

    }
}
