package nl.vpro.domain.api.page;

import java.util.*;
import java.util.function.Predicate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.page.Relation;
import nl.vpro.jackson2.IterableWrapperJson;
import nl.vpro.util.IterableWrapper;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageRelationSearchListType")
@JsonSerialize(using = IterableWrapperJson.Serializer.class)
@JsonDeserialize(using = RelationSearchList.Deserializer.class)
public class RelationSearchList implements Predicate<Relation>, IterableWrapper<RelationSearch> {

    @XmlElement(name = "relationSearch")
    protected List<@Valid @NotNull RelationSearch> relationSearches = new ArrayList<>();

    public RelationSearchList() {
        // jaxb needs empty constructor
    }


    public RelationSearchList(RelationSearch... relationSearches) {
        this.relationSearches.addAll(Arrays.asList(relationSearches));
    }

    public RelationSearchList(List<RelationSearch> relationSearches) {
        this.relationSearches.addAll(relationSearches);
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


    public static class Deserializer extends IterableWrapperJson.Deserializer<RelationSearchList, RelationSearch> {

        public Deserializer() {
            super(RelationSearchList::new, RelationSearch.class);
        }
    }

    @Override
    public String toString() {
        return "RelationSearchList{" +
            "relationSearches=" + relationSearches +
            '}';
    }
}
