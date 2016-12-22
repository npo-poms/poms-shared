/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import nl.vpro.domain.NotFoundException;

public interface BroadcasterService extends OrganizationService<Broadcaster> {

    default Broadcaster findForMisId(String id) throws NotFoundException {
        for (Broadcaster b : findAll()) {
            if (b.misId == null) {
                throw new UnsupportedOperationException();
            }
            if (id.equals(b.misId)) {
                return b;
            }
        }
        return null;
    }

    default Broadcaster findForWhatsOnId(String id) throws NotFoundException {
        for (Broadcaster b : findAll()) {
            if (b.whatsOnId == null) {
                throw new UnsupportedOperationException();
            }
            if (id.equals(b.whatsOnId)) {
                return b;
            }
        }
        return null;
    }

    default Broadcaster findForNeboId(String id) throws NotFoundException {
        for (Broadcaster b : findAll()) {
            if (b.neboId == null) {
                throw new UnsupportedOperationException();
            }
            if (id.equals(b.neboId)) {
                return b;
            }
        }
        return null;
    }

}
