/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.domain.media.*;
import nl.vpro.domain.page.Page;

/**
 * @author rico
 * @author Michiel Meeuwissen
 */
@XmlAccessorType(XmlAccessType.FIELD)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"score", "highlights", "result"})
public class SearchResultItem<T extends Serializable> implements Serializable {

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Program.class, name = "program"),
        @JsonSubTypes.Type(value = Group.class, name = "group"),
        @JsonSubTypes.Type(value = Segment.class, name = "segment"),
        @JsonSubTypes.Type(value = Page.class, name = "page")
    })
    @Getter
    @Setter
    private T result;

    @XmlAttribute
    @Getter
    @Setter
    private Float score;

    @XmlElement(name = "highlight")
    @JsonProperty(value = "highlights")
    private List<HighLight> highlights;

    public SearchResultItem() {
    }

    public SearchResultItem(T item) {
        this.result = item;
    }

    public SearchResultItem(T result, Float score, List<HighLight> highlights) {
        this.result = result;
        this.score = score;
        this.highlights = highlights;
    }

    public List<HighLight> getHighlights() {
        if(highlights == null) {
            highlights = new ArrayList<>();
        }
        return highlights;
    }

    public void setHighlights(List<HighLight> highlights) {
        this.highlights = highlights;
    }

    @Override
    public String toString() {
        return getResult() + ":" + getScore();
    }

}
