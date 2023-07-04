package nl.vpro.domain.media.update;


import com.google.common.annotations.Beta;

/**
 * An update supplier is an authoritative system - i.e. it is not a broadcaster - that supplies updates to POMS.
 * <p>
 * This is a limited number of system, for each of which the POMS backend may have specific configuration on how to exactly handle incoming updates.
 *
 * @since 7.7
 */
public enum UpdateSupplier {

    /**
     * See MSE-5484
     */
    RCRS,


    /**
     * I think we would like to do something similar for Sourcing Service
     */
    @Beta
    SOURCING_SERVICE;


}
