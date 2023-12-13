/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nl.vpro.domain.user.validation.BroadcasterValidator;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author rico
 */
public class BroadcasterValidatorTest {

    BroadcasterService broadcasterService = Mockito.mock(BroadcasterService.class);


    private BroadcasterValidator validator = new BroadcasterValidator();

    @BeforeEach
    public void init() {
        Mockito.when(broadcasterService.findAll()).thenReturn(Arrays.asList(Broadcaster.of("VPRO"), Broadcaster.of("EO")));
        Broadcaster vpro = new Broadcaster("VPRO", "VPRO");
        Mockito.when(broadcasterService.find("VPRO")).thenReturn(vpro);
        validator.initialize(null);

        ServiceLocator.setBroadcasterService(broadcasterService);
    }

    @Test
    public void validBroadcaster() {
        assertThat(validator.isValid(Collections.singletonList("VPRO"), null)).isTrue();
    }

    @Test
    public void invalidBroadcaster() {
        assertThat(validator.isValid(Collections.singletonList("NOTABLE"), null)).isFalse();
    }
}
