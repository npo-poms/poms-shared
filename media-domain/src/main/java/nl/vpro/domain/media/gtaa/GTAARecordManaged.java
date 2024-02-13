package nl.vpro.domain.media.gtaa;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public interface GTAARecordManaged extends GTAAManaged {


    GTAARecord getGtaaRecord();

    void setGtaaRecord(GTAARecord gtaaRecord);

    @Override
    default String getName() {
        return getGtaaRecord().getName();
    }
    @Override
    default void setName(String name) {
        getGtaaRecord().setName(name);
    }
    @Override
    default List<String> getScopeNotes() {
        return getGtaaRecord().getScopeNotes();
    }

    @Override
    default void setScopeNotes(List<String> scopeNotes) {
        GTAARecord gtaaRecord = getGtaaRecord();
        if (scopeNotes != null) {
            gtaaRecord.setScopeNotes(scopeNotes);
        } else {
            gtaaRecord.setScopeNotes(new ArrayList<>());
        }
    }


    /**
     * The URI in GTAA of this thesaurus item
     */
    @Override
    @XmlAttribute
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
    @XmlAttribute
    default GTAAStatus getGtaaStatus() {
        return getGtaaRecord().getStatus();
    }
    @Override
    default void setGtaaStatus(GTAAStatus status) {
        getGtaaRecord().setStatus(status);
    }
}
