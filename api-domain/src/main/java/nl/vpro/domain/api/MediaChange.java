/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Change;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.DateUtils;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 *
 * Note that this class is annotated with jaxb annotations, but is actually only marshalled to json.
 *
 * @author Roelof Jan Koekoek
 */
@XmlType(name = "changeType")
@JsonPropertyOrder({
    "sequence",
    "publishDate",
    "realPublishDate",
    "revision",
    "id",
    "mid",
    "deleted",
    "mergedTo",
    "reasons",
    "media",
})
@XmlAccessorType(XmlAccessType.NONE)
@Slf4j
@XmlRootElement(name = "change")
public class MediaChange extends Change<MediaObject> {

    @XmlAttribute
    @Getter
    @Setter
    @Deprecated
    private Long sequence;

    @XmlAttribute
    @Getter
    @Setter
    @Deprecated
    private Long revision;

    @XmlAttribute
    @Getter
    @Setter
    private String mergedTo;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant realPublishDate;

    @Getter
    @XmlElementWrapper
    @XmlElement(name = "reason")
    @JsonProperty("reasons")
    private List<String> reasons;

    public MediaChange() {
    }

    @lombok.Builder
    private MediaChange(
        Instant publishDate,
        Long revision,
        String mid,
        MediaObject media,
        Boolean deleted,
        MediaSince since,
        Boolean tail,
        @Nullable List<@NonNull String> reasons) {
        this(DateUtils.toLong(MediaSince.instant(publishDate, since)), revision, MediaSince.mid(mid, since), media, deleted);
        setPublishDate(MediaSince.instant(publishDate, since));
        setTail(tail);
        this.reasons = reasons;
    }

    private MediaChange(Long sequence, Long revision, String mid, MediaObject media, Boolean deleted) {
        super(mid, media, deleted);
        this.sequence = sequence;
        this.revision = revision;
    }

    public static MediaChange update(long sequence, Long revision, MediaObject media) {
        MediaChange change = new MediaChange(sequence, revision, media.getMid(), media, false);
        change.setPublishDate(media.getLastPublishedInstant());
        return change;
    }

    public static MediaChange delete(long sequence, Long revision, String mid) {
        return new MediaChange(sequence, revision, mid, null, true);
    }

    public static MediaChange delete(long sequence, Long revision, MediaObject media) {
        MediaChange change = new MediaChange(sequence, revision, media.getMid(), media, true);
        change.setPublishDate(media.getLastPublishedInstant());
        return change;
    }

    public static MediaChange merged(long sequence, Long revision, MediaObject media, String mergedTo) {
        MediaChange change = new MediaChange(sequence, revision, media.getMid(), media, true);
        change.setPublishDate(media.getLastPublishedInstant());
        change.setMergedTo(mergedTo);
        return change;
    }

    public static MediaChange tail(long sequence) {
        return tail(null, sequence);
    }

    public static MediaChange tail(Instant publishDate) {
        return MediaChange.builder()
            .publishDate(publishDate)
            .tail(true)
            .build();
    }


    public static MediaChange tail(MediaSince since) {
        return MediaChange.builder()
            .since(since)
            .tail(true)
            .build();
    }

    public static MediaChange tail(Instant publishDate, Long sequence) {
        MediaChange tail = new MediaChange(publishDate, sequence, null, null, null, null, true, null);
        return tail;
    }

    public static MediaChange of(Instant publishDate, MediaObject media) {
        return of(publishDate, media, null);
    }

    public static MediaChange of(Instant publishDate, MediaObject media, Long revision) {

        MediaChange change;
        final Instant lastPublished;
        if (publishDate == null) {
            lastPublished = media.getLastPublishedInstant();
        }  else {
            lastPublished = publishDate;
        }
        if (media.getWorkflow() == null) {
            log.warn("Workflow is null for {}", media.getMid());
            return null;
        }
        switch (media.getWorkflow()) {
            case DELETED:
            case REVOKED:
            case PARENT_REVOKED:
                change = MediaChange.builder()
                    .publishDate(lastPublished)
                    .revision(revision)
                    .mid(media.getMid())
                    .media(media)
                    .deleted(true)
                    .build();
                break;

            case PUBLISHED:
                change = new MediaChange(lastPublished, revision, media.getMid(), media, false, null, null, null);
                break;

            case MERGED:
                change = new MediaChange(lastPublished, revision, media.getMid(), media, true, null, null, null);
                change.setMergedTo(media.getMergedToRef());
                break;

            default:
                if (media.getWorkflow().isPublishable()) {
                    log.error("Unanticipated workflow for {} : {}. This is a bug.", media.getMid(), media.getWorkflow());
                } else {
                    log.warn("Invalid workflow for {} : {}", media.getMid(), media.getWorkflow());
                }
                change = null;
                break;

        }
        return change;

    }


    @XmlElement(name = "media")
    public MediaObject getMedia() {
        return getObject();
    }
    public void setMedia(MediaObject media) {
        setObject(media);
    }

    @XmlAttribute
    public String getMid() {
        return getId();
    }

    public void setMid(String mid) {
        if (getId() == null) {
            setId(mid);
        }
    }

    public MediaSince asSince() {
        return MediaSince.builder().instant(getPublishDate()).mid(getMid()).build();
    }

    @Override
    public String toString() {
        return super.toString() + (revision != null ? (":" + revision) : "") + (mergedTo != null  ? (":merged to " + mergedTo) : "") + (realPublishDate == null ? "" : (" (" + realPublishDate + ")"));
    }

    @Override
    public void setPublishDate(Instant instant) {
        if (this.realPublishDate == null) {
            this.realPublishDate = getPublishDate();
        }
        super.setPublishDate(instant);

    }

    /**
     * Sometimes the last returned change in a feed has an artificially increased publish date (only when using max).
     * This returns the 'real' publish date
     * @since 5.28
     */
    public Instant getRealPublishDate() {
        return realPublishDate == null ? getPublishDate() : realPublishDate;
    }

}
