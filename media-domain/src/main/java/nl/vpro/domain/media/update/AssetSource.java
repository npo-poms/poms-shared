/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.io.InputStream;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public interface AssetSource {

    InputStream getInputStream();

}
