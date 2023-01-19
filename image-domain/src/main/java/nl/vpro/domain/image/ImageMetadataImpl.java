package nl.vpro.domain.image;

import lombok.*;

import java.time.Instant;
import java.util.*;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.*;
import com.google.common.annotations.Beta;

import nl.vpro.domain.support.License;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.jackson2.Views;
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
@JsonDeserialize(builder = ImageMetadataImpl.Builder.class)
@JsonPropertyOrder(
    {
        "type","title", "height", "width", "sourceSet", "sourceSetString", "crids", "areaOfInterest", "lastModified", "creationDater"
    }
)
public class ImageMetadataImpl implements ImageMetadata {

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
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private final Instant creationInstant;


    @JsonView(Views.Normal.class)
    private final ImageSourceSet sourceSet;

    private final List<@CRID String> crids;

    private final Area areaOfInterest;



    @lombok.Builder(
        builderClassName = "Builder", buildMethodName = "_build", toBuilder = true)
    private ImageMetadataImpl(
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
        Map<ImageSource.Key, ImageSource> sourceSet,
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
        this.sourceSet = new ImageSourceSet();
        if (sourceSet != null) {
            this.sourceSet.putAll(sourceSet);
        }
        this.crids = crids == null ? new ArrayList<>() : crids;
        this.areaOfInterest = areaOfInterest;
    }

    @JsonView(Views.Model.class)
    @Beta
    public String getSourceSetString() {
        return Objects.toString(getSourceSet());
    }

    /**
     * This is horrible.
     * See <a href="https://bugs.openjdk.org/browse/JDK-8071693">JDK bug</a>
     */
    @Override
    public RelativePoint getPointOfInterest() {
        return ImageMetadata.super.getPointOfInterest();
    }


    @SuppressWarnings("UnusedReturnValue")
    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder implements LombokBuilder<Builder> {

        private final Map<ImageSource.Key, ImageSource> _sourceSet = new TreeMap<>();

        private final List<String> crid = new ArrayList<>();

        public Builder addSourceSet(Map<ImageSource.Key, ImageSource> sourceSet){
            if (sourceSet != null) {
                _sourceSet.putAll(sourceSet);
            }
            return this;
        }

        public Builder crid(String crid){
            this.crid.add(crid);
            return this;
        }

        public Builder longHeight(Long height){
            return height(height == null ? null : height.intValue());
        }
        public Builder longWidth(Long width){
            return width(width == null ? null : width.intValue());
        }

        public Builder dimensions(int width, int height) {
            return width(width).height(height);
        }

        public Builder imageSource(ImageSource... source) {
            for (ImageSource s : source) {
                _sourceSet.put(new ImageSource.Key(s.getType(), s.getImageFormat()), s);
            }
            return this;
        }

        public Builder from(ImageMetadata imageMetadata) {
            return
                from((Metadata<?>) imageMetadata)
                    .crids(imageMetadata.getCrids())
                    .areaOfInterest(imageMetadata.getAreaOfInterest())
                    .addSourceSet(imageMetadata.getSourceSet())
                    ;

        }

        public ImageMetadataImpl build() {
            if (sourceSet != null) {
                _sourceSet.putAll(sourceSet);
            }
            if (crids != null) {
                crids.addAll(crid);
            } else {
                crids(crid);
            }
            return sourceSet(_sourceSet)._build();
        }

    }

}
