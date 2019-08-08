package nl.vpro.domain.media;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import nl.vpro.domain.gtaa.Status;
import nl.vpro.validation.NoHtml;

@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Getter
@Setter
public class GtaaGeoLocationRecord implements Serializable, Comparable<GtaaGeoLocationRecord> {
    private static final long serialVersionUID = 0L;

    @Column(name = "gtaa_uri")
    @Getter
    @Id
    @lombok.NonNull
    private String uri;

    @Column(nullable = true, length = 30, name = "gtaa_status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @NoHtml
    @XmlElement
    @lombok.NonNull
    private String name;

    @NoHtml
    @XmlElement
    private String description;

    public GtaaGeoLocationRecord() {}

    @lombok.Builder(builderClassName = "Builder")
    public GtaaGeoLocationRecord(@lombok.NonNull String uri, Status status, @lombok.NonNull String name, String description) {
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.status = status;
    }

    public void setStatus(Status status){
        this.status = status;
    }
    public void setStatus(String status){
        this.status = (status == null)? null : Status.valueOf(status);
    }

    @Override
    public int compareTo(@NonNull GtaaGeoLocationRecord geoLocationRecord) {
        if (uri == null) {
            if (geoLocationRecord.uri == null) {
                return 0;
            }
            return -1;
        }
        int result = uri.compareToIgnoreCase(geoLocationRecord.uri);
        return result == 0 ? uri.compareTo(geoLocationRecord.uri) : result;
    }
}
