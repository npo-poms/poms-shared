package nl.vpro.domain.media.gtaa;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import nl.vpro.openarchives.oai.Label;


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
@JsonSubTypes(
    {
        @JsonSubTypes.Type(value = GTAAPerson.class, name = "person"),
        @JsonSubTypes.Type(value = GTAATopic.class, name = "topic"),
        @JsonSubTypes.Type(value = ThesaurusItem.class, name = "item"),
    })
public interface ThesaurusObject {

    List<Label> getNotes();

    Instant getLastModified();

    String getValue();

    String getId();

    Status getStatus();

    String getRedirectedFrom();

}
