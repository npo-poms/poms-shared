package nl.vpro.domain.subtitles;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

import javax.persistence.*;
import javax.xml.XMLConstants;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Identifiable;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.persistence.DurationToLongConverter;
import nl.vpro.persistence.InstantToTimestampConverter;
import nl.vpro.xml.bind.DurationXmlAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;
import nl.vpro.xml.bind.LocaleAdapter;

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
public class Subtitles implements Serializable, Identifiable<String> {

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

    @Column(name = "[offset]")
    @Convert(converter = DurationToLongConverter.class)
    @XmlAttribute
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Duration offset;

    @Column(nullable = false)
    @Lob
    @XmlElement(required = true)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    private SubtitlesType type = SubtitlesType.CAPTION;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    private SubtitlesFormat format = SubtitlesFormat.WEBVTT;

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    private Locale language;

    public Subtitles() {}

    public Subtitles(String mid, Duration offset, SubtitlesFormat format, String content) {
        this.mid = mid;
        this.offset = offset;
        this.content = content;
        this.format = format;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String getId() {
        return mid;
    }

    public SubtitlesType getType() {
        return type;
    }

    public void setType(SubtitlesType type) {
        this.type = type;
    }

    public SubtitlesFormat getFormat() {
        return format;
    }

    public void setFormat(SubtitlesFormat format) {
        this.format = format;
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
        sb.append("{poProgID='").append(mid).append('\'');
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
