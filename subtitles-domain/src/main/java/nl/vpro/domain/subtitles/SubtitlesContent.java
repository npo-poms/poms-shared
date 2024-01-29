package nl.vpro.domain.subtitles;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

/**
 * The subtitles as a String (to parse this use {@link SubtitlesUtil#parse(nl.vpro.domain.subtitles.Subtitles, boolean)}
 * @author Michiel Meeuwissen
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subtitlesContentType")
@Slf4j
@Data
public class SubtitlesContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 4374514441026800570L;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    private SubtitlesFormat format = SubtitlesFormat.WEBVTT;


    @Column(nullable = false)
    @XmlValue
    private byte[] value;

    @Column
    @XmlAttribute
    private String charset;

    public SubtitlesContent() {
    }

    public SubtitlesContent(SubtitlesFormat format, String content) {
        this.format = format;
        this.value = content.getBytes(StandardCharsets.UTF_8);
        this.charset = "UTF-8";
    }


    @lombok.Builder
    private SubtitlesContent(SubtitlesFormat format, byte[] content, Charset charset) {
        this.format = format;
        this.value = content;
        this.charset = charset == null ? format.getCharset().toString() : charset.name();
    }

    public InputStream asStream() {
        return new ByteArrayInputStream(value == null ? new byte[0] : value);
    }

}
