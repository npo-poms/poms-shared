/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import java.util.Date;

import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theory;

import nl.vpro.theory.ObjectTest;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNotNull;
import static org.junit.Assume.assumeThat;
import static org.junit.Assume.assumeTrue;

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
    public static PublishableObject draft = data(1L, Workflow.DRAFT, null, null);

    @DataPoint
    public static PublishableObject forPublication = data(2L, Workflow.FOR_PUBLICATION, null, null);

    @DataPoint
    public static PublishableObject forRePublication = data(3L, Workflow.FOR_REPUBLICATION, null, null);

    @DataPoint
    public static PublishableObject revoked = data(1L, Workflow.REVOKED, null, null);

    @DataPoint
    public static PublishableObject published = data(2L, Workflow.PUBLISHED, null, null);

    @DataPoint
    public static PublishableObject publishedWithFutureStop = data(2L, Workflow.PUBLISHED, null, new Date(System.currentTimeMillis() + 100000));

    @DataPoint
    public static PublishableObject forDeletion = data(3L, Workflow.FOR_DELETION, null, null);

    @DataPoint
    public static PublishableObject deleted = data(4L, Workflow.DELETED, null, null);

    @DataPoint
    public static PublishableObject futurePublication = data(5L, Workflow.FOR_PUBLICATION, new Date(System.currentTimeMillis() + 100000), null);

    @DataPoint
    public static PublishableObject revocationFallsForPublication = data(7L, Workflow.PUBLISHED, new Date(System.currentTimeMillis() + 100000), new Date(System.currentTimeMillis() - 100000));

    @Theory
    public void testIsActivationWhenTrue(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), anyOf(equalTo(Workflow.FOR_PUBLICATION), equalTo(Workflow.REVOKED)));
        assumeThat(data.isPublishable(), is(true));
        assertTrue(data.isActivation());
    }

    @Theory
    public void testIsActivationWhenFalse(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), not(anyOf(equalTo(Workflow.FOR_PUBLICATION), equalTo(Workflow.REVOKED))));
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
        assumeThat(data.getWorkflow(), equalTo(Workflow.PUBLISHED));
        assumeTrue(!(data.getPublishStart() == null && data.getPublishStop() == null));
        assumeThat(data.getPublishStart(), anyOf(equalTo(null), greaterThan(new Date())));
        assumeThat(data.getPublishStop(), anyOf(equalTo(null), not(greaterThan(new Date()))));
        assertTrue(data.isDeactivation());
    }

    @Theory
    public void testIsRevocationWhenFalse(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), not(anyOf(equalTo(Workflow.PUBLISHED), equalTo(Workflow.FOR_DELETION))));
        assertFalse(data.isDeactivation());
    }

    @Theory
    public void testIsRevocationWhenFalseOnWindow(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), equalTo(Workflow.PUBLISHED));
        assumeTrue(!(data.getPublishStart() == null && data.getPublishStop() == null));
        assumeThat(data.getPublishStart(), anyOf(equalTo(null), not(greaterThan(new Date()))));
        assumeThat(data.getPublishStop(), anyOf(equalTo(null), greaterThan(new Date())));
        assertFalse(data.isDeactivation());
    }

    @Theory
    public void testIsPublishableWhenTrue(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), anyOf(equalTo(Workflow.FOR_PUBLICATION), equalTo(Workflow.FOR_REPUBLICATION), equalTo(Workflow.PUBLISHED), equalTo(Workflow.REVOKED)));
        assumeThat(data.getPublishStart(), anyOf(equalTo(null), not(greaterThan(new Date()))));
        assumeThat(data.getPublishStop(), anyOf(equalTo(null), greaterThan(new Date())));
        assertTrue(data.isPublishable());
    }

    @Theory
    public void testIsPublishableWhenFalse(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), not(anyOf(equalTo(Workflow.FOR_PUBLICATION), equalTo(Workflow.FOR_REPUBLICATION), equalTo(Workflow.PUBLISHED), equalTo(Workflow.REVOKED))));
        assumeThat(data.getPublishStart(), anyOf(equalTo(null), greaterThan(new Date())));
        assumeThat(data.getPublishStop(), anyOf(equalTo(null), not(greaterThan(new Date()))));
        assertFalse(data.isPublishable());
    }

    @Theory
    public void testIsPublishStopBeforeStart(PublishableObject data) {
        assumeNotNull(data);
        assumeNotNull(data.getPublishStart());
        assumeNotNull(data.getPublishStop());
        assumeThat(data.getPublishStart(), greaterThan(data.getPublishStop()));
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
                equalTo(Workflow.PUBLISHED),
                equalTo(Workflow.FOR_REPUBLICATION),
                equalTo(Workflow.FOR_PUBLICATION),
                equalTo(Workflow.REVOKED)));
        assumeTrue(!(data.getPublishStart() == null && data.getPublishStop() == null));
        assumeThat(data.getPublishStart(), anyOf(equalTo(null), greaterThan(new Date())));
        assumeThat(data.getPublishStop(), anyOf(equalTo(null), not(greaterThan(new Date()))));
        assertTrue(data.isRevocable());
    }

    @Theory
    public void testIsNotRevocableWhenFalseOnWindow(PublishableObject data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(),
            anyOf(
                equalTo(Workflow.PUBLISHED),
                equalTo(Workflow.REVOKED)));
        assumeThat(data.getPublishStart(), anyOf(equalTo(null), not(greaterThan(new Date()))));
        assumeThat(data.getPublishStop(), anyOf(equalTo(null), greaterThan(new Date())));
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
            not(greaterThan(Workflow.FOR_PUBLICATION.ordinal())));
        assumeTrue(!(data.getPublishStart() == null && data.getPublishStop() == null));
        assumeThat(data.getPublishStart(), anyOf(equalTo(null), greaterThan(new Date())));
        assumeThat(data.getPublishStop(), anyOf(equalTo(null), not(greaterThan(new Date()))));
        assertFalse(data.isRevocable());
    }

    private static PublishableObject data(Long id, Workflow workflow, Date start, Date stop) {
        PublishableObject result = new PublishableObject(id) {
            @Override
            protected String getUrnPrefix() {
                return null;
            }
        };
        result.setWorkflow(workflow);
        result.setPublishStart(start);
        result.setPublishStop(stop);
        return result;
    }
}
