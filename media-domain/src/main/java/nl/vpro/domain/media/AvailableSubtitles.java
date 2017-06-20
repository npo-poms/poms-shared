package nl.vpro.domain.media;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.xml.bind.LocaleAdapter;

@Embeddable
@Cacheable
@XmlType(name="availableSubtitlesType")
@XmlAccessorType(XmlAccessType.NONE)
@EqualsAndHashCode
@ToString
@Data
public class AvailableSubtitles implements Serializable {

    private static final long serialVersionUID = 0L;

    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @XmlAttribute
    private Locale language;

    @Enumerated(EnumType.STRING)
    @XmlAttribute
    private SubtitlesType type;

    public AvailableSubtitles() {

    }

    @lombok.Builder
    public AvailableSubtitles(Locale language, SubtitlesType type) {
        this.language = language;
        this.type = type;

    }
}
