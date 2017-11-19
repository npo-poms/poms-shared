package nl.vpro.domain.image;

import java.net.URI;
import java.time.Instant;

import org.apache.commons.lang3.StringUtils;

/**
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
    default void copyFrom(Metadata<?> metadata) {
        Metadata.super.copyFrom(metadata);

        if (metadata instanceof ImageMetadata) {
            ImageMetadata<?> image = (ImageMetadata<?>) metadata;
            setHeightInMm(image.getHeightInMm());
            setWidthInMm(image.getWidthInMm());
            setSize(image.getSize());
            setDownloadUrl(image.getDownloadUrl());
            setEtag(image.getEtag());
            setUrlLastModified(image.getUrlLastModified());
            setImageFormat(image.getImageFormat());
        }
    }

    @Override
    default void copyFromIfTargetUnset(Metadata<?> source) {
        Metadata.super.copyFromIfTargetUnset(source);

        if (source instanceof ImageMetadata) {
            ImageMetadata<?> image = (ImageMetadata<?>) source;
            if (getHeightInMm() == null || getHeightInMm() < 0f) {
                setHeightInMm(image.getHeightInMm());
            }
            if (getWidthInMm() == null || getWidthInMm() < 0f) {
                setWidthInMm(image.getWidthInMm());
            }
            if (getSize() == null || getSize() < 0L) {
                setSize(image.getSize());
            }
            if (getDownloadUrl() == null) {
                setDownloadUrl(image.getDownloadUrl());
            }
            if (StringUtils.isEmpty(getEtag())) {
                setEtag(image.getEtag());
            }
            if (getUrlLastModified() == null) {
                setUrlLastModified(image.getUrlLastModified());
            }
            if (getImageFormat() == null) {
                setImageFormat(image.getImageFormat());
            }
        }
    }


    @Override
    default void copyFromIfSourceSet(Metadata<?> metadata) {
        Metadata.super.copyFromIfSourceSet(metadata);

        if (metadata instanceof ImageMetadata) {
            ImageMetadata<?> source = (ImageMetadata<?>) metadata;
            if (source.getHeightInMm() != null && source.getHeightInMm() >= 0f) {
                setHeightInMm(source.getHeightInMm());
            }
            if (source.getWidthInMm() != null && source.getWidthInMm() >= 0f) {
                setWidthInMm(source.getWidthInMm());
            }
            if (source.getSize() != null && source.getSize() >= 0L) {
                setSize(source.getSize());
            }
            if (source.getDownloadUrl() != null) {
                setDownloadUrl(source.getDownloadUrl());
            }
            if (StringUtils.isNotEmpty(source.getEtag())) {
                setEtag(source.getEtag());
            }
            if (source.getUrlLastModified() != null) {
                setUrlLastModified(source.getUrlLastModified());
            }
            if (source.getImageFormat() != null) {
                setImageFormat(source.getImageFormat());
            }
        }
    }

}
