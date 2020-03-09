/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.*;

public interface EditorService extends UserService<Editor> {


    @Override
    default boolean needsUpdate(Editor oldUser, Editor newUser) {
        return oldUser == null ||
            ! Objects.equals(oldUser.getAllowedBroadcasters(), newUser.getAllowedBroadcasters()) ||
            ! Objects.equals(oldUser.getAllowedPortals(), newUser.getAllowedPortals()) ||
            ! Objects.equals(oldUser.getAllowedThirdParties(), newUser.getAllowedThirdParties());
    }


    default Optional<Broadcaster> currentEmployer() {
        return currentUser().map(Editor::getEmployer);
    }

    default SortedSet<Broadcaster> allowedBroadcasters() {
        return currentUser()
            .map(Editor::getAllowedBroadcasters)
            .orElseGet(Collections::emptySortedSet);
    }

    default List<String> allowedBroadcasterIds() {
        List<String> broadcasters = new ArrayList<>();
        for (Broadcaster broadcaster : allowedBroadcasters()) {
            broadcasters.add(broadcaster.getId());
        }
        return broadcasters;
    }

    default   SortedSet<Portal> allowedPortals() {
        return currentUser()
            .map(Editor::getAllowedPortals)
            .orElseGet(Collections::emptySortedSet);
    }



}
