/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page.update;

import java.time.Instant;
import java.util.List;

import nl.vpro.domain.page.update.PageUpdateChange;
import nl.vpro.util.CloseableIterator;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public interface PageUpdateRepository {

    List<String> namesForSectionPath(String path, String portalId);

    CloseableIterator<PageUpdateChange> getChanges(Instant instant);


}
