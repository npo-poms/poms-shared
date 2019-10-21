package nl.vpro.domain.media;

import lombok.*;

import java.io.Serializable;
import java.util.Locale;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.subtitles.SubtitlesWorkflow;
import nl.vpro.xml.bind.LocaleAdapter;


/**
 * This is kind of strange, this table is has only a few fields of Subtitles, and is then in {@link MediaObject} mapped with @CollectionTable.
 */
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

    private SubtitlesWorkflow workflow = null;

    public AvailableSubtitles() {

    }

    public AvailableSubtitles(Locale language, SubtitlesType type) {
        this.language = language;
        this.type = type;
    }

    @lombok.Builder
    private AvailableSubtitles(Locale language, SubtitlesType type, SubtitlesWorkflow workflow) {
        this.language = language;
        this.type = type;
        this.workflow = workflow;

    }

    @XmlAttribute(name = "workflow")
    protected SubtitlesWorkflow getWorkflow_() {
        return workflow == SubtitlesWorkflow.PUBLISHED ? null : workflow;
    }


    protected void setWorkflow_(SubtitlesWorkflow workflow) {
        this.workflow = workflow == null ? SubtitlesWorkflow.PUBLISHED : workflow;
    }
}
