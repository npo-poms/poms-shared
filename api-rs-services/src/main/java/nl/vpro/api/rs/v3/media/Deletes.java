package nl.vpro.api.rs.v3.media;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public enum Deletes {

    /**
     * Return deleted objects
     */
    INCLUDE,
    /**
     * Do not return deleted object
     */
    EXCLUDE,
    /**
     * Make only the object in the change object empty.
     */
    ID_ONLY

}
