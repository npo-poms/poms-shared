/*
 * Copyright (C) 2014 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
public class MemberRefs {

    private MemberRefs() {
    }

    public static List<MediaObject> listOwners(final List<MemberRef> memberRefs) {
        return new AbstractList<MediaObject>() {
            @Override
            public MediaObject get(int index) {
                return  memberRefs.get(index).getGroup();
            }

            @Override
            public int size() {
                return memberRefs.size();
            }
        };
    }


    public static List<MediaObject> listMembers(final List<MemberRef> memberRefs) {
        return new AbstractList<MediaObject>() {
            @Override
            public MediaObject get(int index) {
                return  memberRefs.get(index).getMember();
            }

            @Override
            public int size() {
                return memberRefs.size();
            }
        };
    }


    /**
     * @since 5.7
     */
    public static Optional<MemberRef> findRef(Collection<MemberRef> collection, MediaObject owner) {
         if(collection == null) {
            return Optional.empty();
        }

        for(MemberRef memberRef : collection) {
            if (memberRef.getMidRef() != null && memberRef.getMidRef().equals(owner.getMid())) {
                return Optional.of(memberRef);
            }
            if(memberRef.getGroup().equals(owner)) {
                return Optional.of(memberRef);
            }
        }
        return Optional.empty();
    }

     /**
     * @since 5.7
     */
    public static Optional<MemberRef> findRef(Collection<MemberRef> collection, MediaObject owner, Integer number) {
        if (number == null) {
            return findRef(collection, owner);
        }
        for (MemberRef memberRef : collection) {
            if (memberRef.getNumber() != null && memberRef.getNumber().equals(number)) {
                if (memberRef.getMidRef() != null && memberRef.getMidRef().equals(owner.getMid())) {
                    return Optional.of(memberRef);
                }
                if (memberRef.getGroup().equals(owner)) {
                    return Optional.of(memberRef);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * @since 5.7
     */
    public static boolean isOf(Collection<MemberRef> collection, MediaObject owner) {
        return findRef(collection, owner).isPresent();
    }

     /**
     * @since 5.7
     */
    public static boolean isOf(Collection<MemberRef> collection, MediaObject owner, Integer number) {
        return findRef(collection, owner, number).isPresent();
    }
}
