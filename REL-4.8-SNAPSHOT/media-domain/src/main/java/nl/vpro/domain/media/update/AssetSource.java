/**
 * Copyright (C) 2014 All rights reserved
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
