/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.spring.web.controllers.extjs.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import nl.vpro.domain.media.MediaService;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.domain.user.PortalService;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.errors.ErrorList;
import nl.vpro.transfer.extjs.media.AdminBroadcasterList;
import nl.vpro.transfer.extjs.media.OrganizationsList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping(value = "/organization")
public class OrganizationController {

    @Autowired
    private BroadcasterService broadcasterService;

    @Autowired
    private PortalService portalService;

    @Autowired
    private MediaService mediaService;


    @RequestMapping(value = "/broadcasters", method = RequestMethod.GET)
    @ResponseBody
    public TransferList broadcasters() {
        List<Broadcaster> broadcasterList = broadcasterService.findAll();
        return OrganizationsList.create(broadcasterList);
    }


    @RequestMapping(value = "/broadcasterscount", method = RequestMethod.GET)
    @ResponseBody
    public TransferList broadcastersCount() {
        return AdminBroadcasterList.create(mediaService, broadcasterService.findAll());
    }

    @RequestMapping(value = "/broadcasters/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList<?> addBroadcaster(@PathVariable(value = "id") String broadcaster,
                                          @RequestBody MultiValueMap<String, String> post) {
        return addBroadcaster(post);
    }

    @RequestMapping(value = "/broadcasters", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public TransferList<?> addBroadcaster(@RequestBody MultiValueMap<String, String> post) {
        try {
            List<Broadcaster> result = new ArrayList<Broadcaster>();
            for (Broadcaster b : unmarshalBroadcasters(post)) {
                result.add(broadcasterService.update(b));
            }
            return AdminBroadcasterList.create(mediaService, result);
        } catch (Exception e) {
            return new ErrorList(e.getMessage());
        }

    }


    @RequestMapping(value = "/broadcasters/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public TransferList<?> deleteBroadcaster(@PathVariable(value = "id") String broadcaster) {
        return delete(Collections.singletonList(broadcaster));
    }

    @RequestMapping(value = "/broadcasters", method = RequestMethod.DELETE)
    @ResponseBody
    public TransferList<?> deleteBroadcaster(@RequestBody MultiValueMap<String, String> post) {
        return delete(unmarshalStrings(post));
    }

    protected TransferList<?> delete(Collection<String> broadcasters) {
        try {
            for (String broadcaster : broadcasters) {
                Broadcaster toDelete = broadcasterService.find(broadcaster);
                if(toDelete != null) {
                    broadcasterService.delete(toDelete);
                }
            }

            return OrganizationsList.create();
        } catch(Exception e) {
            return new ErrorList(e.getMessage());
        }
    }

    protected List<Broadcaster> unmarshalBroadcasters(MultiValueMap<String, String> map) {

        String s = map.getFirst("list");
        if (s.startsWith("[")) {
            List<Broadcaster> result = new ArrayList<Broadcaster>();
            JSONArray array = JSONArray.fromObject(s);
            for (int i = 0; i < array.size(); i ++) {
                result.add(unmarshalBroadcaster(array.getJSONObject(i)));
            }
            return result;
        } else {
            return Collections.singletonList(unmarshalBroadcaster(JSONObject.fromObject(s)));
        }

    }
    protected Broadcaster unmarshalBroadcaster(JSONObject json) {
        String id = json.getString("id");
        String whatsonid = json. getString("whatsOnId");
        String neboId = json.getString("neboId");
        String misId = json.getString("misId");

        String displayName = json.getString("displayName");

        return new Broadcaster(id, displayName, whatsonid, neboId, misId);
    }

    protected List<String> unmarshalStrings(MultiValueMap<String, String> map) {

        String s = map.getFirst("list");
        List<String> result = new ArrayList<String>();
        JSONArray array = JSONArray.fromObject(s);
        for (int i = 0; i < array.size(); i++) {
            result.add(array.getString(i));
        }
        return result;
    }

    @RequestMapping(value = "/portals", method = RequestMethod.GET)
    @ResponseBody
    public TransferList portals() {
        return OrganizationsList.create(portalService.findAll());
    }
}
