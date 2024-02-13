package nl.vpro.domain.api;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.DisplayablePredicate;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlRootElement(name = "multipleMediaResult")
@XmlType(name = "multipleMediaResultType")
@XmlSeeAlso(MultipleMediaEntry.class)
public class MultipleMediaResult extends AbstractMultipleResult<MediaObject> {
    public MultipleMediaResult() {
        super(MultipleMediaEntry::new);
    }

    public MultipleMediaResult(List<String> ids, List<MediaObject> mediaObjects, DisplayablePredicate<MediaObject> predicate) {
        super(MultipleMediaEntry::new, ids, mediaObjects, predicate);
    }
}
