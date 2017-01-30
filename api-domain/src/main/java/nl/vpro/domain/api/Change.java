/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.DateUtils;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 */
@XmlType(name = "changeType", propOrder = {"media"})
@JsonPropertyOrder({
    "sequence",
    "publishDate",
    "revision",
    "mid",
    "deleted",
    "mergedTo",
    "media",
})
@XmlAccessorType(XmlAccessType.NONE)
@Slf4j
public class Change {

    @XmlAttribute
    private Long sequence;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant publishDate;

    @XmlAttribute
    private Long revision;

    @XmlAttribute
    private String mid;

    @XmlAttribute
    private Boolean deleted;

    @XmlAttribute
    private String mergedTo;

    @XmlElement
    private MediaObject media;

    @XmlAttribute
    Boolean tail = null;

    public Change() {
    }

    @Builder
    private Change(Instant publishDate, Long revision, String mid, MediaObject media, Boolean deleted) {
        this(DateUtils.toLong(publishDate), revision, mid, media, deleted);
        this.publishDate = publishDate;
    }

    private Change(Long sequence, Long revision, String mid, MediaObject media, Boolean deleted) {
        this.sequence = sequence;
        this.revision = revision;
        this.mid = mid;
        this.media = media;
        this.setDeleted(deleted);
    }

    public static Change update(long sequence, Long revision, MediaObject media) {
        Change change = new Change(sequence, revision, media.getMid(), media, false);
        change.setPublishDate(DateUtils.toInstant(media.getLastPublished()));
        return change;
    }

    public static Change delete(long sequence, Long revision, String mid) {
        return new Change(sequence, revision, mid, null, true);
    }

    public static Change delete(long sequence, Long revision, MediaObject media) {
        Change change = new Change(sequence, revision, media.getMid(), media, true);
        change.setPublishDate(DateUtils.toInstant(media.getLastPublished()));
        return change;
    }

    public static Change merged(long sequence, Long revision, MediaObject media, String mergedTo) {
        Change change = new Change(sequence, revision, media.getMid(), media, true);
        change.setPublishDate(DateUtils.toInstant(media.getLastPublished()));
        change.setMergedTo(mergedTo);
        return change;
    }

    public static Change tail(long sequence) {
        return tail(null, sequence);
    }

    public static Change tail(Instant publishDate) {
        return tail(publishDate, null);
    }


    public static Change tail(Instant publishDate, Long sequence) {
        Change tail = new Change(publishDate, sequence, null, null, null);
        tail.tail = true;
        return tail;
    }

    public static Change of(MediaObject media) {
        return of(media, null);
    }

    public static Change of(MediaObject media, Long revision) {

        Change change;
        final Instant lastPublished = DateUtils.toInstant(media.getLastPublished());
        if (media.getWorkflow() == null) {
            log.warn("Invalid workflow for {} : {}", media.getMid(), media.getWorkflow());
            return null;
        }
        switch (media.getWorkflow()) {
            case DELETED:
            case REVOKED:
                change = new Change(lastPublished, revision, media.getMid(), media, true);
                break;

            case PUBLISHED:
                change = new Change(lastPublished, revision, media.getMid(), media, false);
                break;

            case MERGED:
                change = new Change(lastPublished, revision, media.getMid(), media, true);
                change.setMergedTo(media.getMergedToRef());
                break;

            default:
                log.warn("Invalid workflow for {} : {}", media.getMid(), media.getWorkflow());
                change = null;
                break;

        }
        return change;

    }




    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }

    public Long getRevision() {
        return revision;
    }

    public void setRevision(Long revision) {
        this.revision = revision;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public MediaObject getMedia() {
        return media;
    }

    public void setMedia(MediaObject media) {
        this.media = media;
    }

    public boolean isDeleted() {
        return deleted != null ? deleted : false;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted == null ? null : deleted ? Boolean.TRUE : null;
    }

    public String getMergedTo() {
        return mergedTo;
    }

    public void setMergedTo(String mergedTo) {
        this.mergedTo = mergedTo;
    }

    public Instant getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Instant publishDate) {
        this.publishDate = publishDate;
    }


    public boolean isTail() {
        return tail != null && tail;
    }

    public void setTail(Boolean tail) {
        this.tail = tail;
    }

    @Override
    public String toString() {
        return "Change{" + publishDate + ", " +
                "sequence=" + sequence +
                ", revision=" + revision +
                ", mid='" + mid + '\'' +
                ", media=" + media +
                ", deleted=" + isDeleted() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Change change = (Change) o;

        if (sequence != null ? !sequence.equals(change.sequence) : change.sequence != null) return false;
        if (publishDate != null ? !publishDate.equals(change.publishDate) : change.publishDate != null) return false;
        if (revision != null ? !revision.equals(change.revision) : change.revision != null) return false;
        if (mid != null ? !mid.equals(change.mid) : change.mid != null) return false;
        if (deleted != null ? !deleted.equals(change.deleted) : change.deleted != null) return false;
        if (mergedTo != null ? !mergedTo.equals(change.mergedTo) : change.mergedTo != null) return false;
        if (media != null ? !media.equals(change.media) : change.media != null) return false;
        return tail != null ? tail.equals(change.tail) : change.tail == null;

    }

    @Override
    public int hashCode() {
        int result = sequence != null ? sequence.hashCode() : 0;
        result = 31 * result + (publishDate != null ? publishDate.hashCode() : 0);
        result = 31 * result + (revision != null ? revision.hashCode() : 0);
        result = 31 * result + (mid != null ? mid.hashCode() : 0);
        result = 31 * result + (deleted != null ? deleted.hashCode() : 0);
        result = 31 * result + (mergedTo != null ? mergedTo.hashCode() : 0);
        result = 31 * result + (media != null ? media.hashCode() : 0);
        result = 31 * result + (tail != null ? tail.hashCode() : 0);
        return result;
    }
}
