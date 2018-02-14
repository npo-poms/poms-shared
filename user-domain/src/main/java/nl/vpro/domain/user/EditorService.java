/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public interface EditorService extends UserService<Editor> {

    Broadcaster currentEmployer();

    SortedSet<Broadcaster> allowedBroadcasters();

    default List<String> allowedBroadcasterIds() {
        List<String> broadcasters = new ArrayList<>();
        for (Broadcaster broadcaster : allowedBroadcasters()) {
            broadcasters.add(broadcaster.getId());
        }
        return broadcasters;
    }

}
