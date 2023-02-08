package nl.vpro.domain.image;

import java.time.Instant;

import com.fasterxml.jackson.annotation.*;

import nl.vpro.domain.support.License;


@JsonPropertyOrder(
    {
        "type",
        "title",
        "height",
        "width",
        "sourceSet",
        "sourceSetString",
        "crids",
        "areaOfInterest",
        "lastModifiedInstant",
        "creationInstant"
    }
)
public  class MetadataWrapper implements Metadata {

    @JsonIgnore
    protected final Metadata wrapped;

    public MetadataWrapper(Metadata wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ImageType getType() {
        return wrapped.getType();
    }

    @Override
    public String getTitle() {
        return wrapped.getTitle();
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription();
    }

    @Override
    public License getLicense() {
        return wrapped.getLicense();
    }

    @Override
    public String getSource() {
        return wrapped.getSource();
    }

    @Override
    public String getSourceName() {
        return wrapped.getSourceName();
    }

    @Override
    public String getCredits() {
        return wrapped.getCredits();
    }

    @Override
    public Integer getHeight() {
        return wrapped.getHeight();
    }

    @Override
    public Integer getWidth() {
        return wrapped.getWidth();
    }

    @Override
    @JsonProperty("lastModified")
    public Instant getLastModifiedInstant() {
        return wrapped.getLastModifiedInstant();
    }

    @Override
    @JsonProperty("creationDate")
    public Instant getCreationInstant() {
        return wrapped.getCreationInstant();
    }

    @Override
    public Area getAreaOfInterest() {
        return wrapped.getAreaOfInterest();
    }
}
