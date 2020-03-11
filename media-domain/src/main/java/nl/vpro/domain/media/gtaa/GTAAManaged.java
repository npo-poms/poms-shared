package nl.vpro.domain.media.gtaa;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public interface GTAAManaged {

    String getName();

    void setName(String name);

    List<String> getScopeNotes();

    void setScopeNotes(List<String> scopeNotes);

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
