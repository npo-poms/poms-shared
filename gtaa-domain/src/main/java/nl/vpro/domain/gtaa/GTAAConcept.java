package nl.vpro.domain.gtaa;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;


/**
 * These are the data transfer objects that we use for thesaurus objects
 * <p>
 * They are
 * <ul>
 *  <li>The 'POMS' representation of objects in the thesaurus, the frontend API directly maps the second to the first </li>
 *  <li>In the POMS backend there is also a database representation of these objects (called GTAARecord)</li>
 * </ul>
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    property = "objectType"
)
@JsonTypeIdResolver(GTAAConceptIdResolver.class)
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = GTAAPerson.class),
        @JsonSubTypes.Type(value = GTAATopic.class),
        @JsonSubTypes.Type(value = GTAATopicBandG.class),
        @JsonSubTypes.Type(value = GTAAGenre.class),
        @JsonSubTypes.Type(value = GTAAGeographicName.class),
        @JsonSubTypes.Type(value = GTAAMaker.class),
        @JsonSubTypes.Type(value = GTAAName.class),
        @JsonSubTypes.Type(value = GTAAClassification.class),
        @JsonSubTypes.Type(value = GTAAGenreFilmMuseum.class)
    })

public sealed interface GTAAConcept permits AbstractGTAAConcept {


    URI getId();

    List<String> getScopeNotes();

    Instant getLastModified();

    String getName();

    Status getStatus();

    URI getRedirectedFrom();


}
