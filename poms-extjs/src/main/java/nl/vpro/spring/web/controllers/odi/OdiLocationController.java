package nl.vpro.spring.web.controllers.odi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaService;
import nl.vpro.media.odi.OdiService;
import nl.vpro.media.odi.util.LocationResult;

@Controller
@RequestMapping(value = "")
public class OdiLocationController {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private OdiService odiService;

    @RequestMapping(method = RequestMethod.POST)
    public LocationResult forPost(
        @RequestBody String url,
        HttpServletRequest request) throws IOException {

        return odiService.playUrl(url, request);
    }

    @RequestMapping(value = "/{mediaId}", method = RequestMethod.GET)
    @ResponseBody
    public LocationResult forMedia(
        @PathVariable(value = "mediaId") long mediaId,
        @RequestParam(value = "order", required = false) String pubOptions,
        HttpServletRequest request,
        HttpServletResponse response) throws Exception {

        MediaObject media = findMedia(mediaId, response);
        if(media == null) {
            return null;
        }


        if(pubOptions != null) {
            return odiService.playMedia(media, request, pubOptions.toLowerCase().split(","));
        } else {
            return odiService.playMedia(media, request);
        }
    }

    @RequestMapping(value = "/{mediaId}/location/{locationId}", method = RequestMethod.GET)
    @ResponseBody
    public LocationResult forLocation(
        @PathVariable(value = "mediaId") long mediaId,
        @PathVariable(value = "locationId") long locationId,
        HttpServletRequest request,
        HttpServletResponse response) throws IOException {

        MediaObject media = findMedia(mediaId, response);
        if(media == null) {
            return null;
        }

        Location location = media.findLocation(locationId);
        if(location == null) {
            notFound("location", locationId, response);
            return null;
        }

        return odiService.playLocation(location, request);
    }

    @RequestMapping(value = "/url", method = RequestMethod.GET)
    @ResponseBody
    public LocationResult forUrl(
        @RequestParam(value = "url") String url,
        @RequestParam(value = "output", required = false, defaultValue = "forward") String output,
        HttpServletRequest request) throws IOException {

        return odiService.playUrl(url, request);
    }

    private MediaObject findMedia(long id, HttpServletResponse response) throws IOException {
        MediaObject result = mediaService.get(id);
        if(result == null) {
            notFound("media", id, response);
        }
        return result;
    }

    private void notFound(String type, long id, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "No " + type + " for id" + id);
    }
}
