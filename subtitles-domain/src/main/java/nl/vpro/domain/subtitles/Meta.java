package nl.vpro.domain.subtitles;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * Sometimes a subtitles may contain some information about the whole thing, not associated to one specific cue.
 * <p>
 * These can be stored during parsing in these {@link Meta} objects.
 * <p>
 * The could be published in elasticsearch as kind of cue;
 *
 * @author Michiel Meeuwissen
 * @since 5.12
 */
@XmlRootElement(name = "header")
@ToString(of = {"parent", "sequence", "content", "type"})
@EqualsAndHashCode
@Getter
public class Meta {

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

    @XmlAttribute
    MetaType type;

    @XmlValue
    @JsonProperty("content")
    String content;



    @lombok.Builder(builderClassName = "Builder")
    Meta(
        String parent,
        Integer sequence,
        String content,
        MetaType type
        ) {
        this.parent = parent;
        this.sequence = sequence;
        this.content = content;
        this.type = type;
    }


    protected Meta() {

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
