/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.io.Serializable;

import nl.vpro.domain.Identifiable;

public interface UpdatableIdentifiable<I extends Serializable & Comparable<I>, T> extends Identifiable<I> , Updatable<T> {


}
