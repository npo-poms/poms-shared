/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0 VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Range;

import nl.vpro.domain.Xmlns;
import nl.vpro.util.Ranges;

/**
 * The most basic organizational entity at NPO is a broadcaster. People and content should always be associated with at least one broadcaster.
 */
@Entity
@XmlType(name = "broadcasterType", namespace = Xmlns.MEDIA_NAMESPACE)
@Cacheable
public class Broadcaster extends Organization {

    @Serial
    private static final long serialVersionUID = 4814193296511306394L;

    @Column(unique = true)
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @XmlTransient
    protected String whatsOnId;

    @Column(unique = true)
    @Size(min = 1, max = 255, message = "0 < neboId < 256")
    @XmlTransient
    protected String neboId;

    @Column(unique = true)
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @XmlTransient
    protected String misId;

    @Column
    @Getter
    @Setter
    @XmlTransient
    protected String domain;

    @Column
    @Getter
    @Setter
    @XmlTransient
    protected LocalDate start;


    @Column
    @Getter
    @Setter
    @XmlTransient
    protected LocalDate stop;

    @Column
    @Getter
    @XmlTransient
    private Instant lastModified;

    @Column(columnDefinition = "boolean default true")
    @Getter
    @XmlTransient
    private boolean display = true;

    public Range<LocalDate> asRange() {
        return Ranges.closedClosed(start, stop);
    }


    public static Broadcaster of(String id) {
        return new Broadcaster(id);
    }

    public static Broadcaster whatsOnBroadcaster(String id) {
        return new Broadcaster(null, null, id, null, null);
    }

    public static Broadcaster neboBroadcaster(String id) {
        return new Broadcaster(null, null, null, id, null);
    }

    public Broadcaster() {
    }

    public Broadcaster(String id) {
        this(id, id);
    }

    public Broadcaster(String id, String displayName) {
        this(id, displayName, id, id, id);
    }

    /**
     * @param id          poms broadcaster name
     * @param displayName broadcaster display name
     * @param whatsOnId   what's on broadcaster name
     * @param neboId      nebo broadcaster name
     */
    public Broadcaster(
        String id,
        String displayName,
        String whatsOnId,
        String neboId,
        String misId) {
        super(id, displayName);
        this.whatsOnId = whatsOnId;
        this.neboId = neboId;
        this.misId = misId;
    }


    @lombok.Builder
    private Broadcaster(
        String id,
        String displayName,
        String whatsOnId,
        String neboId,
        String misId,
        String domain,
        LocalDate start,
        LocalDate stop,
        Boolean display) {
        this(id, displayName,whatsOnId, neboId, misId);
        this.domain = domain;
        this.start = start;
        this.stop = stop;
        this.display = display == null || display;
    }


    @SuppressWarnings("CopyConstructorMissesField")
    public Broadcaster(Broadcaster b) {
        this(b.id, b.displayName, b.whatsOnId, b.neboId, b.misId, b.domain, b.start, b.stop, b.display);
    }

    @Override
    @Size(min = 2, max = 4, message = "2 < id < 5")
    @javax.validation.constraints.Pattern(regexp = "[A-Z0-9_-]{2,4}", message = "Broadcaster id ${validatedValue} should match {regexp}")
    public String getId() {
        return super.getId();
    }

    @Override
    public boolean display() {
        return display;
    }

    public String getWhatsOnId() {
        if(StringUtils.isBlank(whatsOnId)) {
            return getId();
        }
        return whatsOnId;
    }

    public String getNeboId() {
        if(StringUtils.isBlank(neboId)) {
            return getId();
        }
        return neboId;
    }

    public String getMisId() {
        if(StringUtils.isBlank(misId)) {
            return getId();
        }
        return misId;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Broadcaster other)) {
            return false;
        }
        if(id != null) {
            return super.equals(o);
        }
        if(whatsOnId != null) {
            return whatsOnId.equals(other.getWhatsOnId());
        }
        if(neboId != null) {
            return neboId.equals(other.getNeboId());
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public static Comparator<Broadcaster> existingFirst(LocalDate date) {
        return Comparator.nullsLast(
            Comparator.<Broadcaster, Boolean>comparing(b -> b.asRange().contains(date)).reversed()
            .thenComparing(b -> b.getDisplayName())
        );

    }
}
