/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

public interface BroadcasterService extends OrganizationService<Broadcaster> {

    Broadcaster findForMisId(String id);

    Broadcaster findForWhatsOnId(String id);

    Broadcaster findForNeboId(String s);


}
