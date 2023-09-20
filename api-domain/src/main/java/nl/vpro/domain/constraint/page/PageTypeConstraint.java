/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.page;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.EnumConstraint;
import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.PageType;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageTypeConstraintType")
public class PageTypeConstraint extends EnumConstraint<PageType, Page> {

    public PageTypeConstraint() {
        super(PageType.class);
    }

    public PageTypeConstraint(PageType value) {
        super(PageType.class, value);
    }

    @Override
    public String getESPath() {
        return "type";
    }

    @Override
    protected Collection<PageType> getEnumValues(Page input) {
        return asCollection(input.getType());

    }

}
