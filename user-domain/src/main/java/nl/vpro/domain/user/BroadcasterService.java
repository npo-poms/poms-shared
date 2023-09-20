/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.time.Instant;
import java.util.Comparator;
import java.util.Optional;

import nl.vpro.domain.NotFoundException;

public interface BroadcasterService extends OrganizationService<Broadcaster> {

    default Instant getLastModification() {
        return findAll().stream()
            .map(Broadcaster::getLastModified)
            .max(Comparator.naturalOrder())
            .orElse(Instant.now());
    }

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
        Broadcaster b = find(value);
        if (b != null) {
            return Optional.of(b);
        }
        try {
            return Optional.of(findForWhatsOnId(value));
        } catch (NotFoundException | UnsupportedOperationException e) {
            try {
                return Optional.of(findForMisId(value));
            } catch (NotFoundException  | UnsupportedOperationException e2) {
                try {
                    return Optional.of(findForNeboId(value));
                } catch (Exception e1) {
                    return Optional.empty();
                }
            }
        }
    }

}
