/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
class AssetLocationTest {

    @Test
    void resolveNonFileScheme() throws Exception {
        AssetLocation target = new AssetLocation("http://host/path/file.name");

        target.resolve("/base/path/");

        assertThat(target.getUrl()).isEqualTo("http://host/path/file.name");
    }

    @Test
    void resolveRelative() throws Exception {
        AssetLocation target = new AssetLocation("file.name");

        target.resolve("/base/path");

        assertThat(target.getUrl()).isEqualTo("file:/base/path/file.name");
    }

    @Test
    void resolveRelativeWithScheme() throws Exception {
        AssetLocation target = new AssetLocation("file:file.name");

        target.resolve("/base/path/");

        assertThat(target.getUrl()).isEqualTo("file:/base/path/file.name");
    }

    @Test
    void resolveAbsolute() throws Exception {
        assertThatThrownBy(() -> {
            AssetLocation target = new AssetLocation("/file.name");

            target.resolve("/base/path/");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void resolveAbsoluteWithScheme() throws Exception {
        assertThatThrownBy(() -> {
            AssetLocation target = new AssetLocation("file:/file.name");
            target.resolve("/base/path/");
        }).isInstanceOf(IllegalArgumentException.class);

    }

    @Test
    void resolveWhenNavigatingUpPath() throws Exception {
        assertThatThrownBy(() -> {

            AssetLocation target = new AssetLocation("../../file.name");

            target.resolve("/base/path/");
        }).isInstanceOf(SecurityException.class);
    }
}
