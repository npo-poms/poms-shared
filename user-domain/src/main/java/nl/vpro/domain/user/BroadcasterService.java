/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.Optional;

import nl.vpro.domain.NotFoundException;

public interface BroadcasterService extends OrganizationService<Broadcaster> {

    default Broadcaster findForMisId(String id) throws NotFoundException {
        for (Broadcaster b : findAll()) {
            if (b.misId == null) {
                throw new UnsupportedOperationException();
            }
            if (id.equalsIgnoreCase(b.misId)) {
                return b;
            }
        }
        throw new NotFoundException(id, "No broadcaster found for mis id");
    }

    default Broadcaster findForWhatsOnId(String id) throws NotFoundException {
        for (Broadcaster b : findAll()) {
            if (b.whatsOnId == null) {
                throw new UnsupportedOperationException();
            }
            if (id.equalsIgnoreCase(b.whatsOnId)) {
                return b;
            }
        }
        throw new NotFoundException(id, "No broadcaster found for whatson broadcaster id");
    }

    default Broadcaster findForNeboId(String id) throws NotFoundException {
        for (Broadcaster b : findAll()) {
            if (b.neboId == null) {
                throw new UnsupportedOperationException();
            }
            if (id.equalsIgnoreCase(b.neboId)) {
                return b;
            }
        }
        throw new NotFoundException(id, "No broadcaster found for nebo broadcaster id");

    }

    /**
     * @since 5.13
     */
    default Optional<Broadcaster> findForIds(String value) {
        try {
            return Optional.of(findForWhatsOnId(value));
        } catch (NotFoundException e) {
            try {
                return Optional.of(findForMisId(value));
            } catch (NotFoundException e2) {
                try {
                    return Optional.of(findForNeboId(value));
                } catch (Exception e1) {
                    return Optional.empty();
                }
            }
        }
    }

}
