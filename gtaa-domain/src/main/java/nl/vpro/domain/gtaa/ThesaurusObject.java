package nl.vpro.domain.gtaa;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import nl.vpro.openarchives.oai.Label;


@JsonTypeInfo(
    use = JsonTypeInfo.Id.CUSTOM,
    property = "objectType"
)
@JsonTypeIdResolver(ThesaurusObjectIdResolver.class)
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

    })

public interface ThesaurusObject {


    String getId();

    List<Label> getNotes();

    Instant getLastModified();

    String getValue();

    Status getStatus();

    String getRedirectedFrom();


}
