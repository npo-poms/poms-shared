/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.jassert.assertions;

import java.time.Instant;
import java.util.Date;

import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.Fail;

import nl.vpro.domain.media.support.PublishableObject;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.util.DateUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public abstract class PublishableObjectAssert<S extends PublishableObjectAssert<S, A>, A extends PublishableObject<A>> extends AbstractObjectAssert<S, A> {

    public PublishableObjectAssert(A actual, Class<?> selfType) {
        super(actual, selfType);
    }

    public S hasWorkflow(Workflow workflow) {
        isNotNull();
        assertThat(actual.getWorkflow()).isEqualTo(workflow);
        return myself;
    }

    public S hasPublishStart(Date start) {
        isNotNull();
        assertThat(actual.getPublishStartInstant()).isEqualTo(DateUtils.toInstant(start));
        return myself;
    }

    public S hasPublishStop(Date stop) {
        isNotNull();
        assertThat(actual.getPublishStopInstant()).isEqualTo(DateUtils.toInstant(stop));
        return myself;
    }

    public S hasPublishStart(Instant start) {
        isNotNull();
        assertThat(actual.getPublishStartInstant()).isEqualTo(start);
        return myself;
    }

    public S hasPublishStop(Instant stop) {
        isNotNull();
        assertThat(actual.getPublishStopInstant()).isEqualTo(stop);
        return myself;
    }

    public S hasPublicationWindow() {
        isNotNull();
        if(actual.getPublishStartInstant() != null || actual.getPublishStopInstant() != null) {
            return myself;
        }
        Fail.fail("expected at least one of publishStart or publishStop not too be null");
        return null;
    }
}
