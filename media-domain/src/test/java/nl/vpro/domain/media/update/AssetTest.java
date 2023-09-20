/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
public class AssetTest {

    private Asset target = new Asset();

    @Test
    public void testResolve() throws Exception {
        target.setSource(new AssetLocation("file.name"));

        target.resolve("/absolute/path/");

        assertThat(((AssetLocation)target.getSource()).getUrl()).isEqualTo("file:/absolute/path/file.name");
    }
}
