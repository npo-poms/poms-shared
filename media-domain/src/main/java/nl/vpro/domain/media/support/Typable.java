/*
 * Copyright (C) 2011 All rights reserved
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
