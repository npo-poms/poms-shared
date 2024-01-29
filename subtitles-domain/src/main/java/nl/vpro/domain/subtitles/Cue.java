package nl.vpro.domain.subtitles;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Duration;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Range;

import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DurationXmlAdapter;

/**
 * Just represent one cue in a series of subtitles.
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@XmlRootElement(name = "cue")
@ToString(of = {"parent", "sequence", "identifier", "content"})
@EqualsAndHashCode
@Getter
public class Cue {

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

    /**
     * Some formats support an identifier per cue
     */
    @XmlAttribute
    String identifier;

    /**
     * When the cue must be started to be displayed relative to the beginning of the stream
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    Duration start;

    /**
     * When the cue must be stopped to be displayed relative to the beginning of the stream
     */
    @XmlAttribute
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    Duration end;

    @XmlValue
    @JsonProperty("content")
    String content;

    @XmlAttribute
    CueSettings settings;

    /**
     * Sometimes the cue may contain specific formatting options for a certain subtitles format.
     * We don't try to generalize this, but the knowledge may be usefull.
     */
    @XmlAttribute
    SubtitlesFormat contentFormat;

    @lombok.Builder(builderClassName = "Builder")
    Cue(
        String parent,
        Integer sequence,
        String identifier,
        Duration start,
        Duration end,
        String content,
        SubtitlesFormat contentFormat,
        CueSettings settings
        ) {
        this.parent = parent;
        this.sequence = sequence;
        this.identifier = identifier == null && sequence != null ? "" + sequence : identifier;
        this.start = start;
        this.end = end;
        this.content = content;
        this.contentFormat = contentFormat;
        this.settings = settings;
    }

    Cue(Cue cue) {
        this.parent = cue.parent;
        this.sequence = cue.sequence;
        this.identifier = cue.identifier;
        this.start = cue.start;
        this.end = cue.end;
        this.content = cue.content;
        this.settings = CueSettings.copy(cue.settings);
    }

    protected Cue() {

    }

    public Range<Duration> asRange() {
        return Range.closedOpen(getStart(), getEnd());
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
        public Builder webvttSettings(String settings) {
            return settings(CueSettings.webvtt(settings));
        }
    }

}
