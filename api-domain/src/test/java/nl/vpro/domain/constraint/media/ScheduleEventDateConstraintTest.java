package nl.vpro.domain.constraint.media;

import java.time.Instant;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.constraint.Operator;
import nl.vpro.domain.media.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScheduleEventDateConstraintTest {

    @Test
    void apply() {
        ScheduleEventDateConstraint constraint = new ScheduleEventDateConstraint();
        constraint.setDate("tomorrow midnight");
        constraint.setOperator(Operator.LT);
        ScheduleEvent event = new ScheduleEvent();
        event.setStartInstant(Instant.now());
        Program program = MediaBuilder.program().scheduleEvents(event).build();
        assertTrue(constraint.test(program));
    }


    @Test
    void xml() {

        Filter filter = new Filter();
        ScheduleEventDateConstraint constraint = new ScheduleEventDateConstraint();
        constraint.setDate("tomorrow midnight");
        constraint.setOperator(Operator.LT);
        filter.setConstraint(constraint);

        ProfileDefinition<MediaObject> profileDefinition = new ProfileDefinition<>(filter);

        JAXB.marshal(profileDefinition, System.out);

    }

    @Test
    void gTE() {
        Filter filter = new Filter();
        ScheduleEventDateConstraint constraint = new ScheduleEventDateConstraint();
        constraint.setDate("2000-01-01");
        constraint.setOperator(Operator.GT);
        filter.setConstraint(constraint);

        Program program = JAXB.unmarshal(getClass().getResourceAsStream("/VPWON_1267277.xml"), Program.class);

        assertThat(filter.test(program)).isTrue();
    }


}
