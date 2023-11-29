package nl.vpro.domain.media;

import java.util.Optional;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * See also nl/vpro/domain/media/vproMedia.xsd . Don't forget to keep that file into sync with this.
 */

@XmlEnum
@XmlType(name = "avFileFormatEnum")
public enum AVFileFormat {


    HASP,  // Html Adaptive Streaming Platform
    H264,  // H264 On NPO streaming platform
    MP4,   // MPEG-4 media
    M4A,   // MPEG-4 audio (please use MP4 instead)
    M4V,   // MPEG-4 video (please use MP4 instead)
    MP3("audio/mpeg"),   // MPEG-3 media
    WVC1,  // Windows Video Codec 9
    WM,    // Windows Media
    WMP,   // Windows Media Player (please use WM instead)
    FLV,   // Flash video
    DGPP { // 3GPP (mobile) media

        @Override
        public String toString() {
            return "3GPP";
        }
    },
    RM,    // Real Media
    RAM,   // Real Audio metafile (please use RM instead)
    RA,    // Real Audio (please use RM instead)
    MPEG2,   // MPEG-2 Highres platform
    HTML,  // HTML embeddable player
    UNKNOWN;

    private final String[] mimeTypes;

    AVFileFormat(String... mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public static Optional<AVFileFormat> forMimeType(String mimetype) {
        if (mimetype == null) {
            return Optional.empty();
        }
        try {

            String[] split = mimetype.split("/", 2);
            return Optional.of(AVFileFormat.valueOf(split[1].toUpperCase()));
        } catch (IllegalArgumentException iae) {
            for (AVFileFormat format : AVFileFormat.values()) {
                for (String mt : format.mimeTypes) {
                    if (mimetype.equalsIgnoreCase(mt)) {
                        return Optional.of(format);
                    }
                }
            }
            return Optional.empty();
        }
    }

    public static AVFileFormat forProgramUrl(final String url) {
        if (url == null) return UNKNOWN;
        final String urlLowerCase = url.toLowerCase();
        if(urlLowerCase.contains("adaptive")) {
            return HASP;
        }
        if(urlLowerCase.contains("h264")) {
            return H264;
        }
        if(urlLowerCase.contains("wmv") || urlLowerCase.contains("wvc1")) {
            return WM;
        }
        if(urlLowerCase.startsWith("http://player.omroep.nl/")) {
            return HTML;
        }
        if(urlLowerCase.endsWith(".asf") ||
            urlLowerCase.endsWith(".wmv") ||
            urlLowerCase.endsWith(".wma") ||
            urlLowerCase.endsWith(".asx")) {
            return WM;
        }
        if(urlLowerCase.endsWith(".m4v") ||
            urlLowerCase.endsWith(".m4a") ||
            urlLowerCase.endsWith(".mov") ||
            urlLowerCase.endsWith(".mp4")
            ) {
            return MP4;
        }
        if(urlLowerCase.endsWith(".ra") ||
            urlLowerCase.endsWith(".rm") ||
            urlLowerCase.endsWith(".ram") ||
            urlLowerCase.endsWith(".smil")) {
            return RM;
        }
        if(urlLowerCase.endsWith(".mp3")) {
            return MP3;
        }
        if(urlLowerCase.endsWith(".3gp") ||
            urlLowerCase.endsWith(".3gpp")) {
            return DGPP;
        }
        if(urlLowerCase.endsWith(".flv") ||
            urlLowerCase.endsWith(".swf") ||
            urlLowerCase.endsWith(".f4v") ||
            urlLowerCase.endsWith(".f4p") ||
            urlLowerCase.endsWith(".f4a") ||
            urlLowerCase.endsWith(".f4b")) {
            return FLV;
        }
        return UNKNOWN;
    }
}
