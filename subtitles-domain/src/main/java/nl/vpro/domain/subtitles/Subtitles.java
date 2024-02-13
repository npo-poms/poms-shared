package nl.vpro.domain.subtitles;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import javax.xml.XMLConstants;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;

import nl.vpro.domain.Changeable;
import nl.vpro.domain.Identifiable;
import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.jackson2.XMLDurationToJsonTimestamp;
import nl.vpro.persistence.InstantToTimestampConverter;
import nl.vpro.xml.bind.*;

import static nl.vpro.i18n.Locales.DUTCH;

/**
 * Closed captions (subtitles for hearing impaired). We could also store translation subtitles in this.
 * <p>
 * The subtitles cues are represented as one String. For parsing this use {@link SubtitlesUtil#parse(nl.vpro.domain.subtitles.Subtitles, boolean)}
 *
 * @author Michiel Meeuwissen
 */
@Entity
@XmlRootElement(name = "subtitles")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "subtitlesType", propOrder = {
    "mid",
    "offset",
    "content"
})
@Slf4j
@IdClass(SubtitlesId.class)
public class Subtitles implements Serializable, Identifiable<SubtitlesId>, MutableOwnable, Changeable, SubtitlesMetadata {

    @Serial
    private static final long serialVersionUID = 0L;

    @JsonCreator
    static Subtitles jsonCreator() {
        return Subtitles.builder().lastModified(null).created(null).build();
    }

    @Column(nullable = false, name="creationDate")
    @XmlAttribute(name = "creationDate")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @Convert(converter = InstantToTimestampConverter.class)
    @XmlSchemaType(name = "dateTime")
    @Getter
    @Setter
    protected Instant creationInstant = Instant.now();


    @Column(nullable = false, name = "lastModified")
    @XmlAttribute(name = "lastModified")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @Convert(converter = InstantToTimestampConverter.class)
    @XmlSchemaType(name = "dateTime")
    @Getter
    @Setter
    protected Instant lastModifiedInstant = Instant.now();

    @Id
    @XmlAttribute(required = true)
    @Getter
    @Setter
    protected String mid;


    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @Id
    private SubtitlesType type = SubtitlesType.CAPTION;

    @Setter
    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @Id
    @Schema(implementation = String.class, type = "string")
    private Locale language;

