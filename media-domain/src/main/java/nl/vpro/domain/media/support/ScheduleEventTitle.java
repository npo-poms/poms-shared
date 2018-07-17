package nl.vpro.domain.media.support;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
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

    public ScheduleEventTitle(ScheduleEvent parent, String title, OwnerType owner, TextualType type) {
        super(parent, title, owner, type);
    }

    public ScheduleEventTitle() {
    }

    @Override
    public boolean mayContainNewLines() {
        return false;
    }
}
