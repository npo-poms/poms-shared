/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.image.backend;

import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.PolyNull;

import nl.vpro.domain.image.UnsupportedImageFormatException;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.11
 */
@Slf4j
public class ReusableImageStream extends ImageStream {
    private static final int BUFFER_SIZE = 1024;

    private static final String HASH_ALGORITHM = "SHA1";

    private File file = null;

    public ReusableImageStream(InputStream stream) {
        super(stream);
    }

    public ReusableImageStream(InputStream stream, Instant lastModified) {
        super(stream, lastModified);
    }

    @lombok.Builder
    public ReusableImageStream(InputStream stream, long length, Instant lastModified) {
        super(stream, length, lastModified);
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
            return new FileInputStream(getFile());
        } catch(FileNotFoundException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }


    @Override
    public long getLength()  {
        if(length > -1) {
            return length;
        } else {
            return getFile().length();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if(file != null) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
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
                BufferedImage read = ImageIO.read(getStream());
                image.setWidth(read.getWidth());
                image.setHeight(read.getHeight());
            } catch (IOException e) {
                log.warn("Can not read meta-data from image binary, since imageInfo didn't check");
                log.warn(e.getMessage(), e);
            }
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
                file = File.createTempFile(ImageStream.class.getName(), "tempImage");
                file.deleteOnExit();
                try (FileOutputStream out = new FileOutputStream(file);
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

    public File getFile()  {
        copy();
        return file;
    }
}
