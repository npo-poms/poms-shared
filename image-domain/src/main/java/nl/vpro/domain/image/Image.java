/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */

package nl.vpro.domain.image;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.Instant;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.AbstractPublishableObject;
import nl.vpro.domain.support.License;
import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.user.Editor;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.validation.WarningValidatorGroup;
import nl.vpro.xml.bind.InstantXmlAdapter;

@SuppressWarnings("WSReferenceInspection")
@Entity(
)
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
public class Image extends AbstractPublishableObject<Image> implements ImageMetadata<Image>, Serializable, MutableOwnable {
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
        public Image build()  {
            Image image =_build();
            image.setCreatedBy(createdBy);
            image.setLastModifiedBy(lastModifiedBy);
            return image;
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name="imageType")
    @XmlAttribute
    private ImageType type;

    @Enumerated(EnumType.STRING)
    @XmlTransient
    private ImageFormat imageFormat;

    @Column(nullable = false)
    @Size.List({
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}"),
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")
    })
    private String title;

    @Column
    private String description;

    private Integer height;

    private Integer width;

    @XmlElement(name = "heightMm")
    private Float heightInMm;

    @XmlElement(name = "widthMm")
    private Float widthInMm;

    private Long size;

    @Column(unique = true, length = 1024)
    private String downloadUrl;

    private String etag;

    @Column
    @Getter
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant urlLastModified;

    @XmlTransient
    private byte[] hash;


    @NotNull(groups = {WarningValidatorGroup.class})
    @XmlElement
    @Embedded
    @Getter
    @Setter
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

    @Lob
    @XmlTransient
    private Blob data;

    @Transient
    @XmlTransient
    private InputStream cachedInputStream;


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

    public Image() {
    }

    public Image(String title) {
        setTitle(title);
    }


    @Override
    public ImageType getType() {
        return type;
    }

    @Override
    public void setType(ImageType imageType) {
        this.type = imageType;
    }


    @Override
    public ImageFormat getImageFormat() {
        return imageFormat;
    }

    @Override
    public Image setImageFormat(ImageFormat imageFormat) {
        this.imageFormat = imageFormat;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        if(title == null || title.length() < 255) {
            this.title = title;
        } else {
            this.title = title.substring(255);
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        if(description == null || description.length() < 255) {
            this.description = description;
        } else {
            this.description = description.substring(255);
        }
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
    public Integer getHeight() {
        return height;
    }

    @Override
    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public Integer getWidth() {
        return width;
    }

    @Override
    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public Float getHeightInMm() {
        return heightInMm;
    }

    @Override
    public Image setHeightInMm(Float heightInMm) {
        this.heightInMm = heightInMm;
        return this;
    }


    @Override
    public Float getWidthInMm() {
        return widthInMm;
    }

    @Override
    public Image setWidthInMm(Float widthInMm) {
        this.widthInMm = widthInMm;
        return this;
    }
    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public Image setSize(Long size) {
        this.size = size;
        return this;
    }

    public String getSizeFormatted() {
        if (size == null){
            return null;
        }
        float result;
        String unit;

        // MM: BTW, these units are incorrect.
        // In S.I. the prefixes T, M and k are powers of 10.
        // Correct would be useage of prefixes Ti, Mi and Ki.
        if(size > 1024 * 1024 * 1024) {
            result = size / 1024 * 1024 * 1024;
            unit = "TB";
        } else if(size > 1024 * 1024) {
            result = size / 1024 * 1024;
            unit = "MB";
        } else {
            result = size / 1024;
            unit = "kB";
        }

        return String.format("%1$.1f %2$s", result, unit);
    }

    @XmlTransient
    public Blob getBlob() {
        return data;
    }

    public Image setBlob(Blob data) {
        this.data = data;
        return this;
    }

    @XmlElement(name = "data")
    @XmlMimeType("application/octet-stream")
    public DataHandler getData() {
        return new DataHandler(new DataSource() {
            @Override
            public InputStream getInputStream() throws IOException {
                if(cachedInputStream == null) {
                    try {
                        return data.getBinaryStream();
                    } catch(SQLException e) {
                        throw new IOException(e);
                    }
                }

                return cachedInputStream;
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                try {
                    return data.setBinaryStream(1);
                } catch(SQLException e) {
                    throw new IOException(e);
                }
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


    void setCachedInputStream(InputStream cachedInputStream) {
        this.cachedInputStream = cachedInputStream;
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
            log.warn("Invalid url found in database {}: {}", downloadUrl, use.getMessage());
            return null;
        }
    }

    @Override
    public Image setDownloadUrl(URI downloadUrl) {
        this.downloadUrl = downloadUrl == null ? null : downloadUrl.toString();
        return this;
    }

    @Override
    public String getImageUri() {
        return getUrn();
    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    @Override
    public String getEtag() {
        return etag;
    }

    @Override
    public Image setEtag(String etag) {
        this.etag = etag;
        return this;
    }

    @Override
    public Image setUrlLastModified(Instant lastModified) {
        this.urlLastModified = lastModified;
        return this;

    }

}
