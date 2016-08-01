/**
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.secondscreen;

import java.util.List;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Service;

/**
 * @author Roelof Jan Koekoek
 * @since 3.8
 */
public interface ScreenService extends Service<Long, Screen> {

    List<Screen> findAll();

    <S extends Screen> S findById(String id);

    boolean deleteScreenOf(MediaObject media);
}
