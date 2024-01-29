package nl.vpro.domain.image;

import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.support.License;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.validation.CRID;
import nl.vpro.validation.URI;
import nl.vpro.xml.bind.InstantXmlAdapter;


/**
 * This is the basic image presentation at VPRO. It implements {@link ImageMetadata}
 */
@Getter
@With
@EqualsAndHashCode
@ToString
@JsonDeserialize(builder = MetadataImpl.Builder.class)
@JsonPropertyOrder(
    {
        "type",
        "title",
        "height",
        "width",
        "sourceSet",
        "crids",
        "areaOfInterest",
        "lastModified",
        "creationDate"
    }
)
public class MetadataImpl implements Metadata {

    private final ImageType type;
    private final String title;
    private final String description;

    private final String alternative;

    private final License license;
    @URI(mustHaveScheme = true, minHostParts = 2)
    private final String source;
    private final String sourceName;
    private final String credits;

    private final Integer height;
    private final Integer width;

    @XmlAttribute(name = "lastModified")
    @JsonProperty("lastModified")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private final Instant lastModifiedInstant;

    @XmlAttribute(name = "creationDate")
    @JsonProperty("creationDate")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private final Instant creationInstant;

    private final List<@CRID String> crids;

    private final Area areaOfInterest;



    @lombok.Builder(
        builderClassName = "Builder", buildMethodName = "_build")
    protected MetadataImpl(
        ImageType type,
        String title,
        String description,
        String alternative,
        License license,
        String source,
        String sourceName,
        String credits,
        Integer height,
        Integer width,
        Instant lastModifiedInstant,
        Instant creationInstant,
        List<@CRID String> crids,
        @Nullable Area areaOfInterest) {

        this.type = type;
        this.title = title;
        this.description = description;
        this.alternative = alternative;
        this.license = license;
        this.source = source;
        this.sourceName = sourceName;
        this.credits = credits;
        this.height = height;
        this.width = width;
        this.lastModifiedInstant = lastModifiedInstant;

        this.creationInstant = creationInstant;
        this.crids = crids == null ? new ArrayList<>() : crids;
        this.areaOfInterest = areaOfInterest;
    }




    public abstract static class MetaBuilder<B extends MetaBuilder<B>> implements LombokBuilder<B> {
        protected List<String> crids;
        protected final List<String> crid = new ArrayList<>();


        public B crid(String crid){
            this.crid.add(crid);
            return (B) this;
        }

        protected void prebuild() {
            if (crids == null) {
                crids(crid);
            } else {
                crids.addAll(crid);
            }
        }

    }

    public static class Builder extends MetaBuilder<Builder> {
        public MetadataImpl build() {
            prebuild();
            return _build();
        }
    }

}
