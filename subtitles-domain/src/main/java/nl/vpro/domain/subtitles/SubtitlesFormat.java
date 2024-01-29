package nl.vpro.domain.subtitles;

import lombok.Getter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.api.rs.subtitles.Constants;
import nl.vpro.util.ISO6937CharsetProvider;


/**
 * The recognized (by POMS) subtitles formats.
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@XmlEnum
@XmlType(name = "subtitlesFormatEnum")
public enum SubtitlesFormat {

    /**
     * <a href="https://www.w3.org/TR/webvtt1/">WEBVTT</a>
     */
    WEBVTT("vtt", Constants.VTT, StandardCharsets.UTF_8),
    TT888("txt", Constants.TT888, ISO6937CharsetProvider.ISO6937),
    EBU("stl", Constants.EBU, null),
    SRT("srt", Constants.SRT, Charset.forName("cp1252"))
    ;

    @Getter
    private final String extension;
    @Getter
    private final String mediaType;
    @Getter
    private final Charset charset;

    SubtitlesFormat(String extension, String mediaType, Charset charset) {
        this.extension = extension;
        this.mediaType = mediaType;
        this.charset = charset;
    }

    public static Optional<SubtitlesFormat> ofExtension(String extension) {
        for (SubtitlesFormat sf : values()) {
            if (sf.extension.equalsIgnoreCase(extension)) {
                return Optional.of(sf);
            }
        }
        return Optional.empty();
    }
}
