package nl.vpro.domain.image;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.ChangeReport;

/**
 * Extends {@link Metadata} to include more information about the image, and its original source.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface ImageMetadata<T extends ImageMetadata<T>>  extends Metadata<T> {

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


    T setHeightInMm(Float heightInMm);
    T setWidthInMm(Float widthInMm);
    T setSize(Long size);
    T setDownloadUrl(URI downloadUrl);
    T setEtag(String etag);
    T setUrlLastModified(Instant lastModified);
    T setImageFormat(ImageFormat imageFormat);

    default T setMimeType(String mimeType) throws UnsupportedImageFormatException {
        return setImageFormat(ImageFormat.forMimeType(mimeType));
    }

    @Override
    default ChangeReport copyFrom(Metadata<?> metadata) {
        ChangeReport change = Metadata.super.copyFrom(metadata);

        if (metadata instanceof ImageMetadata) {
            ImageMetadata<?> imageMetadata = (ImageMetadata<?>) metadata;
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
    default ChangeReport copyFromIfTargetUnset(Metadata<?> source) {
         ChangeReport change = Metadata.super.copyFromIfTargetUnset(source);

        if (source instanceof ImageMetadata) {
            ImageMetadata<?> image = (ImageMetadata<?>) source;
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
    default  ChangeReport  copyFromIfSourceSet(Metadata<?> metadata) {
        ChangeReport change = Metadata.super.copyFromIfSourceSet(metadata);

        if (metadata instanceof ImageMetadata) {
            ImageMetadata<?> source = (ImageMetadata<?>) metadata;
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
