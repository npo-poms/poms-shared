/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
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
