/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.gtaa;

import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import nl.vpro.openarchives.oai.Record;
import nl.vpro.util.CountedIterator;
import nl.vpro.w3.rdf.Description;

/**
 * @author Roelof Jan Koekoek
 * @since 3.7
 */
public interface GTAARepository {

    CountedIterator<Record> getPersonUpdates(Instant from, Instant until);

    CountedIterator<Record> getGeoLocationsUpdates(Instant from, Instant until);

    CountedIterator<Record> getAllUpdates(Instant from, Instant until);

    List<Description> findPersons(String input, Integer max);

    <T extends GTAAConcept, S extends GTAANewConcept>  T  submit(S thesaurusObject, String creator);

    List<Description> findAnything(String input, Integer max);

    List<Description> findForSchemes(String input, Integer max, SchemeOrNot... schemes);

    Optional<Description> retrieveConceptStatus(String id);

    Optional<GTAAConcept> get(String id);


    @Getter
    class SchemeOrNot {
        final String scheme;
        final boolean not;

        public SchemeOrNot(String scheme, boolean not) {
            this.scheme = scheme;
            this.not = not;
        }
        public SchemeOrNot(String scheme) {
            this(scheme, false);
        }
        public SchemeOrNot(Scheme scheme) {
            this(scheme.getUrl());
        }

        public static SchemeOrNot of(Scheme scheme) {
            return new SchemeOrNot(scheme);
        }

        public SchemeOrNot not() {
            return new SchemeOrNot(scheme, ! not);
        }

        @Override
        public String toString() {
            return (not ? "!" : "") + scheme;
        }
    }
}
