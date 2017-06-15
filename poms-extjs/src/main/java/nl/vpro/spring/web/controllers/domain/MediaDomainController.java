package nl.vpro.spring.web.controllers.domain;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaService;

@Controller
@RequestMapping(value = "/media")
@Transactional
public class MediaDomainController {

    @Autowired
    private MediaService mediaService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {"application/xml", "application/json"})
    @ResponseBody
    public MediaObject get(@PathVariable("id") final long mediaId, HttpServletResponse response) throws Exception {
        MediaObject media = mediaService.get(mediaId);

        if(media == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No media for id " + mediaId);
        }

        return media;
    }
}
