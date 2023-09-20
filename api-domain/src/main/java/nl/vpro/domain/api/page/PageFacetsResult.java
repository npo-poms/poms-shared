/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import lombok.Data;
import nl.vpro.domain.api.DateFacetResultItem;
import nl.vpro.domain.api.MultipleFacetsResult;
import nl.vpro.domain.api.TermFacetResultItem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageFacetsResultType",
        propOrder = {
                "sortDates",
                "types",
                "broadcasters",
                "tags",
                "keywords",
                "genres",
                "portals",
                "sections",
                "relations"
        })
@Data
public class PageFacetsResult {

    private List<DateFacetResultItem> sortDates;

    private List<TermFacetResultItem> broadcasters;

    private List<TermFacetResultItem> types;

    private List<TermFacetResultItem> tags;

    private List<TermFacetResultItem> keywords;

    private List<GenreFacetResultItem> genres;

    private List<TermFacetResultItem> portals;

    private List<TermFacetResultItem> sections;

    private List<MultipleFacetsResult> relations;

    List<TermFacetResultItem> getBroadcasters(PageForm form) {
        if (form.getSearches() != null && form.getSearches().getBroadcasters() != null && broadcasters == null) {
            broadcasters = new ArrayList<>();
        }
        return broadcasters;
    }

    List<GenreFacetResultItem> getGenres(PageForm form) {
        if (form.getSearches() != null && form.getSearches().getGenres() != null && genres == null) {
            genres = new ArrayList<>();
        }
        return genres;
    }

    List<TermFacetResultItem> getTags(PageForm form) {
        if (form.getSearches() != null && form.getSearches().getTags() != null && tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    List<TermFacetResultItem> getKeywords(PageForm form) {
        if (form.getSearches() != null && form.getSearches().getKeywords() != null && keywords == null) {
            keywords = new ArrayList<>();
        }
        return keywords;
    }

    List<TermFacetResultItem> getTypes(PageForm form) {
        if (form.getSearches() != null && form.getSearches().getTypes() != null && types == null) {
            types = new ArrayList<>();
        }
        return types;
    }

    List<TermFacetResultItem> getPortals(PageForm form) {
        if (form.getSearches() != null && form.getSearches().getPortals() != null && portals == null) {
            portals = new ArrayList<>();
        }
        return portals;
    }

    List<TermFacetResultItem> getSections(PageForm form) {
        if (form.getSearches() != null && form.getSearches().getSections() != null && sections == null) {
            sections = new ArrayList<>();
        }
        return sections;
    }

    List<DateFacetResultItem> getSortDates(PageForm form) {
        if (form.getSearches() != null && form.getSearches().getSortDates() != null && sortDates == null) {
            sortDates = new ArrayList<>();
        }
        return sortDates;
    }

    List<MultipleFacetsResult> getRelations(PageForm form) {
        if (form.getSearches() != null && form.getSearches().getRelations() != null && relations == null) {
            relations = new ArrayList<>();
        }
        return relations;
    }
}
