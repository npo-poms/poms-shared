package nl.vpro.domain.image;

import java.net.URI;

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

    ImageFormat getImageFormat();


    default String getMimeType() {
        ImageFormat format = getImageFormat();
        return format == null ? null : format.getMimeType();
    }


    T setHeightInMm(Float heightInMm);
    T setWidthInMm(Float widthInMm);
    T setSize(Long size);
    T setDownloadUrl(URI downloadUrl);
    T setEtag(String etag);
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
            setImageFormat(image.getImageFormat());
        }
    }

    @Override
    default void copyFromIfUnset(Metadata<?> metadata) {
        Metadata.super.copyFromIfUnset(metadata);

        if (metadata instanceof ImageMetadata) {
            ImageMetadata<?> image = (ImageMetadata<?>) metadata;
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
            if (StringUtils.isEmpty(image.getEtag())) {
                setEtag(image.getEtag());
            }
            if (getImageFormat() == null) {
                setImageFormat(image.getImageFormat());
            }
        }
    }

}
