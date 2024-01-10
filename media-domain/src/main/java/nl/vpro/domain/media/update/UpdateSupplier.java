package nl.vpro.domain.media.update;


import lombok.Getter;

import com.google.common.annotations.Beta;

import nl.vpro.metis.IdType;

/**
 * An update supplier is an authoritative system - i.e. it is not a broadcaster - that supplies updates to POMS.
 * <p>
 * This is a limited number of systems, for each of which the POMS backend may have specific configuration on how to exactly handle incoming updates.
 *
 * @since 7.7
 */
public enum UpdateSupplier {

    /**
     * See MSE-5484
     */
    RCRS(IdType.RCRS),


    /**
     * I think we would like to do something similar for Sourcing Service
     */
    @Beta
    SOURCING_SERVICE(IdType.SRCS),


    /**
     * Only used for metis.
     */
    PREPR(IdType.PREPR),

    /**
     * @since 7.10 (using web services earlier)
     */
    PROMO(null);

    @Getter
    private final IdType idType;


    UpdateSupplier(IdType idType) {
        this.idType = idType;
    }
}
