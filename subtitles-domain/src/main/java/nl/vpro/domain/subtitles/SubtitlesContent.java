package nl.vpro.domain.subtitles;

import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.*;

/**
 * @author Michiel Meeuwissen
 */
@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "subtitlesContentType")
@Slf4j
public class SubtitlesContent implements Serializable {

    @Column(nullable = false)
    @Lob
    @XmlValue
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @XmlAttribute
    private SubtitlesFormat format = SubtitlesFormat.WEBVTT;


    public SubtitlesContent() {

    }

    public SubtitlesContent(SubtitlesFormat format, String content) {
        this.format = format;
        this.content = content;
    }

    public String getValue() {
        return content;
    }

    public void setValue(String content) {
        this.content = content;
    }

    public SubtitlesFormat getFormat() {
        return format;
    }

    public void setFormat(SubtitlesFormat format) {
        this.format = format;
    }
}
