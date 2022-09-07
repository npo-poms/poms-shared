/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain;

import java.io.Serializable;

public interface Identifiable<I extends Serializable & Comparable<I>> {

    I getId();

}
