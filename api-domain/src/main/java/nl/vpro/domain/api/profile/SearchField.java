/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.profile;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 * @deprecated  Ik geloof dat dit niet meer gebruikt wordt. Het was denk ik over van toen je dit soort dingen in het profiel zette.
 * @see nl.vpro.domain.api.SearchFieldDefinition
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "searchFieldType")
public class SearchField {

    @XmlAttribute
    private Float boost = 1f;

    @XmlValue
    @JsonProperty("name")
    private String name;

    private SearchField() {
    }

    public SearchField(String name) {
        this.name = name;
    }

    public SearchField(String name, Float boost) {
        this.name = name;
        this.boost = boost;
    }

    public Float getBoost() {
        return boost;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return (boost != 1f ? (boost + "*") : "") + name;
    }


}
