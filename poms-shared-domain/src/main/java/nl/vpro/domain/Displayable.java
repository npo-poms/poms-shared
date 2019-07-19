/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain;

/**
 * A displayable has a {@link #getDisplayName()} method, to display the object as a single string (in dutch) to the user.
 * @author Roelof Jan Koekoek
 * @since 1.4
 */
public interface Displayable {

    String getDisplayName();

}
