package nl.vpro.domain.media.gtaa;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public interface GTAARecordManaged extends GTAAManaged {


    GTAARecord getGtaaRecord();

    void setGtaaRecord(GTAARecord gtaaRecord);


    /**
     * The URI in GTAA of this thesaurus item
     */
    @Override
    default String getGtaaUri() {
        return getGtaaRecord().getUri();
    }

    @Override
    default void setGtaaUri(String uri) {
        getGtaaRecord().setUri(uri);
    }

    /**
     * The status in GTAA of this thesaurus item.
     */
    @Override
    default GTAAStatus getGtaaStatus() {
        return getGtaaRecord().getStatus();
    }
    @Override
    default void setGtaaStatus(GTAAStatus status) {
        getGtaaRecord().setStatus(status);
    }
}
