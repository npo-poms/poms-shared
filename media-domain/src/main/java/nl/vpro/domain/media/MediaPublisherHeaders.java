package nl.vpro.domain.media;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface MediaPublisherHeaders {


    String PUBLISH_TO_HEADER = "publishTo";
    String QUEUETIME_HEADER = "queueTime";
    String TRANSACTION_UUID_HEADER = "transactionUUID";
    String REASON_HEADER = "reason";
    String TRIGGERED_BY_HEADER = "triggeredBy";
    String RECENTLY_MODIFIED_BY_HEADER = "recentlyModifiedBy";
    String LAST_MODIFIED_BY_HEADER = "lastModifiedBy";
    String MID = "mid";
    String PREVIOUS_PUBLISHDATE_HEADER = "previousPublishDate";
}
