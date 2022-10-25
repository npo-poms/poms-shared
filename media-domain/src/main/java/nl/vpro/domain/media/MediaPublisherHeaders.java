package nl.vpro.domain.media;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

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
    String REASONS_SPLITTER         = "\t";
    String REASON_SPLITTER          = "|";
    String REASON_SPLITTER_QUOTED   = Pattern.quote(REASON_SPLITTER);
    String TRIGGERED_BY_HEADER         = "triggeredBy";
    String RECENTLY_MODIFIED_BY_HEADER = "recentlyModifiedBy";
    String LAST_MODIFIED_BY_HEADER     = "lastModifiedBy";
    String MID = "mid";
    String PREVIOUS_PUBLISHDATE_HEADER = "previousPublishDate";


    @Slf4j
    enum Destination implements Displayable {
        ElasticSearch("NPO Frontend API"),
        ElasticSearch_previous("NPO Frontend API (a previous ES-deployment)"),
        PROJECTM("Project M")
        ;


        @Getter
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

        public static Destination[] arrayOf(String destination) {
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
