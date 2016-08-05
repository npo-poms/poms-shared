package nl.vpro.domain.subtitles;

import lombok.ToString;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DurationXmlAdapter;

/**
* @author Michiel Meeuwissen
* @since 4.8
*/
@XmlRootElement(name = "cue")
@ToString(of = {"parent", "sequence"})
public class Cue {

    static final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("UT"));
    }

    @XmlAttribute
    String parent;
    @XmlAttribute
    int sequence;

    @XmlAttribute
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    Duration start;

    @XmlAttribute
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    Duration end;
    @XmlValue
    @JsonProperty("content")
    String content;


    Cue(String parent,
        int sequence,
        Duration start,
        Duration end,
        String content) {
        this.parent = parent;
        this.sequence = sequence;
        this.start = start;
        this.end = end;
        this.content = content;
    }

    Cue(Cue cue) {
        this.parent = cue.parent;
        this.sequence = cue.sequence;
        this.start = cue.start;
        this.end = cue.end;
        this.content = cue.content;
    }

    protected Cue() {

    }


    public static Cue parse(String parent, String timeLine, String content) throws ParseException {
        String[] split = timeLine.split("\\s+");
        return new Cue(
            parent,
            Integer.parseInt(split[0]),
            Duration.ofMillis(dateFormat.parse(split[1] + "0").getTime()),
            Duration.ofMillis(dateFormat.parse(split[2] + "0").getTime()),
            content
        );

    }

    public String getParent() {
        return parent;
    }

    public int getSequence() {
        return sequence;
    }

    public Duration getStart() {
        return start;
    }

    public Duration getEnd() {
        return end;
    }

    public String getContent() {
        return content;
    }
}
