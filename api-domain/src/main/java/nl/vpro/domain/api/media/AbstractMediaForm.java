/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.media;

import lombok.*;

import java.util.function.Predicate;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.checkerframework.checker.nullness.qual.Nullable;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.api.Form;
import nl.vpro.domain.api.FormUtils;
import nl.vpro.domain.media.MediaObject;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */

@XmlTransient
@EqualsAndHashCode
@ToString
public abstract class AbstractMediaForm implements Form, Predicate<MediaObject> {

    @JsonProperty("highlight")
    @XmlAttribute(name = "highlight")
    private Boolean highlighted = null;

    @XmlElement
    @Valid
    @Getter
    @Setter
    private MediaSearch searches;

    @Override
    public String getText() {
        return FormUtils.getText(searches);
    }

    @Override
    public boolean isHighlight() {
        return highlighted != null ? highlighted : false;
    }

    public void setHighlight(boolean highlight) {
        this.highlighted = highlight;
    }

    @Override
    public boolean test(@Nullable MediaObject input) {
        return getTestResult(input).test().getAsBoolean();
    }
    public MediaSearch.TestResult getTestResult(MediaObject input) {
        return searches == null ? MediaSearch.TestResultIgnore.INSTANCE : searches.getTestResult(input);
    }
}
