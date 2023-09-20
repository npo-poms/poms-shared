/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */

package nl.vpro.domain.image.backend;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URI;
import java.time.Instant;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.persistence.*;
import javax.validation.constraints.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.meeuw.functional.ThrowAnySupplier;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.*;
import nl.vpro.domain.image.ImageFormat;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.support.License;
import nl.vpro.domain.user.Editor;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.validation.PomsValidatorGroup;
import nl.vpro.validation.WarningValidatorGroup;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * <p>This is the image object as used by <a href="https://images.poms.omroep.nl"> poms image server</a></p>
 * <p>
 * It is the database entity, and also has an XML/Json representation, but this is currently not exposed in public API's.
 * <p>
 * It can also be used to represent cached converts
 * </p>
 *
 */
@SuppressWarnings("WSReferenceInspection")
@Entity(name = "Image")
@XmlRootElement(name = "image")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "imageType",
    propOrder = {
        "title",
        "description",
        "height",
        "width",
        "heightInMm",
        "widthInMm",
        "mimeType",
        "size",
        "downloadUrl",
        "etag",
        "urlLastModified",
        "source",
        "sourceName",
        "date",
        "credits",
        "license",
        "data",
        "broadcaster",
        "owner"
    }
)
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder", buildMethodName= "_build")
@Slf4j
public class BackendImage extends AbstractPublishableObject<BackendImage> implements BackendImageMetadata<BackendImage>, Identifiable<Long>, Serializable, MutableOwnable {
    @Serial
    private static final long serialVersionUID = -140942203904508506L;

    public static final String BASE_URN = "urn:vpro:image:";


    public static class Builder {
        private Editor createdBy;
        private Editor lastModifiedBy;

        public Builder createdBy(Editor editor) {
            this.createdBy = editor;
            return this;
        }

        public Builder lastModifiedBy(Editor editor) {
            this.lastModifiedBy = editor;
            return this;
        }

        public Builder downloadUri(URI downloadUri) {
            return downloadUrl(downloadUri.toString());
        }
        public BackendImage build()  {
            BackendImage image =_build();
            image.setCreatedBy(createdBy);
            image.setLastModifiedBy(lastModifiedBy);
            return image;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="imageType")
    @XmlAttribute
    @Getter
    @Setter
    private ImageType type;

    @Enumerated(EnumType.STRING)
    @XmlTransient
    @Getter
    private ImageFormat imageFormat;

    @Column(nullable = false)
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    @Getter
    private String title;

    @Column
    @Nullable
    @Getter
    private String description;

    @Getter
    @Setter
    @Positive
    private Integer height;

    @Getter
    @Setter
    @Positive
    private Integer width;

    @XmlElement(name = "heightMm")
    @Getter
    @Positive
    private Float heightInMm;

    @XmlElement(name = "widthMm")
    @Getter
    @Positive
    private Float widthInMm;

    @Getter
    private Long size;

    @Column(unique = true, length = 1024)
    @nl.vpro.validation.URI(groups = PomsValidatorGroup.class)
    private String downloadUrl;

    @Getter
    private String etag;

    @Column
    @Getter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant urlLastModified;

    @XmlTransient
    @Getter
    @Setter
    private byte[] hash;


    @NotNull(groups = {WarningValidatorGroup.class})
    @XmlElement
    @Embedded
    @Getter
    private License license;

    @Getter
    @Setter
    private String source;

    @Getter
    @Setter
    private String sourceName;

    @Getter
    @Setter
    private String date;

    @Getter
    @Setter
    private String credits;

    @Transient
    @XmlTransient
    private ThrowAnySupplier<ImageStream> imageStream;
    /**
     * @since 5.10
     */
    @Getter
    @Setter
    private String broadcaster;


    /**
     * @since 5.10
     */
    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private OwnerType owner;

    public BackendImage() {
    }

    public BackendImage(String title) {
        setTitle(title);
    }


    @Override
    public BackendImage setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    @Override
    public void setTitle(String title) {
        if(title == null || title.length() < 255) {
            this.title = title;
        } else {
            this.title = title.substring(0, 255);
        }
    }

    @Override
    public void setDescription(String description) {
        if(description == null || description.length() < 255) {
            this.description = description;
        } else {
            this.description = description.substring(0, 255);
        }
    }

    @Override
    public void setLicense(License license) {
        this.license = license;
    }

    @Override
    @XmlElement
    public String getMimeType() {
        if(imageFormat == null) {
            return null;
        }
        return imageFormat.getMimeType();
    }

    @Override
    public BackendImage setHeightInMm(Float heightInMm) {
        this.heightInMm = heightInMm;
        return this;
    }

    @Override
    public BackendImage setWidthInMm(Float widthInMm) {
        this.widthInMm = widthInMm;
        return this;
    }

    @Override
    public BackendImage setSize(Long size) {
        this.size = size;
        return this;
    }

    @Override
    public BackendImage setId(Long id) {
        this.id = id;
        return this;
    }

    @SneakyThrows
    @XmlTransient
    public ImageStream getImageStream() throws NotFoundException {
        return imageStream == null ? null : imageStream.getThrows();
    }

   public BackendImage supplyImageStream(ThrowAnySupplier<ImageStream> data) {
        this.imageStream = data;
        return this;
    }

    @XmlElement(name = "data")
    @XmlMimeType("application/octet-stream")
    public DataHandler getData() {
        if (imageStream == null) {
            return null;
        } else {
            return new DataHandler(new DataSource() {
                @Override
                public InputStream getInputStream() throws IOException {
                    ImageStream is = BackendImage.this.getImageStream();
                    return is == null ? null : is.getStream();
                }

                @Override
                public OutputStream getOutputStream() throws IOException {
                    throw new UnsupportedOperationException("Immutable blob input");
                }

                @Override
                public String getContentType() {
                    return getMimeType();
                }

                @Override
                public String getName() {
                    return title;
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     *
     * For images (in the image database) it is "urn:vpro:image:"
     */
    @Override
    protected String getUrnPrefix() {
        return BASE_URN;
    }

    @Override
    public URI getDownloadUrl() {
        try {
            return downloadUrl == null ? null : URI.create(downloadUrl);
        } catch (IllegalArgumentException use) {
            log.warn("Invalid url {} found in {}: {}", downloadUrl, this, use.getMessage());
            return null;
        }
    }

    @Override
    public BackendImage setDownloadUrl(URI downloadUrl) {
        this.downloadUrl = downloadUrl == null ? null : downloadUrl.toString();
        return this;
    }

    @Override
    public String getImageUri() {
        return getUrn();
    }

    @Override
    public BackendImage setEtag(String etag) {
        this.etag = etag;
        return this;
    }

    @Override
    public BackendImage setUrlLastModified(Instant lastModified) {
        this.urlLastModified = lastModified;
        return this;
    }

}
