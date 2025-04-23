package nl.vpro.domain.image.backend;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

import jakarta.validation.constraints.Positive;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.ChangeReport;
import nl.vpro.domain.image.*;

import static nl.vpro.domain.image.backend.BackendImage.BASE_URN;

/**
 * Extends {@link MutableMetadata} to include more information about the image, and its original source.
 * <p>
 * This targets mostly at fields that are  need to <em>serve</em> the image.
 *
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@SuppressWarnings("UnusedReturnValue")
public interface BackendImageMetadata<T extends BackendImageMetadata<T>>  extends MutableMetadata<T> {

    Float getHeightInMm();

    Float getWidthInMm();

    Long getSize();

    URI getDownloadUrl();

    String getEtag();

    Instant getUrlLastModified();

    ImageFormat getImageFormat();

    default String getMimeType() {
        ImageFormat format = getImageFormat();
        return format == null ? null : format.getMimeType();
    }

    default void copyTitleToDescriptionIfEmpty() {
        if (StringUtils.isBlank(getDescription())) {
            setDescription(getTitle());
        }
    }

    default Long getId() {
        String uri = getImageUri();
        return uri == null ? null : Long.parseLong(uri.substring(BASE_URN.length()));
    }

    default T setId(Long id) {
        return setImageUri(id == null ? null : BASE_URN + id);
    }



    T setHeightInMm(@Positive Float heightInMm);
    T setWidthInMm(@Positive Float widthInMm);
    T setSize(@Positive Long size);
    T setDownloadUrl(URI downloadUrl);
    T setEtag(String etag);
    T setUrlLastModified(Instant lastModified);
    T setImageFormat(ImageFormat imageFormat);
    default T setImageUri(String imageUri) {
        if  (! Objects.equals(imageUri, getImageUri())) {
            throw new UnsupportedOperationException();
        }
        return (T) this;
    }

    default T setMimeType(String mimeType) throws UnsupportedImageFormatException {
        return setImageFormat(ImageFormat.forMimeType(mimeType));
    }

    @Override
    default ChangeReport copyFrom(MutableMetadata<?> metadata) {
        ChangeReport change = MutableMetadata.super.copyFrom(metadata);

        if (metadata instanceof BackendImageMetadata<?> imageMetadata) {
            if (!Objects.equals(getHeightInMm(), imageMetadata.getHeightInMm())) {
                setHeightInMm(imageMetadata.getHeightInMm());
                change.change();
            }
            if (!Objects.equals(getWidthInMm(), imageMetadata.getWidthInMm())) {
                setWidthInMm(imageMetadata.getWidthInMm());
                change.change();
            }
            if (!Objects.equals(getSize(), imageMetadata.getSize())) {
                setSize(imageMetadata.getSize());
                change.change();
            }
            if (!Objects.equals(getDownloadUrl(), imageMetadata.getDownloadUrl())) {
                setDownloadUrl(imageMetadata.getDownloadUrl());
                change.change();
            }
            if (!Objects.equals(getEtag(), imageMetadata.getEtag())) {
                setEtag(imageMetadata.getEtag());
                change.change();
            }
            if (!Objects.equals(getUrlLastModified(), imageMetadata.getUrlLastModified())) {
                setUrlLastModified(imageMetadata.getUrlLastModified());
                change.change();
            }
            if (!Objects.equals(getImageFormat(), imageMetadata.getImageFormat())) {
                setImageFormat(imageMetadata.getImageFormat());
                change.change();
            }
        }
        return change;
    }

    @Override
    default ChangeReport copyFromIfTargetUnset(MutableMetadata<?> source) {
         ChangeReport change = MutableMetadata.super.copyFromIfTargetUnset(source);

        if (source instanceof BackendImageMetadata<?> image) {
            if ((getHeightInMm() == null || getHeightInMm() < 0f) && !Objects.equals(getHeightInMm(), image.getHeightInMm())) {
                setHeightInMm(image.getHeightInMm());
                change.change();
            }
            if ((getWidthInMm() == null || getWidthInMm() < 0f) && !Objects.equals(getWidthInMm(), image.getWidthInMm())) {
                setWidthInMm(image.getWidthInMm());
                change.change();
            }
            if ((getSize() == null || getSize() < 0L) && !Objects.equals(getSize(), image.getSize())) {
                setSize(image.getSize());
                change.change();
            }
            if (getDownloadUrl() == null  && !Objects.equals(getDownloadUrl(), image.getDownloadUrl())) {
                setDownloadUrl(image.getDownloadUrl());
                change.change();
            }
            if (StringUtils.isEmpty(getEtag()) && !Objects.equals(getEtag(), image.getEtag())) {
                setEtag(image.getEtag());
                change.change();
            }
            if (getUrlLastModified() == null && !Objects.equals(getUrlLastModified(), image.getUrlLastModified())) {
                setUrlLastModified(image.getUrlLastModified());
                change.change();
            }
            if (getImageFormat() == null && !Objects.equals(getImageFormat(), image.getImageFormat())) {
                setImageFormat(image.getImageFormat());
                change.change();
            }
        }
        return change;
    }


    @Override
    default  ChangeReport  copyFromIfSourceSet(MutableMetadata<?> metadata) {
        ChangeReport change = MutableMetadata.super.copyFromIfSourceSet(metadata);

        if (metadata instanceof BackendImageMetadata<?> source) {
            if (source.getHeightInMm() != null && source.getHeightInMm() >= 0f && !Objects.equals(getHeightInMm(), source.getHeightInMm())) {
                setHeightInMm(source.getHeightInMm());
                change.change();
            }
            if (source.getWidthInMm() != null && source.getWidthInMm() >= 0f && !Objects.equals(getWidthInMm(), source.getWidthInMm())) {
                setWidthInMm(source.getWidthInMm());
                change.change();
            }
            if (source.getSize() != null && source.getSize() >= 0L && !Objects.equals(getSize(), source.getSize())) {
                setSize(source.getSize());
                change.change();
            }
            if (source.getDownloadUrl() != null && !Objects.equals(getDownloadUrl(), source.getDownloadUrl())) {
                setDownloadUrl(source.getDownloadUrl());
                change.change();
            }
            if (StringUtils.isNotEmpty(source.getEtag()) && !Objects.equals(getEtag(), source.getEtag())) {
                setEtag(source.getEtag());
                change.change();
            }
            if (source.getUrlLastModified() != null && !Objects.equals(getUrlLastModified(), source.getUrlLastModified())) {
                setUrlLastModified(source.getUrlLastModified());
                change.change();
            }
            if (source.getImageFormat() != null && !Objects.equals(getImageFormat(), source.getImageFormat())) {
                setImageFormat(source.getImageFormat());
                change.change();
            }
        }
        return change;
    }

}
