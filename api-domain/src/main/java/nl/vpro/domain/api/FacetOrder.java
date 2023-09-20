/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.io.IOException;
import java.util.Comparator;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.ObjectUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlEnum
@XmlType(name = "facetOrderTypeEnum")
//@JsonDeserialize(using = FacetDeserializer.class)
@XmlJavaTypeAdapter(FacetAdapter.class)
public enum FacetOrder {
    VALUE_ASC,
    VALUE_DESC,
    COUNT_ASC,
    COUNT_DESC;

    @XmlType(name = "facetOrderTypeEnum")
    protected enum FacetOrderBackwards {
        VALUE_ASC,
        VALUE_DESC,
        COUNT_ASC,
        COUNT_DESC,

        TERM,
        REVERSE_TERM,
        COUNT,
        REVERSE_COUNT
    }

    private static final Comparator<TermFacetResultItem> VALUE_ASC_COMPARATOR = (o1, o2) -> ObjectUtils.compare(o1.getValue(), o2.getValue());

    private static final Comparator<TermFacetResultItem> VALUE_DESC_COMPARATOR = (o1, o2) -> VALUE_ASC_COMPARATOR.compare(o2, o1);

    private static final Comparator<TermFacetResultItem> COUNT_ASC_COMPARATOR = (o1, o2) -> {
        long diff = o1.getCount() - o2.getCount();
        if (diff < 0) {
            return -1;
        } else if (diff == 0) {
            return VALUE_ASC_COMPARATOR.compare(o1, o2);
        } else {
            return 1;
        }
    };

    private static final Comparator<TermFacetResultItem> COUNT_DESC_COMPARATOR = (o1, o2) -> COUNT_ASC_COMPARATOR.compare(o2, o1);

    public static Comparator<TermFacetResultItem> toComparator(final FacetOrder order) {
        return switch (order) {
            case VALUE_ASC -> VALUE_ASC_COMPARATOR;
            case VALUE_DESC -> VALUE_DESC_COMPARATOR;
            case COUNT_ASC -> COUNT_ASC_COMPARATOR;
            default -> COUNT_DESC_COMPARATOR;
        };
    }

    static FacetOrder backwardCompatibleValueOf(FacetOrderBackwards v) {
        if (v == null) {
            return null;
        }
        return switch (v) {
            case TERM -> FacetOrder.VALUE_ASC;
            case REVERSE_TERM -> FacetOrder.VALUE_DESC;
            case COUNT -> FacetOrder.COUNT_DESC;
            case REVERSE_COUNT -> FacetOrder.COUNT_ASC;
            default -> FacetOrder.valueOf(v.toString());
        };
    }


    public static FacetOrder backwardCompatibleValueOf(String v) {
        if (v == null) {
            return null;
        }
        return backwardCompatibleValueOf(FacetOrder.FacetOrderBackwards.valueOf(v));
    }

}

// not needed, jackson seems to use the XmlAdapter
class FacetDeserializer extends JsonDeserializer<FacetOrder> {

    @Override
    public FacetOrder deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        return FacetOrder.backwardCompatibleValueOf(FacetOrder.FacetOrderBackwards.valueOf(jp.getText()));
    }
}
class FacetAdapter extends XmlAdapter<FacetOrder.FacetOrderBackwards, FacetOrder>{

    @Override
    public FacetOrder unmarshal(FacetOrder.FacetOrderBackwards v) {
        return FacetOrder.backwardCompatibleValueOf(v);
    }

    @Override
    public FacetOrder.FacetOrderBackwards marshal(FacetOrder v) {
        if (v == null) {
            return null;
        }
        return FacetOrder.FacetOrderBackwards.valueOf(v.toString());

    }
}

