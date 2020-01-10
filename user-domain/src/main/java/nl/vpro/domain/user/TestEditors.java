/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.time.Instant;

/**
 * @author Roelof Jan Koekoek
 * @since 2.3
 * @deprecated I think this is too flimsy to warrant a complete utility class like this.
 *
 */
@Deprecated
public class TestEditors {

    /**
     * @deprecated Use e.g. {@link nl.vpro.domain.media.MediaTestDataBuilder#vproEditor}
     */
    @Deprecated
    public static Editor vproEditor() {
        return new Editor("editor@vpro.nl", "Editor", "editor@vpro.nl", new Broadcaster("VPRO", "VPRO"), "Test", "Editor", Instant.now());
    }

}
