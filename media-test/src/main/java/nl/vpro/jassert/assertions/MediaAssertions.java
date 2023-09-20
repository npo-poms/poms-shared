/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.jassert.assertions;

import org.assertj.core.api.Assertions;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class MediaAssertions extends Assertions {

    public static MediaAssert mediaAssertThat(MediaObject  actual) {
        return new MediaAssert(actual);
    }

    public static MediaAssert assertThat(MediaObject  actual) {
        return mediaAssertThat(actual);
    }


    public static LocationAssert locationAssertThat(Location actual) {
        return new LocationAssert(actual);
    }
}
