/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

public interface EditorService extends UserService<Editor> {

    Broadcaster currentEmployer();

}
