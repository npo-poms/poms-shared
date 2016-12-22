package nl.vpro.domain.constraint.media;

import java.util.Date;

import javax.xml.bind.JAXB;

import org.junit.Test;

import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.constraint.Operator;
import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.ScheduleEvent;

import static org.junit.Assert.assertTrue;

public class ScheduleEventDateConstraintTest {

    @Test
    public void testApply() throws Exception {
        ScheduleEventDateConstraint constraint = new ScheduleEventDateConstraint();
        constraint.setDate("tomorrow midnight");
        constraint.setOperator(Operator.LT);
        ScheduleEvent event = new ScheduleEvent();
        event.setStart(new Date());
        Program program = MediaBuilder.program().scheduleEvents(event).build();
        assertTrue(constraint.test(program));
    }


    @Test
    public void testXml() {

        Filter filter = new Filter();
        ScheduleEventDateConstraint constraint = new ScheduleEventDateConstraint();
        constraint.setDate("tomorrow midnight");
        constraint.setOperator(Operator.LT);
        filter.setConstraint(constraint);

        ProfileDefinition<MediaObject> profileDefinition = new ProfileDefinition<>(filter);

        JAXB.marshal(profileDefinition, System.out);

    }

}
