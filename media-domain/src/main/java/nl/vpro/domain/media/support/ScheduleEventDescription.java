package nl.vpro.domain.media.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.AbstractDescriptionEntity;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.ScheduleEvent;

/**
 * See https://jira.vpro.nl/browse/MSE-3524
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
@Cacheable
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventDescription", namespace = Xmlns.MEDIA_NAMESPACE)
@JsonPropertyOrder({"value", "owner", "type"})
public class ScheduleEventDescription extends AbstractDescriptionEntity<ScheduleEventDescription, ScheduleEvent> {

    @Serial
    private static final long serialVersionUID = -5439283140517618047L;

    @Getter
    @Setter
    @ManyToOne
    @NotNull
    @JoinColumns({
       @JoinColumn(name = "parent_channel", referencedColumnName = "channel"),
       @JoinColumn(name = "parent_start", referencedColumnName = "start")

    })
    ScheduleEvent parent;

    @lombok.Builder
    public ScheduleEventDescription(ScheduleEvent parent, String title, OwnerType owner, TextualType type) {
        super(title, owner, type);
        this.parent = parent;
    }

    public ScheduleEventDescription() {
    }



}
