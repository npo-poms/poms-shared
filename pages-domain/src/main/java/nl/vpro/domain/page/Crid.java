/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.page.bind.CridToString;
import nl.vpro.validation.CRID;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 * @deprecated There is no real point. Just use @{@link CRID} {@link String}
 */
@SuppressWarnings("DeprecatedIsStillUsed")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pridType")
@JsonSerialize(using = CridToString.Serializer.class)
@JsonDeserialize(using = CridToString.Deserializer.class)
@Deprecated
public class Crid {

    @XmlValue
    @CRID
    private String value;

    protected Crid() {
    }

    public Crid(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Crid crid = (Crid) o;

        return value.equals(crid.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
