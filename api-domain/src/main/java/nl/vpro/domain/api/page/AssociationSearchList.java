package nl.vpro.domain.api.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.page.Association;
import nl.vpro.jackson2.IterableJson;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageAssociationSearchListType")
@JsonSerialize(using = IterableJson.Serializer.class)
@JsonDeserialize(using = AssociationSearchList.Deserializer.class)
public class AssociationSearchList implements Predicate<Association>, Iterable<AssociationSearch> {


    @Valid
    @XmlElement(name = "search")
    protected List<AssociationSearch> associationSearches = new ArrayList<>();

    public AssociationSearchList() {
    }


    public AssociationSearchList(AssociationSearch... relationSearch) {
        this(Arrays.asList(relationSearch));
    }

    public AssociationSearchList(List<AssociationSearch> relationSearches) {
        this.associationSearches.addAll(relationSearches);
    }


    public List<AssociationSearch> asList() {
        return associationSearches;
    }

    public int size() {
        return asList().size();
    }
    @NonNull
    @Override
    public Iterator<AssociationSearch> iterator() {
        return associationSearches.iterator();

    }

    @Override
    public boolean test(Association association) {
        for (AssociationSearch search : this) {
            if (search.test(association)) {
                return true;
            }
        }
        return false;
    }

    public static class Deserializer extends IterableJson.Deserializer<AssociationSearch> {

        public Deserializer() {
            super(AssociationSearchList::new, AssociationSearch.class);
        }
    }

}
