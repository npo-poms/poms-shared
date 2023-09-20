/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.support.Tag;

/**
 * @author Roelof Jan Koekoek
 * @since 3.6
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bulkUpdateType", propOrder = {
    "titles",
    "descriptions",
    "tags"
})
public class BulkUpdate {

    private List<TitleUpdate> titles;

    private List<DescriptionUpdate> descriptions;

    private List<Tag> tags;

    public List<TitleUpdate> getTitles() {
        return titles;
    }

    public void addTitle(TitleUpdate title) {
        if(titles == null) {
            titles = new ArrayList<>();
        }
        titles.add(title);
    }

    public void setTitles(List<TitleUpdate> titles) {
        this.titles = titles;
    }

    public List<DescriptionUpdate> getDescriptions() {
        return descriptions;
    }

    public void addDescription(DescriptionUpdate description) {
        if(descriptions == null) {
            descriptions = new ArrayList<>();
        }
        descriptions.add(description);
    }

    public void setDescriptions(List<DescriptionUpdate> descriptions) {
        this.descriptions = descriptions;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTag(Tag tag) {
        if(tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(tag);
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
}
