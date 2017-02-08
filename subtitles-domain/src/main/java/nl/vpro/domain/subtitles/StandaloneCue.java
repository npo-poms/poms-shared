package nl.vpro.domain.subtitles;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */

import lombok.Builder;
import lombok.ToString;

import java.time.Duration;
import java.util.Locale;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.LocaleAdapter;

import static nl.vpro.i18n.Locales.DUTCH;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Duration offset = null;


    public static StandaloneCue translation(Cue cue, Locale locale) {
        return new StandaloneCue(cue, locale, SubtitlesType.TRANSLATION, Duration.ZERO);
    }


    public static StandaloneCue tt888(Cue cue) {
        return new StandaloneCue(cue, DUTCH, SubtitlesType.CAPTION, Duration.ZERO);
    }

    public static StandaloneCue of(Cue cue, Subtitles subtitles) {
        return new StandaloneCue(cue, subtitles.getLanguage(), subtitles.getType(), subtitles.getOffset());
    }

    protected StandaloneCue() {

    }

    public StandaloneCue(Cue cue, Locale locale, SubtitlesType type, Duration offset) {
        super(cue);
        this.locale = locale;
        this.type = type;
        this.offset = offset == null || offset.isZero() ? null : offset;
    }

    @Builder
    StandaloneCue(String parent,
                  int sequence,
                  Duration start,
                  Duration end,
                  String content,
                  Locale locale,
                  SubtitlesType type,
                  Duration offset) {
        super(Cue.builder().parent(parent).sequence(sequence).start(start).end(end).content(content).build());
        this.locale = locale;
        this.type = type;
        this.offset = offset == null || offset.isZero() ? null : offset;
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
