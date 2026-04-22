/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page;

import lombok.*;

import java.util.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.AbstractFacet;
import nl.vpro.domain.api.SearchableFacet;
import nl.vpro.domain.api.jackson.page.RelationFacetListJson;

/**
 * @author Michiel Meeuwissen
 * @since 4.1
 */
@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageRelationFacetListType", propOrder = {"filter", "subSearch", "facets"})
@JsonSerialize(using = RelationFacetListJson.Serializer.class)
@JsonDeserialize(using = RelationFacetListJson.Deserializer.class)
public class RelationFacetList extends AbstractFacet<PageSearch> implements SearchableFacet<PageSearch, RelationSearch>, Iterable<RelationFacet> {

    @Valid
    private PageSearch filter;

    @Valid
    private RelationSearch subSearch;

    /**
     * -- GETTER --
     *  Use iterator if you want to initialise the facet names. Clients may supply there own custom name, but
     *  this is optional
     */
    @XmlElement(name = "facet")
    protected List<@Valid @NotNull RelationFacet> facets;

    public RelationFacetList() {
    }

    public RelationFacetList(List<RelationFacet> facets) {
        this.facets = facets;
    }



    @lombok.Builder
    private RelationFacetList(
        @Singular  List<RelationFacet> facets,
        RelationSearch subSearch,
        PageSearch filter
        ) {
        this.facets = facets;
        this.filter = filter;
        this.subSearch = subSearch;
    }


    public boolean isEmpty() {
        return facets == null || facets.isEmpty();
    }

    public int size() {
        return facets == null ? 0 : facets.size();
    }

    @Override
    public boolean hasSubSearch() {
        return subSearch != null && subSearch.hasSearches();
    }

    @NonNull
    @Override
    public Iterator<RelationFacet> iterator() {
        return new Iterator<RelationFacet>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                return facets != null && index < facets.size();
            }

            @Override
            public RelationFacet next() {
                 if (! hasNext()) {
                    throw new NoSuchElementException();
                }
                RelationFacet relationFacet = facets.get(index++);
                if(relationFacet.getName() == null) {
                    relationFacet.setName("relations" + index);
                }
                return relationFacet;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Unsupported");
            }
        };
    }
}
