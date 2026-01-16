package nl.vpro.domain.image;

import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.util.*;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import nl.vpro.domain.support.License;
import nl.vpro.jackson.Views;
import nl.vpro.validation.CRID;


/**
 * This is the basic image presentation at VPRO. It implements {@link ImageMetadata}
 */
@Getter
@ToString
@JsonDeserialize(builder = ImageMetadataImpl.Builder.class)
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
        "creationDate",
        "sourceSet"
    }
)
public class ImageMetadataImpl extends MetadataImpl implements ImageMetadata {

    @JsonView(Views.Model.class)
    private final ImageSourceSet sourceSet;



    @lombok.Builder(
        builderClassName = "Builder", buildMethodName = "_build", builderMethodName = "ibuilder")
    protected ImageMetadataImpl(
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
        @Nullable Area areaOfInterest,
        Map<ImageSource.Key, ImageSource> sourceSet
        ) {
        super(type, title, description, alternative, license, source, sourceName, credits, height, width, lastModifiedInstant, creationInstant, crids, areaOfInterest);
        this.sourceSet = new ImageSourceSet(this);
        if (sourceSet != null) {
            this.sourceSet.putAll(sourceSet);
        }
    }

    /**
     * This is horrible.
     * See <a href="https://bugs.openjdk.org/browse/JDK-8071693">JDK bug</a>
     */
    @Override
    @JsonView(Views.Model.class)
    public RelativePoint getPointOfInterest() {
        return ImageMetadata.super.getPointOfInterest();
    }


    @SuppressWarnings("UnusedReturnValue")
    @JsonPOJOBuilder(withPrefix = "")
    @JsonIgnoreProperties("picture")
    public static class Builder extends MetadataImpl.MetaBuilder<Builder> {

        private final Map<ImageSource.Key, ImageSource> _sourceSet = new LinkedHashMap<>();


        public Builder addSourceSet(Map<ImageSource.Key, ImageSource> sourceSet){
            if (sourceSet != null) {
                _sourceSet.putAll(sourceSet);
            }
            return this;
        }

        public Builder imageSource(ImageSource... source) {
            for (ImageSource s : source) {
                _sourceSet.put(new ImageSource.Key(s.getType(), s.getFormat()), s);
            }
            return this;
        }

        public Builder from(ImageMetadata imageMetadata) {
            return
                from((Metadata) imageMetadata)
                    .addSourceSet(imageMetadata.getSourceSet())
                    ;

        }

        @Override
        public void  prebuild() {
            super.prebuild();
            if (sourceSet != null) {
                _sourceSet.putAll(sourceSet);
            }
            sourceSet(_sourceSet);
        }

        public ImageMetadataImpl build() {
            prebuild();
            return _build();
        }

    }

}
