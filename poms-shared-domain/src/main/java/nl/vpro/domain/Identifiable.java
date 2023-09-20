/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain;

import java.io.Serializable;

public interface Identifiable<I extends Serializable & Comparable<I>> {

    I getId();

}
