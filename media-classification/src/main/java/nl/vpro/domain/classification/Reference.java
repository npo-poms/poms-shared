/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.classification;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Reference {

    @XmlValue
    private String value;

    public String getValue() {
        return value;
    }
}
