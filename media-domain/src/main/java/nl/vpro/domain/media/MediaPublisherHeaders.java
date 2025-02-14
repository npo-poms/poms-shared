package nl.vpro.domain.media;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.PolyNull;

import nl.vpro.i18n.Displayable;


/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface MediaPublisherHeaders {

    String PUBLISH_TO_HEADER        = "publishTo";
    String QUEUETIME_HEADER         = "queueTime";
    String TRANSACTION_UUID_HEADER  = "transactionUUID";
    String REASON_HEADER            = "reason";
    String COLLECTED_REASONS_HEADER = "collectedReasons";


    String TRIGGERED_BY_HEADER         = "triggeredBy";
    String RECENTLY_MODIFIED_BY_HEADER = "recentlyModifiedBy";
    String LAST_MODIFIED_BY_HEADER     = "lastModifiedBy";
    String MID = "mid";
    String PREVIOUS_PUBLISHDATE_HEADER = "previousPublishDate";


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

        Destination(String displayName) {
            this.displayName = displayName;
        }

        public static String[] toStringArray(Destination... destinations) {
            if(destinations == null) {
                return null;
            }

            String[] result = new String[destinations.length];
            for(int i = 0; i < destinations.length; i++) {
                result[i] = destinations[i].name();
            }
            return result;
        }

        public static String[] toStringArray(Collection<Destination> destinations) {
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

        public static Destination[] arrayOfOrNull(String destination) {
            if (StringUtils.isBlank(destination)) {
                return null;
            }
            return arrayOf(destination);
        }

    }

}
