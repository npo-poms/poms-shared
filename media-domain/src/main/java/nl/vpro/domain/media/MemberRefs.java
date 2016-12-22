/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.AbstractList;
import java.util.List;

/**
 * @author Roelof Jan Koekoek
 * @since 3.3
 */
public class MemberRefs {

    public static List<MediaObject> listOwners(final List<MemberRef> memberRefs) {
        return new AbstractList<MediaObject>() {
            @Override
            public MediaObject get(int index) {
                return  memberRefs.get(index).getOwner();
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
}
