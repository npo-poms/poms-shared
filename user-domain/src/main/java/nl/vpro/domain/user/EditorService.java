/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.*;

public interface EditorService extends UserService<Editor> {


    @Override
    default boolean needsUpdate(Editor oldUser, Editor newUser) {
        return oldUser == null
            ||
            ! Objects.equals(oldUser.getPrincipalId(), newUser.getPrincipalId()) ||
            ! Objects.equals(oldUser.getAllowedBroadcasters(), newUser.getAllowedBroadcasters()) ||
            ! Objects.equals(oldUser.getAllowedPortals(), newUser.getAllowedPortals()) ||
            ! Objects.equals(oldUser.getAllowedThirdParties(), newUser.getAllowedThirdParties());
    }


    Optional<Broadcaster> currentEmployer();

    SortedSet<Broadcaster> allowedBroadcasters();

    default List<String> allowedBroadcasterIds() {
        List<String> broadcasters = new ArrayList<>();
        for (Broadcaster broadcaster : allowedBroadcasters()) {
            broadcasters.add(broadcaster.getId());
        }
        return broadcasters;
    }

}
