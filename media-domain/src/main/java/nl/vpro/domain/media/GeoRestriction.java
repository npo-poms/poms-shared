package nl.vpro.domain.media;

import lombok.*;

import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Indicates for which geographical {@link Region}, something must be available. This extends from {@link Restriction},
 * so this restriction may be under {@link nl.vpro.domain.Embargo embargo}.
 */
@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "geoRestrictionType")
@SuppressWarnings("serial")
@ToString
public class GeoRestriction extends Restriction<GeoRestriction> implements Comparable<GeoRestriction> {


    public static class Builder extends RestrictionBuilder<Builder> {

    }

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "nl.vpro.constraints.NotNull")
    @XmlAttribute(name = "regionId")
    @Getter
    @Setter
    protected Region region;

    @Column(nullable=false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "nl.vpro.constraints.NotNull")
    @Getter
    @Setter
    @XmlAttribute
    protected Platform platform = Platform.INTERNETVOD;


    @XmlTransient
    @Getter
    @Setter
    private boolean authoritative = false;

    public GeoRestriction() {
    }

    public GeoRestriction(String region) {
        // When loading from JSON
        String[] split = region.split(":", 2);
        if (split.length == 1) {
            this.region = Region.valueOf(split[0]);
        } else {
            this.platform = Platform.valueOf(split[0]);
            this.region = Region.valueOf(split[1]);
        }
    }

    public GeoRestriction(Region region) {
        this.region = region;
    }

    public GeoRestriction(Region region, Instant start, Instant stop) {
        super(start, stop);
        this.region = region;
        authoritative = true;
    }

    @lombok.Builder(builderClassName = "Builder")
    private GeoRestriction(
        @Nullable Long id,
        @NonNull @lombok.NonNull Region region,
        @Nullable Instant start,
        @Nullable Instant stop,
        @Nullable Platform platform, boolean authoritative) {
        super(id, start, stop);
        this.region = region;
        this.authoritative = authoritative;
        this.platform = platform == null ? Platform.INTERNETVOD : platform;
    }

    public GeoRestriction(GeoRestriction source) {
        super(source);
        this.region = source.region;
        this.authoritative = source.authoritative;
        this.platform = source.platform;
    }

    public static GeoRestriction copy(GeoRestriction source){
        if(source == null) {
            return null;
        }
        return new GeoRestriction(source);
    }


    @JsonValue
    protected String getJsonValue() {
        return getJsonValue(platform, region);
    }

    public static String getJsonValue(Platform platform, Region region) {
        return (platform == null || platform == Platform.INTERNETVOD ? "" : (platform.name() + ":")) + region.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        GeoRestriction that = (GeoRestriction) o;

        if (authoritative != that.authoritative) return false;
        if (region != that.region) return false;
        return platform == that.platform;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + region.hashCode();
        result = 31 * result + platform.hashCode();
        result = 31 * result + (authoritative ? 1 : 0);
        return result;
    }

    @Override
    public int compareTo(GeoRestriction o) {
        Platform p1 = platform == null ? Platform.INTERNETVOD : platform;
        Platform p2 = o.platform == null ? Platform.INTERNETVOD : o.platform;
        int result = p1.compareTo(p2);
        if (result != 0) {
            return result;
        }
        result = region.compareTo(o.region);
        if (result != 0) {
            return result;
        }
        return Objects.compare(start, o.start, (o1, o2) -> o1 == null ? -1 : (o2 == null ? 1 : o1.compareTo(o2)));

    }


}
