/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

/**
 * @see TextualType
 * @param <S>
 */
public interface Typable<S> {

    S getType();

    void setType(S type);

}
