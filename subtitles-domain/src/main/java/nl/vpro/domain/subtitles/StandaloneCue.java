package nl.vpro.domain.subtitles;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */

import lombok.ToString;

import java.time.Duration;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.LocaleAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 1.8
 */
@XmlRootElement(name = "standaloneCue")
@ToString(includeFieldNames = false, callSuper = true, of = {"locale"})
public class StandaloneCue extends Cue {

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    private Locale locale;

    @XmlAttribute
    private SubtitlesType type = SubtitlesType.CAPTION;

    @XmlAttribute
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    private Duration offset = Duration.ZERO;


    public static StandaloneCue translation(Cue cue, Locale locale) {
        return new StandaloneCue(cue, locale, SubtitlesType.TRANSLATION, Duration.ZERO);
    }

    protected StandaloneCue() {

    }

    public StandaloneCue(Cue cue, Locale locale, SubtitlesType type, Duration offset) {
        super(cue);
        this.locale = locale;
        this.type = type;
        this.offset = offset;
    }

    public Locale getLocale() {
        return locale;
    }

    public SubtitlesType getType() {
        return type;
    }

    public Duration getOffset() {
        return offset;
    }

    @XmlTransient
    public String getId() {
        return getParent() + "\t" + getType() + "\t" + getLocale() + "\t" + getSequence();
        //return getParent() + "_" + getType() + "_" + getLocale() + "_" + getSequence();
    }
}
