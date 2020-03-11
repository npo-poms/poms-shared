package nl.vpro.domain.media.gtaa;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public interface GTAAManaged {

    /**
     * The URI in GTAA of this thesaurus item
     */
    String getGtaaUri();

    void setGtaaUri(String uri);

    /**
     * The status in GTAA of this thesaurus item.
     */
    GTAAStatus getGtaaStatus();

    void setGtaaStatus(GTAAStatus status);

}
