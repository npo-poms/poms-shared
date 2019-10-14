/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import java.time.Instant;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theory;

import nl.vpro.test.theory.ObjectTest;

import static nl.vpro.domain.media.support.Workflow.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.*;

/**
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class PublishableObjectTest extends ObjectTest<PublishableObject> {

    @DataPoint
    public static PublishableObject nullArgument = null;

    @DataPoint
    public static PublishableObject withId1 = data(1L, null, null, null);

    @DataPoint
    public static PublishableObject withId2 = data(2L, null, null, null);

    @DataPoint
    public static PublishableObject forPublication = data(2L, FOR_PUBLICATION, null, null);

    @DataPoint
    public static PublishableObject forRePublication = data(3L, FOR_REPUBLICATION, null, null);

    @DataPoint
    public static PublishableObject revoked = data(1L, REVOKED, null, null);

    @DataPoint
    public static PublishableObject published = data(2L, PUBLISHED, null, null);

    @DataPoint
    public static PublishableObject publishedWithFutureStop = data(2L,
        PUBLISHED,
        null,
        Instant.now().plusSeconds(100)
    );

    @DataPoint
    public static PublishableObject forDeletion = data(3L, Workflow.FOR_DELETION, null, null);

    @DataPoint
    public static PublishableObject deleted = data(4L, Workflow.DELETED, null, null);

    @DataPoint
    public static PublishableObject futurePublication = data(5L, FOR_PUBLICATION,
        Instant.now().plusSeconds(100), null);

    @DataPoint
    public static PublishableObject revocationFallsForPublication = data(7L,
        PUBLISHED,
        Instant.now().plusSeconds(100),
        Instant.now().minusSeconds(100)
    );

    @Theory
    public void testIsActivationWhenTrue(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), anyOf(equalTo(FOR_PUBLICATION), equalTo(REVOKED)));
        assumeThat(data.isPublishable(), is(true));
        assertTrue(data.isActivation());
    }

    @Theory
    public void testIsActivationWhenFalse(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), not(anyOf(equalTo(FOR_PUBLICATION), equalTo(REVOKED))));
        assertFalse(data.isActivation());
    }

    @Theory
    public void testIsRevocationWhenTrue(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), equalTo(Workflow.FOR_DELETION));
        assertTrue(data.isDeactivation());
    }

    @Theory
    public void testIsRevocationWhenTrueOnWindow(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), equalTo(PUBLISHED));
        assumeTrue(!(data.getPublishStartInstant() == null && data.getPublishStopInstant() == null));
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assertTrue(data.isDeactivation());
    }

    @Theory
    public void testIsRevocationWhenFalse(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), not(anyOf(equalTo(PUBLISHED), equalTo(Workflow.FOR_DELETION))));
        assertFalse(data.isDeactivation());
    }

    @Theory
    public void testIsRevocationWhenFalseOnWindow(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), equalTo(PUBLISHED));
        assumeTrue(!(data.getPublishStartInstant() == null && data.getPublishStopInstant() == null));
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assertFalse(data.isDeactivation());
    }

    @Theory
    public void testIsPublishableWhenTrue(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(),
            anyOf(
                equalTo(FOR_PUBLICATION),
                equalTo(FOR_REPUBLICATION),
                equalTo(PUBLISHED),
                equalTo(REVOKED),
                nullValue()
            )
        );
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assertTrue(data.isPublishable());
    }

    @Theory
    public void testIsPublishableWhenFalse(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(),
            not(anyOf(
                equalTo(FOR_PUBLICATION),
                equalTo(FOR_REPUBLICATION),
                equalTo(PUBLISHED),
                equalTo(REVOKED),
                nullValue()
                )
            )
        );
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assertFalse(data.isPublishable());
    }

    @Theory
    public void testIsPublishStopBeforeStart(PublishableObject data) {
        assumeNotNull(data);
        assumeNotNull(data.getPublishStartInstant());
        assumeNotNull(data.getPublishStopInstant());
        assumeThat(data.getPublishStartInstant(), greaterThan(data.getPublishStopInstant()));
        assertFalse(data.isPublishable());
    }

    @Theory
    public void testIsRevocableOnWhenDeleted(PublishableObject data) {
        assumeNotNull(data);
        assumeNotNull(data.getWorkflow());
        assumeThat(data.getWorkflow(), equalTo(Workflow.DELETED));
        assertTrue(data.isRevocable());
    }

    @Theory
    public void testIsRevocableOnWhenTrueOnWindow(PublishableObject data) {
        assumeNotNull(data);
        assumeNotNull(data.getWorkflow());
        assumeThat(data.getWorkflow(),
            anyOf(
                equalTo(PUBLISHED),
                equalTo(FOR_REPUBLICATION),
                equalTo(FOR_PUBLICATION),
                equalTo(REVOKED)));
        assumeTrue(!(data.getPublishStartInstant() == null && data.getPublishStopInstant() == null));
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assertTrue(data.isRevocable());
    }

    @Theory
    public void testIsNotRevocableWhenFalseOnWindow(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(),
            anyOf(
                equalTo(PUBLISHED),
                equalTo(REVOKED)));
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assertFalse(data.isRevocable());
    }

    //@Theory I would not know why this is sensible
    // If you create an object 'for publication' with a publish stop in the near future
    // and the work-flow manager happens to hit it for the first time after that time
    // it will consider it a candidate for revocation, but is is 'not revocable'
    // and will never any more come out of this situation.
    // We could also consider making the query for publication hit 'for publication' regardless
    // of the publication window. But that would cause useless publication/revocation only.
    public void testIsNotRevocableWhenNotYetPublished(PublishableObject data) {
        assumeNotNull(data);
        assumeNotNull(data.getWorkflow());
        assumeThat(
            data.getWorkflow().ordinal(),
            not(greaterThan(FOR_PUBLICATION.ordinal())));
        assumeTrue(!(data.getPublishStartInstant() == null && data.getPublishStopInstant() == null));
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assertFalse(data.isRevocable());
    }

    private static PublishableObject data(Long id, Workflow workflow, Instant start, Instant stop) {
        PublishableObject result = new PublishableObject(id) {
            @Override
            protected String getUrnPrefix() {
                return null;
            }
        };
        result.setWorkflow(workflow);
        result.setPublishStartInstant(start);
        result.setPublishStopInstant(stop);
        return result;
    }
}
