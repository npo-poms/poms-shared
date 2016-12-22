/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.page.update;

import java.util.List;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public interface PageUpdateRepository {

    List<String> namesForSectionPath(String path, String portalId);


}
