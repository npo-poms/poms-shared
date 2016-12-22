/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image;

import nl.vpro.domain.image.support.DomainObject;
import nl.vpro.domain.image.support.Modifiable;

public interface Resource<T extends Resource> extends Modifiable<T>, DomainObject<T> {
    String getTitle();

    T setTitle(String title);

}
