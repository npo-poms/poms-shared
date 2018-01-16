/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import java.time.Instant;

import nl.vpro.domain.Changes;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public interface PageUpdateService {

    Changes<PageUpdate> getChanges(Instant since);

    PageUpdate load(String pageRef);

    PageUpdate load(PageUpdate example);

    PageUpdate loadFirst(String pageRef);

    void save(PageUpdate oldPage, PageUpdate update);

    boolean delete(PageUpdate update);

    boolean delete(String url);

    boolean deleteAll(PageUpdate first, String url, int max);

}
