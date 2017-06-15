/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.SortedSet;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MemberRef;
import nl.vpro.domain.media.Program;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "memberOf")
public class MemberRefList extends TransferList<MemberRefView>{

    public MemberRefList() {
    }

    public MemberRefList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static MemberRefList createMemberOf(MediaPermissionEvaluator permissionEvaluator, MediaObject media) {
        SortedSet<MemberRef> fullList = media.getMemberOf();
        return create(permissionEvaluator, fullList);
    }

    public static MemberRefList createEpisodeOf(MediaPermissionEvaluator permissionEvaluator, Program program) {
        SortedSet<MemberRef> fullList = program.getEpisodeOf();
        return create(permissionEvaluator, fullList);
    }

    public static MemberRefList create(MediaPermissionEvaluator permissionEvaluator, MemberRef fullReference) {
        MemberRefList simpleList = new MemberRefList(true, "");
        simpleList.add(MemberRefView.create(permissionEvaluator, fullReference));
        return simpleList;
    }

    private static MemberRefList create(MediaPermissionEvaluator permissionEvaluator, SortedSet<MemberRef> fullList) {
        MemberRefList simpleList = new MemberRefList(true, "");

        for(MemberRef memberRef : fullList) {
            simpleList.add(MemberRefView.create(permissionEvaluator, memberRef));
        }

        return simpleList;
    }
}
