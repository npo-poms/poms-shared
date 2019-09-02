/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import nl.vpro.domain.*;

/**
 * @since 5.11
 */
public interface MidIdentifiable extends Identifiable<Long> {

    String getMid();

    MediaType getMediaType();


}
