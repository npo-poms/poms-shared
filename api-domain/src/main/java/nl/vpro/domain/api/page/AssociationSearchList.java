package nl.vpro.domain.api.page;

import java.util.*;
import java.util.function.Predicate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.page.Association;
import nl.vpro.jackson2.IterableWrapperJson;
import nl.vpro.util.IterableWrapper;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageAssociationSearchListType")
@JsonSerialize(using = IterableWrapperJson.Serializer.class)
@JsonDeserialize(using = AssociationSearchList.Deserializer.class)
public class AssociationSearchList implements Predicate<@Valid Association>, IterableWrapper<AssociationSearch> {


    @XmlElement(name = "search")
    protected List<@Valid @NotNull AssociationSearch> associationSearches = new ArrayList<>();

    public AssociationSearchList() {
    }


    public AssociationSearchList(AssociationSearch... relationSearch) {
        this(Arrays.asList(relationSearch));
    }

    public AssociationSearchList(List<AssociationSearch> relationSearches) {
        this.associationSearches.addAll(relationSearches);
    }


    @Override
    public List<AssociationSearch> asList() {
        return associationSearches;
    }


    @Override
    public boolean test(Association association) {
        for (AssociationSearch search : asList()) {
            if (search.test(association)) {
                return true;
            }
        }
        return false;
    }

    public static class Deserializer extends IterableWrapperJson.Deserializer<AssociationSearchList, AssociationSearch> {

        public Deserializer() {
            super(AssociationSearchList::new, AssociationSearch.class);
        }
    }

}
