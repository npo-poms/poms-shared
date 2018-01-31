/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.PersonInterface;
import nl.vpro.domain.media.Person;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.openarchives.oai.Label;
import nl.vpro.openarchives.oai.MetaData;
import nl.vpro.validation.NoHtml;
import nl.vpro.w3.rdf.Description;
import nl.vpro.xml.bind.InstantXmlAdapter;

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
@EqualsAndHashCode
@ToString
@AllArgsConstructor
@Builder
@XmlRootElement(name = "person")
public class GTAAPerson implements ThesaurusObject, PersonInterface, Serializable {

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
    private List<Label> notes;

    @Getter
    @Setter
    private List<Names> knownAs;

    @Getter
    @Setter
    @XmlAttribute
    private Status status;

    @Getter
    @Setter
    private String redirectedFrom;

    @Getter
    @Setter
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant lastModified;

    @Getter
    @Setter
    @XmlAttribute
    private String gtaaUri;

    public GTAAPerson() {

    }

    public GTAAPerson(Person person) {
        this.givenName = person.getGivenName();
        this.familyName = person.getFamilyName();
        status = person.getGtaaRecord() == null ? null : person.getGtaaRecord().getStatus();
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

    @Override
    @XmlAttribute
    public String getId() {
        return gtaaUri;
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
        answer.setGtaaUri(description.getAbout());

        return answer;
    }

    public String getPrefLabel() {
        return familyName + (givenName != null ? ", " + givenName  : "");
    }

}
