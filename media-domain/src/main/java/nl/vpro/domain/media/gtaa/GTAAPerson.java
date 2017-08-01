/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Person;
import nl.vpro.domain.media.gtaa.Label;
import nl.vpro.w3.rdf.Description;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
@Slf4j
@XmlType
public class GTAAPerson extends Person {

    private static final long serialVersionUID = 1L;

    @Getter
    private List<Label> notes;

    @Getter
    private List<Names> knownAs;

    public static GTAAPerson create(Description description) {
        return create(description, null);
    }

    @XmlElement
    public Status getStatus() {
        return this.gtaaRecord != null ? this.gtaaRecord.getStatus() : null;
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
            log.warn("Description has no prefLabel {}", description);
            prefName = Names.of(submittedPrefLabel);
        }
        if (prefName != null) {
            answer.givenName = prefName.getGivenName();
            answer.familyName = prefName.getFamilyName();
        }

        answer.notes = description.getScopeNote();

        if (description.getAltLabels() != null && !description.getAltLabels().isEmpty()) {
            final List<Names> altNames = description.getAltLabels().stream().map(Names::of)
                    .collect(Collectors.toList());

            if (answer.knownAs == null) {
                answer.knownAs = altNames;
            } else {
                answer.knownAs.addAll(altNames);
            }
        }

        answer.setGtaaRecord(new GTAARecord(description.getAbout(), description.getStatus()));

        return answer;
    }

    @AllArgsConstructor
    @Data
    @Builder
    public static class Names {

        protected final String givenName;
        protected final String familyName;

        private static Names of(Label label) {
            if (label == null) {
                return null;
            }
            return of(label.getValue());
        }

        private static Names of(String label) {
            if (label == null) {
                return null;
            }
            Names.NamesBuilder names = Names.builder();
            int splitIndex = label.indexOf(", ");

            if (splitIndex > 0) {
                names.givenName(label.substring(splitIndex + 2));
                names.familyName(label.substring(0, splitIndex));
            } else {
                names.familyName(label);
            }
            return names.build();
        }

    }
}
