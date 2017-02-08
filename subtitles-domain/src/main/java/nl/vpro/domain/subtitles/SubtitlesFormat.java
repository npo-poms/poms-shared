package nl.vpro.domain.subtitles;

import lombok.Getter;

import java.nio.charset.Charset;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.api.rs.subtitles.Constants;


/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
@XmlEnum
@XmlType(name = "subtitlesFormatEnum")
@Getter
public enum SubtitlesFormat {
    WEBVTT("vtt", Constants.VTT_TYPE, WEBVTTandSRT.VTT_CHARSET),
    EBU("ebu", Constants.EBU_TYPE, nl.vpro.domain.subtitles.EBU.EBU_CHARSET),
    SRT("srt", Constants.SRT_TYPE, WEBVTTandSRT.SRT_CHARSET);


    private final String extension;
    private final MediaType mediaType;
    private final Charset charset;

    SubtitlesFormat(String extension, MediaType mediaType, Charset charset) {
        this.extension = extension;
        this.mediaType = mediaType;
        this.charset = charset;
    }
}
