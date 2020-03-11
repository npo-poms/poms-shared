/*
 * Copyright (C) 2012 All rights reserved VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.Xmlns;

@Entity
@XmlType(name = "broadcasterType", namespace = Xmlns.MEDIA_NAMESPACE)
@Cacheable
public class Broadcaster extends Organization {

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
    protected String domain;

    public static Broadcaster of(String id) {
        return new Broadcaster(id);
    }

    public static Broadcaster whatsOnBroadcaster(String id) {
        return new Broadcaster(null, null, id, null, null);
    }

    public static Broadcaster neboBroadcaster(String id) {
        return new Broadcaster(null, null, null, id, null);
    }

    protected Broadcaster() {
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
    @lombok.Builder
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

    public Broadcaster(Broadcaster b) {
        this(b.id, b.displayName, b.whatsOnId, b.neboId, b.misId);
    }

    @Override
    @Size(min = 2, max = 4, message = "2 < id < 5")
    @javax.validation.constraints.Pattern(regexp = "[A-Z0-9_-]{2,4}", message = "Broadcaster id ${validatedValue} should match {regexp}")
    public String getId() {
        return super.getId();
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
        if(!(o instanceof Broadcaster)) {
            return false;
        }
        if(id != null) {
            return super.equals(o);
        }
        Broadcaster other = (Broadcaster)o;
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
}
