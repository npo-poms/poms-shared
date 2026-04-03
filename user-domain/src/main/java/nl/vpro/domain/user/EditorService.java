/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.util.*;
import java.util.stream.Collectors;

import nl.vpro.domain.Roles;
import nl.vpro.domain.media.support.OwnerType;

/**
 * Like {@link UserService} but providing some more security related information
 */
public interface EditorService extends UserService<Editor> {

    @Override
    default boolean needsUpdate(Editor oldUser, Editor newUser) {
        return oldUser == null ||
            ! Objects.equals(oldUser.getLastLogin(), newUser.getLastLogin()) ||
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
        return allowedBroadcasters().stream()
            .map(Broadcaster::getId)
            .collect(Collectors.toList());
    }

    default   SortedSet<Portal> allowedPortals() {
        return currentUser()
            .map(Editor::getAllowedPortals)
            .orElseGet(Collections::emptySortedSet);
    }

    default SortedSet<Broadcaster> activeBroadcasters() {
        return currentUser()
            .map(Editor::getActiveBroadcasters)
            .orElseGet(Collections::emptySortedSet);
    }

    default SortedSet<ThirdParty> allowedThirdParties() {
        return currentUser()
            .map(Editor::getAllowedThirdParties)
            .orElseGet(Collections::emptySortedSet);
    }

    default SortedSet<ThirdParty> activeThirdParties() {
        return currentUser()
            .map(Editor::getAllowedThirdParties)
            .orElseGet(Collections::emptySortedSet);
     }


    default OwnerType currentOwner() {
        return currentUser().map(e -> {
            String principalId = e.getPrincipalId();
            if  ("mis-importer".equals(principalId)) {
                return OwnerType.MIS;
           /* } else if ("radiobox-importer".equals(principalId)) { // radiobox is gone since 2020.
                return OwnerType.RADIOBOX;*/
            } else if (currentUserCanChooseOwnerType()) {
                return OwnerType.NPO;
            } else {
                return OwnerType.BROADCASTER;
            }
        }).orElse(null);
    }

    default boolean currentUserCanChooseOwnerType() {
        return currentUserHasRole(Roles.CAN_CHOOSE_OWNER_TYPE);
    }


    default boolean isAuthenticatedAs(String principalId) {
        return isAuthenticated() && principalId.equals(currentUser().map(AbstractUser::getPrincipalId).orElse(null));
    }

}
