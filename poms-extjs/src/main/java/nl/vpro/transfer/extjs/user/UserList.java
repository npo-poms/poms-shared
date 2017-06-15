/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.user;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.user.Editor;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "users")
public class UserList extends TransferList<EditorView> {

    private UserList() {
    }

    public static UserList create(List<? extends Editor> users) {
        UserList simpleList = new UserList();

        simpleList.success = true;

        for(Editor user : users) {
            simpleList.add(EditorView.create(user));
        }

        return simpleList;
    }
}
