/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.search.MemberRefItem;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "members")
public class MembersList extends TransferList<MembersView>{

    public MembersList() {
    }

    public MembersList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static MembersList create(MediaPermissionEvaluator permissionEvaluator, List<MemberRefItem> fullList) {
        MembersList simpleList = new MembersList(true, "");

        for(MemberRefItem memberRef : fullList) {
            if(memberRef != null) {
                simpleList.add(MembersView.create(permissionEvaluator, memberRef));
            }
        }

        return simpleList;
    }

    public static MembersList create(MediaPermissionEvaluator permissionEvaluator, MemberRefItem memberRef) {
        MembersList simpleList = new MembersList(true, "");
        simpleList.add(MembersView.create(permissionEvaluator, memberRef));
        return simpleList;
    }
}
