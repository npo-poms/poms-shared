/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.spring.web.controllers.extjs.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import nl.vpro.domain.user.Editor;
import nl.vpro.domain.user.MediaEditorService;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.media.OrganizationView;
import nl.vpro.transfer.extjs.media.OrganizationsList;
import nl.vpro.transfer.extjs.user.UserList;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private MediaEditorService userService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public TransferList current() {
        List<Editor> users = new ArrayList<Editor>();
        users.add(userService.currentUser());
        return UserList.create(users);
    }

    @RequestMapping(value = "/names", method = RequestMethod.GET)
    @ResponseBody
    public TransferList current(
        @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
        @RequestParam(value = "query", required = true) String name) {

        List<? extends Editor> users = userService.findUsers(name, limit);

        return UserList.create(users);
    }

    @RequestMapping(value = "/broadcasters", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<OrganizationView> getCurrentBroadcasters() {
        return OrganizationsList.create(userService.allowedBroadcasters(), userService.activeBroadcasters());
    }



    @RequestMapping(value = "/portals", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<OrganizationView> getCurrentPortals() {
        return OrganizationsList.create(userService.allowedPortals(), userService.activePortals());
    }

    @RequestMapping(value = "/organizations", method = {RequestMethod.PUT})
    @ResponseBody
    public TransferList<OrganizationView> updateSelectedOrganizations(@RequestBody MultiValueMap<String, String> body) {
        List<String> broadcaster = body.get("broadcaster");
        userService.setActiveBroadcasters(broadcaster);
        List<String> portals = body.get("portal");
        userService.setActivePortals(portals);
        // DRS TODO: This isn't right, but IE doesn't like receiving NULL
        return OrganizationsList.create(userService.allowedBroadcasters(), userService.activeBroadcasters());
    }
}
