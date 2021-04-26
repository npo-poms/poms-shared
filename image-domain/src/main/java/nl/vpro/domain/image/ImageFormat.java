package nl.vpro.domain.image;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

@Slf4j
public enum ImageFormat {

    BMP("image/bmp", "bmp"),
    GIF("image/gif", true, "gif"),
    IEF("image/ief", "ief"),
    IFF("image/iff", "iff"),
    JPG("image/jpeg", "jpe", "jpeg", "jpg"),
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
    WEBP("image/webp", "webp");


    static final Map<String, String> MAPPING = new HashMap<>();
    static {
        MAPPING.put("image/pjpeg", "image/jpeg");
    }

    @Getter
    private final String mimeType;

    private final String[] extensions;

    private final boolean supportsAnimation;

    ImageFormat(String mimeType, String... extensions) {
        this(mimeType, false, extensions);
    }

    ImageFormat(String mimeType, boolean supportsAnimation, String... extensions) {
        this.mimeType = mimeType;
        this.extensions = extensions;
        this.supportsAnimation = supportsAnimation;
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

        throw new UnsupportedImageFormatException("No matching type for mime-type: " + mimeType);
    }

    public String getFileExtension() {
        return name().toLowerCase();
    }

    public boolean supportsAnimation() {
        return supportsAnimation;
    }
}
