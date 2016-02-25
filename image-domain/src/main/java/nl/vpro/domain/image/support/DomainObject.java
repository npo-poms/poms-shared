/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.support;

import java.io.Serializable;

public interface DomainObject<T extends DomainObject> extends Serializable {

    Long getId();

    T setId(Long id);
}
