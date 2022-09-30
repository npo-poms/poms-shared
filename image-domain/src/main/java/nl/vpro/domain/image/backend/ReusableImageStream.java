/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.backend;

import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.imageio.ImageIO;
import javax.validation.constraints.Min;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.*;

import nl.vpro.domain.image.UnsupportedImageFormatException;

/**
 * A version of {@link ImageStream} that on first use of {@link #getStream()} will copy the stream to a file, so you
 * call it multiple times.
 *
 *
 * @author Roelof Jan Koekoek
 * @since 1.11
 */
@Slf4j
public class ReusableImageStream extends ImageStream {
    private static final int BUFFER_SIZE = 1024;

    private static final String HASH_ALGORITHM = "SHA1";

    private Path file = null;

    public ReusableImageStream(InputStream stream) {
        this(stream, null);
    }

    public ReusableImageStream(InputStream stream, Instant lastModified) {
        super(stream, lastModified);
    }

    public ReusableImageStream(InputStream stream, long length, Instant lastModified) {
        super(stream, length, lastModified);
    }

    @lombok.Builder
    private ReusableImageStream(
        @NonNull InputStream stream,
        @Min(0) long length,
        @Nullable Instant lastModified,
        @Nullable String contentType,
        @Nullable String etag,
        @Nullable URI url,
        @Nullable Runnable onClose) {
        super(stream, length, lastModified, contentType, etag, url, onClose);
    }

    public ReusableImageStream(ImageStream stream) {
        super(stream.getStream(), stream.getLength(), stream.getLastModified());
    }

    @PolyNull
    public static ReusableImageStream of(@PolyNull ImageStream imageStream) {
        if (imageStream == null) {
            return null;
        } else if (imageStream instanceof  ReusableImageStream) {
            return (ReusableImageStream) imageStream;
        } else {
            return new ReusableImageStream(imageStream);
        }
    }

    @Override
    public InputStream getStream() {
        try {
            return Files.newInputStream(getFile());
        } catch(IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }


    @Override
    public long getLength()  {
        if(length > -1) {
            return length;
        } else {
            try {
                return Files.size(getFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    public void close() throws IOException {
        super.close();
        if (file != null) {
            Files.deleteIfExists(file);
        }
    }

     @Override
     public ReusableImageStream withMetaData(BackendImageMetadata<?> metaData) {
         ReusableImageStream reusableImageStream = ReusableImageStream.builder()
             .stream(stream)
             .url(url)
             .onClose(onClose)
             .length(length)
             .etag(etag == null ? metaData.getEtag() : etag)
             .contentType(metaData.getMimeType())
             .lastModified(lastModified == null ? metaData.getLastModifiedInstant() : lastModified)
             .build();
         reusableImageStream.file = file;
         return reusableImageStream;
     }

    public void copyImageInfoTo(BackendImageMetadata<?> image) {
        final ImageInfo imageInfo = new ImageInfo();
        image.setSize(getLength());
        imageInfo.setInput(getStream());
        if (imageInfo.check()) {
            image.setHeight(imageInfo.getHeight());
            image.setWidth(imageInfo.getWidth());

            final float pH = imageInfo.getPhysicalHeightInch();
            if (pH > 0) {
                image.setHeightInMm(pH * 25.4f);
            }
            final float pW = imageInfo.getPhysicalWidthInch();
            if (pW > 0) {
                image.setWidthInMm(pW * 25.4f);
            }
            if (imageInfo.getMimeType() != null) {
                try {
                    image.setMimeType(
                        imageInfo.getMimeType()
                    );
                } catch (UnsupportedImageFormatException uns) {
                    log.warn(uns.getMessage());
                }
            }
        } else {
            try {
                InputStream stream1 = getStream();
                BufferedImage read = ImageIO.read(getStream());
                if (read != null) {
                    image.setWidth(read.getWidth());
                    image.setHeight(read.getHeight());
                    return;
                }
            } catch (IOException e) {
                log.warn(e.getMessage(), e);
            }
            log.warn("Can not read meta-data from image binary, since imageInfo didn't check");
        }
    }

    public byte[] getHash() throws IOException {
        final InputStream is = getStream();
        MessageDigest m;
        try {
            m = MessageDigest.getInstance(HASH_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            // cannot occur, SHA1 is supported
            throw new IllegalStateException();
        }
        m.reset();
        final byte[] buffer = new byte[BUFFER_SIZE];
        long total = 0L;
        int i = is.read(buffer, 0, BUFFER_SIZE);
        while (i != -1) {
            total += i;
            m.update(buffer, 0, i);
            i = is.read(buffer, 0, BUFFER_SIZE);
        }
        final byte[] result = m.digest();
        log.debug("Found hash based on {} bytes: {}", total, result);
        return result;
    }

    public synchronized void copy()  {
        if(file == null) {
            try {
                file = Files.createTempFile(ImageStream.class.getName(), "tempImage");
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        Files.deleteIfExists(file);
                    } catch (IOException e) {
                        log.warn(e.getMessage(), e);
                    }
                }));
                try (OutputStream out = Files.newOutputStream(file);
                     InputStream s = stream){
                    IOUtils.copy(s, out);
                } finally {
                    stream = null;
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    public Path getFile()  {
        copy();
        return file;
    }
}
