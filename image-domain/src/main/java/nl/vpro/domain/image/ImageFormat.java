package nl.vpro.domain.image;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

/**
 * Recognized image formats.
 */
@Slf4j
public enum ImageFormat {

    BMP("image/bmp", "bmp"),
    GIF("image/gif", true, "gif"),
    IEF("image/ief", "ief"),
    IFF("image/iff", "iff"),
    JPG("image/jpeg", "J", false, "jpe", "jpeg", "jpg"),
    JFIF("image/pipeg", "jfif"),
    PNG("image/png", "png"),
    PBM("image/x-portable-bitmap", "pbm"),
    PGM("image/x-portable-graymap", "pgm"),
    PNM("image/x-portable-anymap", "pnm"),
    PPM("image/x-portable-pixmap", "ppm"),
    SVG("image/svg+xml", "svg"), // Expensive!
    RAS("image/x-cmu-raster", "ras"),
    RGB("image/x-rgb", "rgb"),
    TIF("image/tiff", "tif", "tiff"),
    XBM("image/x-xbitmap", "xbm"),
    XPM("image/x-xpixmap", "xpm"),
    WEBP("image/webp",  "W", false, "webp"),
    /**
     * The image format could not be determined. May be the image is corrupt?
     * @since 8.9
     *
     */
    UNKNOWN("application/octet-stream", "image");
    ;


    public static final ImageFormat AS_IS = null;

    static final Map<String, String> MAPPING = new HashMap<>();
    static {
        MAPPING.put("image/pjpeg", "image/jpeg");
        MAPPING.put("image/jpg", "image/jpeg"); // found in the logs: May  5 22:29:22 judy07 1 2021-05-05T22:29:22+02:00 bogo03 /e/as/poms7a - - - [prod] - imagebackend - WARN  -  - For https://radio-images.npo.nl/%7Bformat%7D/c558d163-3329-4fc4-91e4-83b1a9e2bf1d/7aed65cc-d12d-4a89-91b7-8f701a534d0c.jpg image/jpg: No matching type for mime-type: image/jpg  [ nl.vpro.domain.image.ImageDownloaders - pool-4-thread-1 ]
        // it's actually wrong, but never mind

    }

    @Getter
    private final String mimeType;

    private final String[] extensions;

    private final boolean supportsAnimation;

    private final String shortName;

    ImageFormat(String mimeType, String... extensions) {
        this(mimeType, null, false, extensions);
    }
    ImageFormat(String mimeType, boolean supportsAnimation, String... extensions) {
        this(mimeType, null, supportsAnimation, extensions);
    }

    ImageFormat(String mimeType, String shortName, boolean supportsAnimation, String... extensions) {
        this.mimeType = mimeType;
        this.extensions = extensions;
        this.shortName = shortName;
        this.supportsAnimation = supportsAnimation;
    }

    @NonNull
    public String getShortName() {
        return shortName == null ? name() : shortName;
    }

    @NonNull
    public static Optional<ImageFormat> forFileExtension(String extension) throws UnsupportedImageFormatException {
        if (StringUtils.isEmpty(extension)) {
            return Optional.empty();
        }
        for(ImageFormat type : ImageFormat.values()) {
            for(String match : type.extensions) {
                if(match.equals(extension.toLowerCase().trim())) {
                    return Optional.of(type);
                }
            }
        }

        throw new UnsupportedImageFormatException ("No matching type for file extension: " + extension);
    }

    public static ImageFormat forMimeType(final String mimeType) throws UnsupportedImageFormatException {
        if (mimeType == null) {
            return null;
        }
        String cleaned = mimeType.trim().toLowerCase();
        String mapped = MAPPING.getOrDefault(cleaned, cleaned);
        for(ImageFormat type : ImageFormat.values()) {
            if(type.getMimeType().equals(mapped) || type.getMimeType().equals(cleaned)) {
                return type;
            }
        }

        throw new UnsupportedImageFormatException("No matching type for mime-type: '" + mimeType + "'");
    }

    public String getFileExtension() {
        return name().toLowerCase();
    }

    @PolyNull
    public static String getFileExtension(@PolyNull ImageFormat imageFormat) {
        return imageFormat == null ? null : imageFormat.getFileExtension();
    }

    public boolean supportsAnimation() {
        return supportsAnimation;
    }
}
