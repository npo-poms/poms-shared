package nl.vpro.domain.media;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import nl.vpro.domain.PublicationReason;
import nl.vpro.i18n.Displayable;


/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface MediaPublisherHeaders {

    /**
     * Indicates to which destinations the publisher application is requested to publish to. This used to be more relevant
     * @see Destination
     */
    String PUBLISH_TO_HEADER        = "publishTo";

    /**
     * At what moment this message was queued. This is used to calculate the time it took to publish.
     */
    String QUEUETIME_HEADER         = "queueTime";

    /**
     *
     */
    String TRANSACTION_UUID_HEADER  = "transactionUUID";

    /**
     * The reason why this message was published {@see PublicationReason#toString()}, a {@link String} and a timestamp separated by {@link PublicationReason#FIELD_SPLITTER}
     */
    String REASON_HEADER            = "reason";
    /**
     * A list of recent reason. Encoded in a {@link String}, separated by {@link nl.vpro.domain.PublicationReason#RECORD_SPLITTER}
     * @see #REASON_HEADER
     */
    String COLLECTED_REASONS_HEADER = "collectedReasons";

    /**
     * The user who triggered the publication
     */
    String TRIGGERED_BY_HEADER         = "triggeredBy";

    /**
     * The users who recently modified the object. A comma separated list of usernames.
     */
    String RECENTLY_MODIFIED_BY_HEADER = "recentlyModifiedBy";

     /**
     * The users who last modified the object.
     */
    String LAST_MODIFIED_BY_HEADER     = "lastModifiedBy";

    /**
     * The mid of the object
     */
    String MID = "mid";

    /**
     * The previous publishdate of the object
     */
    String PREVIOUS_PUBLISHDATE_HEADER = "previousPublishDate";

    /**
     * A header to indicate that the object is to be considered deleted.
     * @since 8.5
     */
    String DELETED = "deleted";


    /**
     * POMS used to support several 'destinations' when publishing. Corresponding to virtual topic in JMS.
     * There were 'Couchdb', 2 versions of elasticsearch (during transition), 'projectm', and 1 or two others.
     * <p>
     * At last, there was only one left, namely {@link #ElasticSearch}, but now there is also {@link #Kafka}.
     *
     */
    @Slf4j
    @Getter
    enum Destination implements Displayable {
        ElasticSearch("NPO Frontend API"),

        /**
         * All data is published to kafka topics too, which can be picked up by Metadata Services
         * @since 8.5
         */
        Kafka("Kafka Topics")
        ;

        private final String displayName;

        Destination(@NonNull String displayName) {
            this.displayName = displayName;
        }

        @PolyNull
        public static String[] toStringArray(@NonNull Destination @PolyNull... destinations) {
            if(destinations == null) {
                return null;
            }

            String[] result = new String[destinations.length];
            for(int i = 0; i < destinations.length; i++) {
                result[i] = destinations[i].name();
            }
            return result;
        }

        @PolyNull
        public static String[] toStringArray(@PolyNull Collection<@NonNull Destination> destinations) {
            if (destinations == null) {
                return null;
            }
            return toStringArray(destinations.toArray(new Destination[0]));
        }

        @PolyNull
        public static Destination[] arrayOf(@PolyNull String destination) {
            if (destination == null) {
                return null;
            }
            String[] values =
                StringUtils.isEmpty(destination) ?
                    new String[0] :
                    destination.split("\\W+");

            Destination[] result = new Destination[values.length];
            for(int i = 0; i < values.length; i++) {
                try {
                    result[i] = valueOf(values[i]);
                } catch(IllegalArgumentException iae) {
                    log.error(iae.getMessage());
                }
            }
            return result;
        }

        @PolyNull
        public static Destination[] arrayOfOrNull(@PolyNull String destination) {
            if (StringUtils.isBlank(destination)) {
                return null;
            }
            return arrayOf(destination);
        }

    }

}
