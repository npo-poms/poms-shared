package nl.vpro.domain.media.support;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.AbstractOwnedTextEntity;
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
@XmlType(name = "scheduleEventTitle", namespace = Xmlns.MEDIA_NAMESPACE)
@JsonPropertyOrder({"value", "owner", "type"})
public class ScheduleEventTitle extends AbstractOwnedTextEntity<ScheduleEventTitle, ScheduleEvent> {

    @Serial
    private static final long serialVersionUID = -445917594010977511L;

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
    public ScheduleEventTitle(ScheduleEvent parent, String title, OwnerType owner, TextualType type) {
        super(title, owner, type);
        this.parent = parent;
    }

    public ScheduleEventTitle() {
    }

    @Override
    public boolean mayContainNewLines() {
        return false;
    }
}
