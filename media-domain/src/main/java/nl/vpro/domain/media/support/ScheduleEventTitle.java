package nl.vpro.domain.media.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.AbstractTitleEntity;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.ScheduleEvent;

/**
 * See <a href="https://jira.vpro.nl/browse/MSE-3524">MSE-3524</a>
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Setter
@Entity
@Cacheable
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "scheduleEventTitle", namespace = Xmlns.MEDIA_NAMESPACE)
@JsonPropertyOrder({"value", "owner", "type"})
public class ScheduleEventTitle extends AbstractTitleEntity<ScheduleEventTitle, ScheduleEvent> {

    @Serial
    private static final long serialVersionUID = -445917594010977511L;

    @Getter
    @ManyToOne
    @NotNull
    @JoinColumns({
       @JoinColumn(name = "parent_channel", referencedColumnName = "channel"),
       @JoinColumn(name = "parent_start", referencedColumnName = "start")

    })
    ScheduleEvent parent;




    @lombok.Builder
    public ScheduleEventTitle(ScheduleEvent parent, String title, OwnerType owner, TextualType type) {
        super(title, owner, type);
        this.parent = parent;
    }

    public ScheduleEventTitle() {
    }

}
