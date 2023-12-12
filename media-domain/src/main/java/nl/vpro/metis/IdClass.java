package nl.vpro.metis;

/**
 * The class of the 'metis' ID ('mid')
 * <p>
 * Classically, this was either a {@link #PRID} or a {@link #SRID}, but now also {@link #IID} is possible (currently needed by PREPR)
 * @since 7.10
 */
public enum IdClass {

    /**
     * An ID form program object
     */
    PRID,
    /**
     * An ID for group (series) objects
     */
    SRID,
    /**
     * An ID for images.
     */
    IID
}
