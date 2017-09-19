/*
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.gtaa;

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
    
    CountedIterator<Record> getAllUpdates(Instant from, Instant until);

    Description submit(String prefLabel, List<Label> notes, String creator);

    List<Description> findPersons(String input, Integer max);

    GTAAPerson submit(GTAAPerson person, String creator);

    List<Description> findAnything(String input, Integer max);

    Optional<Description> retrieveItemStatus(String id);
}
