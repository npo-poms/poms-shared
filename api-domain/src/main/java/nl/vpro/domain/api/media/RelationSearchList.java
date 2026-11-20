package nl.vpro.domain.api.media;


import java.util.*;
import java.util.function.Predicate;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.api.jackson.RelationSearchListJson;
import nl.vpro.domain.media.Relation;
import nl.vpro.util.IterableWrapper;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaRelationSearchListType")
@JsonSerialize(using = RelationSearchListJson.Serializer.class)
@JsonDeserialize(using = RelationSearchListJson.Deserializer.class)
public class RelationSearchList implements Predicate<Relation>, IterableWrapper<RelationSearch> {

    @XmlElement(name = "relationSearch")
    protected List<@Valid RelationSearch> relationSearches = new ArrayList<>();

    public RelationSearchList() {
        // jaxb needs empty constructor
    }


    public RelationSearchList(RelationSearch... relationSearches) {
        this.relationSearches.addAll(Arrays.asList(relationSearches));
    }


    @Override
    public List<RelationSearch> asList() {
        return relationSearches;
    }

    @Override
    public boolean test(@Nullable Relation input) {
        throw new UnsupportedOperationException("not used");
    }




    public boolean hasSearches() {
        for (RelationSearch relationSearch : asList()) {
            if (relationSearch.hasSearches()) {
                return true;
            }
        }
        return false;
    }



    @Override
    public String toString() {
        return String.valueOf(relationSearches);
    }
}
