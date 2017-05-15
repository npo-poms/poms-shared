package nl.vpro.domain.subtitles;

import lombok.Getter;

import java.nio.charset.Charset;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.api.rs.subtitles.Constants;
import nl.vpro.util.ISO6937CharsetProvider;


/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@XmlEnum
@XmlType(name = "subtitlesFormatEnum")
public enum SubtitlesFormat {
    WEBVTT("vtt", Constants.VTT, Charset.forName("UTF-8")),
    EBUTXT("txt", Constants.EBUTXT, ISO6937CharsetProvider.ISO6937),
    //EBU("stl", Constants.EBUTXT, ISO6937CharsetProvider.ISO6937),
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
}
