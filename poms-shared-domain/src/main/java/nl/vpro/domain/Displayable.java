/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain;

/**
 * A displayable has a {@link #getDisplayName()} method, to display the object as a single string (in Dutch) to the user.
 * @author Roelof Jan Koekoek
 * @since 1.4
 * @deprecated Use nl.vpro.i18n.Displayable
 */
@Deprecated
public interface Displayable extends nl.vpro.i18n.Displayable {


}
