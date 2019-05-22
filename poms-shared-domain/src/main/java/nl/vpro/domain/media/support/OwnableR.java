/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

/**
 * Ownable objects are owned by a certain {@link OwnerType}. This indicates something about the authority of the value, and normally the same field value exist for different 'owner types'. E.g. a main title may be owned by 'MIS', but may have a another value but than owned by 'BROADCASTER'. Both values remain available, but in principle the BROADCASTER value overwrites the MIS value (but one may interpret differently).
 *
 * @see OwnerType
 *
 * @since 5.11
 * @javadoc
 * TODO I think this wants to be 'ReadonlyOwnable'? Should {@link Ownable} not extend this?
 */

public interface OwnableR {

    OwnerType getOwner();

}
