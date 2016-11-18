/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.support;

import java.util.Date;

public interface Modifiable<T extends Modifiable> {
    Date getCreationDate();

    T setCreationDate(Date creationDate);

    Date getLastModified();

    T setLastModified(Date lastModified);
}
