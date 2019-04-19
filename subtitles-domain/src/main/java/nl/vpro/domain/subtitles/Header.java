package nl.vpro.domain.subtitles;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@XmlRootElement(name = "header")
@ToString(of = {"parent", "sequence", "content"})
@EqualsAndHashCode
@Getter
public class Header {

    /**
     * The MID of the parent media object
     */
    @XmlAttribute
    String parent;

    /**
     * A sequence number relative to other cues of the same parent.
     */
    @XmlAttribute
    Integer sequence;

    @XmlValue
    @JsonProperty("content")
    String content;

    /**
     * Sometimes the cue may contain specific formatting options for a certain subtitles format.
     * We don't try to generalize this, but the knowledge may be usefull.
     */
    @XmlAttribute
    SubtitlesFormat contentFormat;

    @lombok.Builder(builderClassName = "Builder")
    Header(
        String parent,
        Integer sequence,
        String content,
        SubtitlesFormat contentFormat
        ) {
        this.parent = parent;
        this.sequence = sequence;
        this.content = content;
        this.contentFormat = contentFormat;
    }


    protected Header() {

    }

    public static Builder forMid(String mid) {
        return builder()
            .parent(mid)
            .sequence(0);
    }

    public static class Builder {
        public Builder mid(String mid) {
            return parent(mid);
        }
    }

}
