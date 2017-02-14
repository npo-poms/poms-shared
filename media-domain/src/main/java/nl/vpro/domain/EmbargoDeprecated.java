package nl.vpro.domain;

import java.util.Date;

import nl.vpro.util.DateUtils;

/**
 * An object describing a publication embargo, meaning that it has a publish start and stop instant.
 *
 * @author Michiel Meeuwissen
 * @since 5.2
 */
public interface EmbargoDeprecated extends Embargo {

    @Deprecated
    default Date getPublishStart() {
        return DateUtils.toDate(getEmbargoStart());
    }

    @Deprecated
    default void setPublishStart(Date publishStart) {
        setEmbargoStart(DateUtils.toInstant(publishStart));
    }
    @Deprecated
    default Date getPublishStop() {
        return DateUtils.toDate(getEmbargoStop());
    }

    @Deprecated
    default void setPublishStop(Date publishStop) {
        setEmbargoStop(DateUtils.toInstant(publishStop));
    }

}
