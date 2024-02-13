/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.*;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlTransient;

import nl.vpro.domain.api.media.MediaSearch;
import nl.vpro.domain.api.page.PageSearch;

/**
 * A Search interface but JAXB won't handle interfaces
 *
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({MediaSearch.class, PageSearch.class})
@XmlTransient
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractTextSearch<V> extends AbstractSearch<V> {

    @Valid
    protected SimpleTextMatcher text;

    public SimpleTextMatcher getText() {
        return text;
    }

    public void setText(SimpleTextMatcher text) {
        this.text = text;
    }

}
