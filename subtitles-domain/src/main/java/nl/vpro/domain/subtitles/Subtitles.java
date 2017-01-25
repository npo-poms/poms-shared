package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.Locale;
import java.util.NoSuchElementException;

import javax.persistence.*;
import javax.xml.XMLConstants;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import nl.vpro.domain.Identifiable;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.persistence.DurationToLongConverter;
import nl.vpro.persistence.InstantToTimestampConverter;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;
import nl.vpro.xml.bind.LocaleAdapter;

import static nl.vpro.i18n.Locales.DUTCH;

/**
 * Closed captions (subtitles for hearing impaired). We could also store translation subtitles in this.
 *
 * @author Michiel Meeuwissen
 */
@Entity
@XmlRootElement(name = "subtitles")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subtitlesType", propOrder = {
    "mid",
    "offset",
    "content"
})
@Slf4j
@IdClass(SubtitlesId.class)
public class Subtitles implements Serializable, Identifiable<SubtitlesId> {

    private static final long serialVersionUID = 0L;

    @Column(nullable = false)
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @Convert(converter = InstantToTimestampConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected Instant creationDate = Instant.now();


    @Column(nullable = false)
    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @Convert(converter = InstantToTimestampConverter.class)
    @XmlSchemaType(name = "dateTime")
    protected Instant lastModified = Instant.now();

    @Id
    @XmlAttribute(required = true)
    protected String mid;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @Id
    private SubtitlesType type = SubtitlesType.CAPTION;

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @Id
    private Locale language;

    @Column(name = "[offset]")
    @Convert(converter = DurationToLongConverter.class)
    @XmlAttribute
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Duration offset;

    @Embedded
    @XmlElement(required = true)
    private SubtitlesContent content;


    public static Subtitles ebu(String mid, Duration offset, Locale language, String content) {
        return new Subtitles(mid, offset, language, SubtitlesFormat.EBU, content);
    }

    public static Subtitles ebuCaption(String mid, Duration offset, String content) {
        Subtitles subtitles = new Subtitles(mid, offset, DUTCH, SubtitlesFormat.EBU, content);
        subtitles.setType(SubtitlesType.CAPTION);
        return subtitles;
    }

    public static Subtitles webvtt(String mid, Duration offset, Locale language, String content) {
        return new Subtitles(mid, offset, language, SubtitlesFormat.WEBVTT, content);
    }

    public static Subtitles from(Iterator<StandaloneCue> cueIterator) {
        PeekingIterator<StandaloneCue> peeking = Iterators.peekingIterator(cueIterator);
        Subtitles subtitles = new Subtitles();
        subtitles.setCreationDate(null);
        subtitles.setLastModified(null);

        StringWriter writer = new StringWriter();
        try {
            StandaloneCue first = peeking.peek();
            WEBVTTandSRT.formatWEBVTT(peeking, writer);
            subtitles.setMid(first.getParent());
            subtitles.setLanguage(first.getLocale());
            subtitles.setOffset(first.getOffset());
            subtitles.setContent(new SubtitlesContent(SubtitlesFormat.WEBVTT, writer.toString()));
        } catch(NoSuchElementException nse) {
            log.error(nse.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return subtitles;

    }

    public static Subtitles from(String mid, Duration offset, Locale language, Iterator<Cue> cues)  {
        StringWriter writer = new StringWriter();
        try {
            WEBVTTandSRT.formatWEBVTT(cues, writer);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return new Subtitles(mid, offset, language, SubtitlesFormat.WEBVTT, writer.toString());
    }


    public Subtitles() {}

    public Subtitles(String mid, Duration offset, Locale language, SubtitlesFormat format, String content) {
        this.mid = mid;
        this.offset = offset;
        this.content = new SubtitlesContent(format, content);
        this.language = language;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }


    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public Duration getOffset() {
        return offset;
    }

    public Subtitles setOffset(Duration offset) {
        this.offset = offset;
        return this;
    }

    public SubtitlesContent getContent() {
        return content;
    }

    public void setContent(SubtitlesContent content) {
        this.content = content;
    }

    @Override
    public SubtitlesId getId() {
        return new SubtitlesId(mid, language, type);
    }

    public SubtitlesType getType() {
        return type;
    }

    public void setType(SubtitlesType type) {
        this.type = type;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Subtitles");
        sb.append("{mid='").append(mid).append('\'');
        sb.append(", creationDate=").append(creationDate);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Subtitles subtitles = (Subtitles)o;

        if(mid != null ? !mid.equals(subtitles.mid) : subtitles.mid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return mid != null ? mid.hashCode() : 0;
    }
}
