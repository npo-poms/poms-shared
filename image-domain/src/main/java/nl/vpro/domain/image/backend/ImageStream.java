/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.backend;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

import javax.validation.constraints.Min;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.meeuw.functional.ThrowingRunnable;

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
@Slf4j
@Data
public class ImageStream implements AutoCloseable {

    @Setter(AccessLevel.NONE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    protected InputStream stream;

    final protected long length;

    final protected Instant lastModified;

    protected String contentType;

    protected String etag;

    protected URI url;

    protected ThrowingRunnable<IOException> onClose;

    protected boolean closed;

    protected ImageStream(@NonNull InputStream stream, Instant lastModified) {
        this.stream = stream;
        this.lastModified = lastModified;
        this.length = -1;
    }

    public ImageStream(@NonNull InputStream stream, long length, Instant lastModified) {
        this(stream, length, lastModified, null,  null, null, null);
    }

    @SneakyThrows
    public static ImageStream of(InputStream stream, Instant lastModified) {
        return new ReusableImageStream(stream, lastModified);
    }

    public static ImageStream of(InputStream stream) {
        return of(stream, null);
    }

    public static ImageStream of(File file) throws IOException {
        return of(file.toPath());
    }

    public static ImageStream of(final Path file) throws IOException {
        if (Files.exists(file)) {
            return new ImageStream(
                Files.newInputStream(file),
                Files.size(file),
                Files.getLastModifiedTime(file).toInstant()
            );
        } else {
            return of(new byte[0]);
        }
    }

    public static ImageStream of(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        return new ImageStream(
            connection.getInputStream(),
            connection.getContentLength(),
            Instant.ofEpochMilli(connection.getLastModified())
        );
    }

    public static ReusableImageStream of(byte[] bytes) throws IOException {
        return new ReusableImageStream(new ByteArrayInputStream(bytes), bytes.length, null);
    }


    @lombok.Builder(builderMethodName = "imageStreamBuilder")
    protected ImageStream(
        @Nullable InputStream stream,
        @Min(0) long length,
        @Nullable Instant lastModified,
        @Nullable String contentType,
        @Nullable String etag,
        @Nullable URI url,
        @Nullable ThrowingRunnable<IOException> onClose) {
        this.stream = stream;
        this.length = length;
        this.lastModified = lastModified;
        this.contentType = contentType;
        this.etag = etag;
        this.url = url;
        this.onClose = onClose;
    }

    @SneakyThrows
    @Override
    public void close() throws IOException {
        closed = true;
        log.debug("Closing {} ", this);
        IOException ioException = null;
        if (onClose != null) {
            try {
                onClose.runThrows();
            } catch (IOException ioe) {
                ioException = ioe;
            }
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException ioe) {
                ioException = ioe;
            }
        }
        if (ioException != null) {
            throw ioException;
        }

    }

    /**
     * @throws IOException if the stream could not be produces because closed.
     */
    public InputStream getStream() throws IOException {
        if (closed) {
            throw new IOException("Stream closed");
        }
        return stream;
    }

    public ImageStream withMetaData(BackendImageMetadata<?> metaData) throws IOException {

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
