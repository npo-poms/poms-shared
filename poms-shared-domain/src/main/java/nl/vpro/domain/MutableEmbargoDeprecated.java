package nl.vpro.domain;

import java.util.Date;

import static nl.vpro.util.DateUtils.toDate;
import static nl.vpro.util.DateUtils.toInstant;

/**
 * Some deprecated methods related to {@link Embargo}s
 *
 * @author Michiel Meeuwissen
 * @since 5.2
 */
public interface MutableEmbargoDeprecated<T extends MutableEmbargoDeprecated<T>> extends MutableEmbargo<T> {

    @Deprecated
    default Date getPublishStart() {
        return toDate(getPublishStartInstant());
    }

    @Deprecated
    default void setPublishStart(Date publishStart) {
        setPublishStartInstant(toInstant(publishStart));
    }
    @Deprecated
    default Date getPublishStop() {
        return toDate(getPublishStopInstant());
    }

    @Deprecated
    default void setPublishStop(Date publishStop) {
        setPublishStopInstant(toInstant(publishStop));
    }


}
