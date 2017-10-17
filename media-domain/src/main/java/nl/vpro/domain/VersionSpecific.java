package nl.vpro.domain;

/**
 * Classes which can behave differently according the specified version of the application can implement this.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface VersionSpecific {


    /**
     * For which version this object is supposed to be filled.
     *      */
    Float getVersion();

    void setVersion(Float version);
}
