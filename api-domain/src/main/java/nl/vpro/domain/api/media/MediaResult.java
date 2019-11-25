package nl.vpro.domain.api.media;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.media.MediaObject;

/**
 * Exists only because of https://jira.vpro.nl/browse/API-118
 *
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlRootElement(name = "mediaResult")
@XmlType(name = "mediaResultType")
public class MediaResult extends Result<MediaObject> {

    public MediaResult() {
    }

    public static MediaResult emptyResult(Long offset, Integer max) {
        return new MediaResult(Collections.emptyList(), offset, max, 0L, TotalQualifier.EQUAL_TO);
    }

    public MediaResult(List<? extends MediaObject> list, Long offset, Integer max, Long total, TotalQualifier totalQualifier) {
        super(list, offset, max, total, totalQualifier);
    }

    public MediaResult(Result<? extends MediaObject> mediaObjects) {
        super(mediaObjects);
    }
}
