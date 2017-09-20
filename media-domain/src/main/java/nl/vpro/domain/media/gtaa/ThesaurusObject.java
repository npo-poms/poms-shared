package nl.vpro.domain.media.gtaa;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlSeeAlso;

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
@XmlSeeAlso({GTAAPerson.class, ThesaurusItem.class})

public interface ThesaurusObject {

    String getId();

    List<Label> getNotes();

    Instant getLastModified();

    String getValue();

    Status getStatus();

    String getRedirectedFrom();

}
