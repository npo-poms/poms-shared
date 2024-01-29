package nl.vpro.domain.api.media;

import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
        return new MediaResult(Collections.emptyList(), offset, max, Total.EMPTY);
    }

    public MediaResult(List<? extends MediaObject> list, Long offset, Integer max, Total total) {
        super(list, offset, max, total);
    }

    public MediaResult(Result<? extends MediaObject> mediaObjects) {
        super(mediaObjects);
    }
}
