/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.backend;

import lombok.*;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

/**
 * An image stream represents the actual blob data for an image.
 * <p>
 * This contains an {@link InputStream} plus some information that may be provided by the backing implementation like
 * {@link #getLength()} and  {@link #getEtag()}
 * <p>
 * The {@link ReusableImageStream} specialization will allow for repeated calls on {@link #getStream()} resulting a stream that can be used over and over again.
 * </p>
 *
 */
@Data
public class ImageStream implements AutoCloseable {

    @Setter(AccessLevel.NONE)
    protected InputStream stream;

    final protected long length;

    final protected Instant lastModified;

    protected String contentType;

    protected String etag;

    protected URI url;

    protected Runnable onClose;

    public ImageStream(InputStream stream, long length, Instant lastModified) {
        this(stream, length, lastModified, null,  null, null, null);
    }

    public ImageStream(InputStream stream, Instant lastModified) {
        this(stream, -1, lastModified);
    }

    public ImageStream(InputStream stream) {
        this(stream, null);
    }

    public ImageStream(File file) throws IOException {
        this(file.toPath());
    }

    public ImageStream(Path file) throws IOException {
        this(Files.newInputStream(file), Files.size(file), Files.getLastModifiedTime(file).toInstant());
    }

    public static ImageStream of(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        return new ImageStream(connection.getInputStream(), connection.getContentLength(), Instant.ofEpochMilli(connection.getLastModified()));
    }

    public static ReusableImageStream of(byte[] bytes) throws IOException {
        return new ReusableImageStream(new ByteArrayInputStream(bytes), bytes.length, null);
    }


    @lombok.Builder(builderMethodName = "imageStreamBuilder")
    private ImageStream(InputStream stream, long length, Instant lastModified, String contentType, String etag, URI url, Runnable onClose) {
        this.stream = stream;
        this.length = length;
        this.lastModified = lastModified;
        this.contentType = contentType;
        this.etag = etag;
        this.url = url;
        this.onClose = onClose;
    }

    @Override
    public void close() throws IOException {
        if (stream != null) {
            stream.close();
        }
        if (onClose != null) {
            onClose.run();
        }
    }

    public ImageStream withMetaData(BackendImageMetadata<?> metaData) {

        return ImageStream.imageStreamBuilder()
            .stream(stream)
            .url(url)
            .onClose(onClose)
            .length(length)
            .etag(etag == null ? metaData.getEtag() : etag)
            .contentType(metaData.getMimeType())
            .lastModified(lastModified == null ? metaData.getLastModifiedInstant() : lastModified)
            .build();
    }
}
