package nl.vpro.domain.media.gtaa;

import java.util.List;

import com.google.common.annotations.Beta;

/**
 * An object that is 'GTAA managed' represents a link to  meta data that is synced wit the thesaurus of Beeld en Geluid.
 * <p>
 * The link to this system is in {@link #getGtaaUri()}
 *
 * This object is fully modifiable though that does not make sense in all situations, because the data cannot be actually modified since it is maintained by the GTAA.
 * <p>
 * But for marshalling and testing purposes it can be useful. But this interface may still be split up.
 * <p>
 *
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public interface GTAAManaged {

    String getName();

    @Beta
    void setName(String name);

    List<String> getScopeNotes();

    @Beta
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

    @Beta
    void setGtaaStatus(GTAAStatus status);

}
