/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @see OwnerType
 * @since 5.11
 */

public interface MutableOwnable extends Ownable {

    void setOwner(@NonNull OwnerType owner);

}
