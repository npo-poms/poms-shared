/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.support;

import net.jqwik.api.*;

import java.io.Serial;
import java.time.Instant;

import org.junit.jupiter.api.Assumptions;
import org.meeuw.util.test.BasicObjectTheory;

import static nl.vpro.domain.media.support.Workflow.*;
import static nl.vpro.test.jqwik.HamcrestAssumptions.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class PublishableObjectTest implements BasicObjectTheory<PublishableObject<?>> {

    private static final Instant NOW = Instant.now();


    public static PublishableObject<?> withId1 = data(1L, null, null, null);

    public static PublishableObject<?> withId2 = data(2L, null, null, null);

    public static PublishableObject<?> forPublication = data(2L, FOR_PUBLICATION, null, null);

    public static PublishableObject<?>forRePublication = data(3L, FOR_REPUBLICATION, null, null);

    public static PublishableObject<?>revoked = data(1L, REVOKED, null, null);

    public static PublishableObject<?>published = data(2L, PUBLISHED, null, null);

    public static PublishableObject<?>publishedWithFutureStop = data(2L,
        PUBLISHED,
        null,
        NOW.plusSeconds(100)
    );

    public static PublishableObject<?>forDeletion = data(3L, FOR_DELETION, null, null);

    public static PublishableObject<?>deleted = data(4L, Workflow.DELETED, null, null);

    public static PublishableObject<?>futurePublication = data(5L, FOR_PUBLICATION,
       NOW.plusSeconds(100), null);


    public static PublishableObject<?>revocationFallsForPublication = data(7L,
        PUBLISHED,
        NOW.plusSeconds(100),
        NOW.minusSeconds(100)
    );



    @Property
    public void testIsActivationWhenTrue(@ForAll(DATAPOINTS) PublishableObject<?>data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), anyOf(equalTo(FOR_PUBLICATION), equalTo(REVOKED)));
        assumeThat(data.isPublishable(NOW), is(true));

        assertTrue(data.isActivation(NOW));
    }

    @Property
    public void testIsActivationWhenFalse(@ForAll(DATAPOINTS) PublishableObject<?>data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), not(anyOf(equalTo(FOR_PUBLICATION), equalTo(REVOKED))));
        assertFalse(data.isActivation(NOW));
    }

    @Property(maxDiscardRatio = 1000)
    public void testIsRevocationWhenTrue(@ForAll(DATAPOINTS) PublishableObject<?>data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), equalTo(Workflow.FOR_DELETION));
        assertTrue(data.isDeactivation(NOW));
    }

    @Property(maxDiscardRatio = 1000)
    public void testIsRevocationWhenTrueOnWindow(@ForAll(DATAPOINTS) PublishableObject<?>data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), equalTo(PUBLISHED));
        assumeTrue(!(data.getPublishStartInstant() == null && data.getPublishStopInstant() == null));
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assertTrue(data.isDeactivation(NOW));
    }



    @Property
    public void testIsRevocationWhenFalse(@ForAll(DATAPOINTS) PublishableObject<?>data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), not(anyOf(equalTo(PUBLISHED), equalTo(Workflow.FOR_DELETION))));
        assertFalse(data.isDeactivation(NOW));
    }

    @Property(maxDiscardRatio = 1000)
    public void testIsRevocationWhenFalseOnWindow(@ForAll(DATAPOINTS) PublishableObject<?>data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(), equalTo(PUBLISHED));
        assumeTrue(!(data.getPublishStartInstant() == null && data.getPublishStopInstant() == null));
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assertFalse(data.isDeactivation(NOW));
    }

    @Property
    public void testIsPublishableWhenTrue(@ForAll(DATAPOINTS) PublishableObject<?>data) {
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
        assertTrue(data.isPublishable(NOW));
    }

    @Property
    public void testIsPublishableWhenFalse(@ForAll(DATAPOINTS) PublishableObject<?>data) {
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
        assertFalse(data.isPublishable(NOW));
    }

    @Property(maxDiscardRatio = 1000)
    public void testIsPublishStopBeforeStart(@ForAll(DATAPOINTS) PublishableObject<?>data) {
        assumeNotNull(data);
        assumeNotNull(data.getPublishStartInstant());
        assumeNotNull(data.getPublishStopInstant());
        assumeThat(data.getPublishStartInstant(), greaterThan(data.getPublishStopInstant()));
        assertFalse(data.isPublishable(NOW));
    }

    @Property(maxDiscardRatio = 1000)
    public void testIsRevocableOnWhenDeleted(@ForAll(DATAPOINTS) PublishableObject<?>data) {
        assumeNotNull(data);
        assumeNotNull(data.getWorkflow());
        assumeThat(data.getWorkflow(), equalTo(Workflow.DELETED));
        assertTrue(data.isRevocable(NOW));
    }

    @Property
    public void testIsRevocableOnWhenTrueOnWindow(@ForAll(DATAPOINTS) PublishableObject<?>data) {
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
        assertTrue(data.isRevocable(NOW));
    }

    @Property
    public void testIsNotRevocableWhenFalseOnWindow(@ForAll(DATAPOINTS) PublishableObject<?>data) {
        assumeNotNull(data);
        assumeThat(data.getWorkflow(),
            anyOf(
                equalTo(PUBLISHED),
                equalTo(REVOKED)));
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assertFalse(data.isRevocable(NOW));
    }

    //@Theory I would not know why this is sensible
    // If you create an object 'for publication' with a publish stop in the near future
    // and the work-flow manager happens to hit it for the first time after that time
    // it will consider it a candidate for revocation, but is is 'not revocable'
    // and will never any more come out of this situation.
    // We could also consider making the query for publication hit 'for publication' regardless
    // of the publication window. But that would cause useless publication/revocation only.
    public void testIsNotRevocableWhenNotYetPublished(PublishableObject<?>data) {
        assumeNotNull(data);
        assumeNotNull(data.getWorkflow());
        assumeThat(
            data.getWorkflow().ordinal(),
            not(greaterThan(FOR_PUBLICATION.ordinal())));
        Assumptions.assumeTrue(!(data.getPublishStartInstant() == null && data.getPublishStopInstant() == null));
        assumeThat(data.getPublishStartInstant(), anyOf(equalTo(null), greaterThan(Instant.now())));
        assumeThat(data.getPublishStopInstant(), anyOf(equalTo(null), not(greaterThan(Instant.now()))));
        assertFalse(data.isRevocable(NOW));
    }

    @Override
    public Arbitrary<? extends PublishableObject<?>> datapoints() {
        return Arbitraries.of(
            withId1,
            withId2,
            forPublication,
            forRePublication,
            revoked,
            published,
            publishedWithFutureStop,
            forDeletion,
            deleted,
            futurePublication,
            revocationFallsForPublication);
    }

    private static PublishableObject<?> data(Long id, Workflow workflow, Instant start, Instant stop) {
        PublishableObject<?> result = new MyPublishableObject(id);

        result.setWorkflow(workflow);
        result.setPublishStartInstant(start);
        result.setPublishStopInstant(stop);
        return result;
    }

    private static class MyPublishableObject extends PublishableObject<MyPublishableObject> {
        @Serial
        private static final long serialVersionUID = 5229283134880255886L;

        MyPublishableObject(Long id) {
            super(id);
        }

        @Override
        protected String getUrnPrefix() {
            return null;
        }
    }

}
