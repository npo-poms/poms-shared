/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import java.time.Instant;
import java.util.concurrent.Future;
import java.util.function.Function;

import nl.vpro.domain.Changes;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public interface PageUpdateRepository extends SectionRepository {

    PageUpdate loadByUrl(String url);

    PageUpdate loadByCrid(String crid);

    Future<?> save(PageUpdate update);

    Future<?> delete(String pageRef);

    Future<?> deleteAll(String url, int max, Function<PageUpdate, Boolean> permissionEvaluator);

    Changes<PageUpdate> getChanges(Instant since);

    class ToManyUpdatesException extends RuntimeException {

        public ToManyUpdatesException(String message) {
            super(message);
        }
    }

}
