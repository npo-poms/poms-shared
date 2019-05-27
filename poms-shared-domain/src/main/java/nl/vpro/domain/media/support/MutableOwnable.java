/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import javax.annotation.Nonnull;

/**
 * @see OwnerType
 * @since 5.11
 */

public interface MutableOwnable extends Ownable {

    void setOwner(@Nonnull OwnerType owner);

}
