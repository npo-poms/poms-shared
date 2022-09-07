/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.io.Serializable;

import nl.vpro.domain.Identifiable;

public interface UpdatableIdentifiable<I extends Serializable & Comparable<I>, T> extends Identifiable<I> , Updatable<T> {


}
