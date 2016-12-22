/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

public interface Ownable {

    OwnerType getOwner();

    void setOwner(OwnerType owner);

}
