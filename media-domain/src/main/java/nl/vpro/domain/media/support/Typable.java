/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import javax.validation.constraints.NotNull;

/**
 * @see TextualType
 * @param <S>
 */
public interface Typable<S> {

    S getType();

    void setType(@NotNull(message = "{nl.vpro.constraints.NotNull}") S type);

}
