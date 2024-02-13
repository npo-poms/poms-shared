package nl.vpro.domain.api.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.page.Relation;
import nl.vpro.jackson2.IterableJson;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageRelationSearchListType")
@JsonSerialize(using = IterableJson.Serializer.class)
@JsonDeserialize(using = RelationSearchList.Deserializer.class)
public class RelationSearchList implements Predicate<Relation>, Iterable<RelationSearch> {

    @XmlElement(name = "relationSearch")
    @Valid
    protected List<RelationSearch> relationSearches = new ArrayList<>();

    public RelationSearchList() {
        // jaxb needs empty constructor
    }


    public RelationSearchList(RelationSearch... relationSearches) {
        this.relationSearches.addAll(Arrays.asList(relationSearches));
    }

    public RelationSearchList(List<RelationSearch> relationSearches) {
        this.relationSearches.addAll(relationSearches);
    }


    public List<RelationSearch> asList() {
        return relationSearches;
    }

    @Override
    public boolean test(@Nullable Relation input) {
        throw new UnsupportedOperationException("not used");
    }


    public boolean hasSearches() {
        for (RelationSearch relationSearch : this) {
            if (relationSearch.hasSearches()) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public Iterator<RelationSearch> iterator() {
        return asList().iterator();
    }

    public int size() {
        return asList().size();
    }
    public static class Deserializer extends IterableJson.Deserializer<RelationSearch> {

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