    @Column(name = "`offset`")
    @XmlAttribute
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    @JsonSerialize(using = XMLDurationToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = XMLDurationToJsonTimestamp.DeserializerJavaDuration.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Duration offset;

    @Setter
    @Column(nullable = false)
    private Integer cueCount;

    @Embedded
    private SubtitlesContent content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @NonNull
    @Getter
    @Setter
    private OwnerType owner = OwnerType.BROADCASTER;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    @NonNull
    @Getter
    @Setter
    private SubtitlesWorkflow  workflow = SubtitlesWorkflow.FOR_PUBLICATION;


    @XmlTransient
    @Transient
    @Getter
    @Setter
    private boolean avoidParsing = false;

    public Subtitles() {
    }

    @lombok.Builder(builderClassName = "Builder")
    protected Subtitles(
            String mid,
            Duration offset,
            Locale language,
            SubtitlesFormat format,
            String content,
            InputStream value,
            Iterator<Cue> cues,
            SubtitlesType type,
            OwnerType owner,
            Instant created,
            Instant lastModified,
            SubtitlesWorkflow workflow) {
        this.mid = mid;
        this.offset = offset;
        if (content == null && value == null && cues == null) {
            this.content = null;
        } else if (content == null && value == null && format == null && cues != null) {
            StringWriter writer = new StringWriter();
            try {
                WEBVTTandSRT.formatWEBVTT(cues, writer);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            this.content = new SubtitlesContent(SubtitlesFormat.WEBVTT, writer.toString());
        } else if (content != null && cues == null && value == null) {
            this.content = new SubtitlesContent(format, content);
        } else if (value != null && cues == null && content == null) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try {
                int copy = IOUtils.copy(value, bytes);
                log.debug("Copied {} bytes", copy);
                byte[] byteArray = bytes.toByteArray();
                if (format == null){
                    if (new String(Arrays.copyOf(byteArray, 6)).equals("WEBVTT")) {
                        format = SubtitlesFormat.WEBVTT;
                    } else {
                        format = SubtitlesFormat.SRT;
                        log.warn("No format given, supposing {}", format);
                    }
                }

                this.content = SubtitlesContent.builder().content(bytes.toByteArray()).format(format).build();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            throw new IllegalArgumentException("Should either give iterator of cues, or content and format, or value and format");
        }
        this.owner = owner == null ? OwnerType.BROADCASTER : owner;
        this.language = language;
        this.cueCount = null;
        this.type = type == null ? SubtitlesType.CAPTION : type;
        this.creationInstant = created;
        this.lastModifiedInstant = lastModified;
        this.workflow = workflow == null ? SubtitlesWorkflow.FOR_PUBLICATION : workflow;
    }

    public static Subtitles tt888Caption(String mid, Duration offset, String content) {
        return builder()
                .mid(mid)
                .offset(offset)
                .language(DUTCH)
                .format(SubtitlesFormat.TT888)
                .content(content)
                .type(SubtitlesType.CAPTION)
                .owner(OwnerType.AUTHORITY)
                .build();

    }

    public static Subtitles webvtt(String mid, Duration offset, Locale language, String content) {
        return builder()
                .mid(mid)
                .offset(offset)
                .language(language)
                .format(SubtitlesFormat.WEBVTT)
                .content(content)
                .build();
    }

    public static Subtitles webvttTranslation(String mid, Duration offset, Locale language, String content) {
        Subtitles subtitles = webvtt(mid, offset, language, content);
        subtitles.setType(SubtitlesType.TRANSLATION);
        return subtitles;
    }

    public static Subtitles from(Iterator<StandaloneCue> cueIterator) {
        PeekingIterator<StandaloneCue> peeking = Iterators.peekingIterator(cueIterator);
        Subtitles subtitles = new Subtitles();
        subtitles.setCreationInstant(null);
        subtitles.setLastModifiedInstant(null);

        StringWriter writer = new StringWriter();
        try {
            StandaloneCue first = peeking.peek();
            WEBVTTandSRT.formatWEBVTT(peeking, writer);
            subtitles.setMid(first.getParent());
            subtitles.setLanguage(first.getLanguage());
            subtitles.setType(first.getType());
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
        return builder()
            .mid(mid)
            .offset(offset)
            .language(language)
            .format(SubtitlesFormat.WEBVTT)
            .content(writer.toString())
            .build();
    }

    public static Subtitles from(SubtitlesId sid, Iterator<Cue> cues) {
        StringWriter writer = new StringWriter();
        try {
            WEBVTTandSRT.formatWEBVTT(cues, writer);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return builder()
            .mid(sid.getMid())
            .language(sid.getLanguage())
            .type(sid.getType())
            .format(SubtitlesFormat.WEBVTT)
            .content(writer.toString())
            .build();
    }


    @Override
    public Duration getOffset() {
        return offset;
    }

    public Subtitles setOffset(Duration offset) {
        this.offset = offset;
        return this;
    }

    @XmlElement(required = true)
    public SubtitlesContent getContent() {
        return content;
    }

    public void setContent(SubtitlesContent content) {
        this.content = content;
        this.cueCount = null;
        getCueCount();
    }

    @Override
    public SubtitlesId getId() {
        return new SubtitlesId(mid, language, type);
    }

    public SubtitlesType getType() {
        return type;
    }

    public Locale getLanguage() {
        return language;
    }

    @XmlAttribute
    @Override
    public Integer getCueCount() {
        if (cueCount == null) {
            int result = 0;
            try {
                for (Cue cue : SubtitlesUtil.parse(this, false)) {
                    result++;
                }
                cueCount = result;
            } catch (Exception e) {
                log.warn("At cue {}: {}: {}", result, e.getClass(), e.getMessage());
                cueCount = 0;
            }
        }
        return cueCount;
    }

    public SubtitlesMetadata getMetadata() {
        return SubtitlesMetadataImpl.builder()
            .cueCount(getCueCount())
            .offset(getOffset())
            .id(getId())
            .workflow(getWorkflow())
            .build()
            ;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Subtitles");
        sb.append("{mid='").append(mid).append('\'');
        sb.append(", creationDate=").append(creationInstant);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Subtitles subtitles = (Subtitles) o;

        if (mid != null ? !mid.equals(subtitles.mid) : subtitles.mid != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return mid != null ? mid.hashCode() : 0;
    }


}
