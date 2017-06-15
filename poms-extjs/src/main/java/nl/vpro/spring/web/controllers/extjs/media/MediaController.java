package nl.vpro.spring.web.controllers.extjs.media;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.search.DateRange;
import nl.vpro.domain.media.search.MediaForm;
import nl.vpro.domain.media.search.Pager;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.errors.ErrorList;
import nl.vpro.transfer.extjs.media.support.*;
import nl.vpro.util.DateUtils;
import nl.vpro.util.TextUtil;
import nl.vpro.web.media.ExtMediaService;

@Controller
@RequestMapping(value = "/media")
public class MediaController {

    private final static Logger LOG = LoggerFactory.getLogger(MediaController.class);


    @Autowired
    private MediaService mediaService;

    @Autowired
    private ExtMediaService extMediaService;

    @Autowired
    private RelationDefinitionService relationDefinitionService;

    @Autowired
    private MediaPermissionEvaluator permissionEvaluator;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public TransferList viewMediaList(
        @RequestParam(value = "text", required = false, defaultValue = "") String text,
        @RequestParam(value = "mediaType", required = false, defaultValue = "MEDIA") String type,
        @RequestParam(value = "start", required = false, defaultValue = "0") int start,
        @RequestParam(value = "limit", required = false, defaultValue = "25") int limit,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "dir", required = false, defaultValue = "asc") String dir,
        @RequestParam(value = "eventStart", required = false, defaultValue = "") String eventStart,
        @RequestParam(value = "eventStop", required = false, defaultValue = "") String eventStop,
        @RequestParam(value = "createdBy", required = false, defaultValue = "") String createdBy,
        @RequestParam(value = "creationStart", required = false, defaultValue = "") String creationStart,
        @RequestParam(value = "creationStop", required = false, defaultValue = "") String creationStop,
        @RequestParam(value = "modifiedBy", required = false, defaultValue = "") String modifiedBy,
        @RequestParam(value = "modifiedStart", required = false, defaultValue = "") String modifiedStart,
        @RequestParam(value = "modifiedStop", required = false, defaultValue = "") String modifiedStop,
        @RequestParam(value = "noBroadcast", required = false, defaultValue = "false") String noBCString,
        @RequestParam(value = "hasLocations", required = false, defaultValue = "false") String hasLocationsString,
        @RequestParam(value = "noPlaylist", required = false, defaultValue = "false") String noPlaylistString,
        @RequestParam(value = "broadcasters", required = false, defaultValue = "") String broadcastersString,
        @RequestParam(value = "portals", required = false, defaultValue = "") String portalsString,
        @RequestParam(value = "onlyWritable", required = false, defaultValue = "false") boolean onlyWritable,
        @RequestParam(value = "tags", required = false, defaultValue = "") String tags,
        @RequestParam(value = "avType", required = false, defaultValue = "") String avType,
        @RequestParam(value = "notAnEpisode", required = false, defaultValue = "false") String notAnEpisode
    ) throws ParseException {


        try {
            MediaForm form = createForm(text, type, start, limit, sort, dir, eventStart, eventStop, createdBy, creationStart, creationStop, modifiedBy, modifiedStart, modifiedStop, noBCString, hasLocationsString, noPlaylistString, broadcastersString, portalsString, MediaType.valueOf(type).hasEpisodeOf() ? notAnEpisode : "false");
            if(!StringUtils.isEmpty(tags)) {
                form.setTags(Arrays.asList(tags.split("\\s*,\\s*")));
            }
            if(!StringUtils.isEmpty(avType)) {
                form.setAvType(AVType.valueOf(avType));
            }

            return extMediaService.search(form, onlyWritable);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public TransferList<?> createMedia(@RequestBody MultiValueMap<String, String> post) {
        try {
            return extMediaService.createMedia(post);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}.xml", method = RequestMethod.GET, produces = "application/xml")
    @ResponseBody
    public MediaObject getMediaXml(@PathVariable(value = "id") String id) {
        return mediaService.get(Long.valueOf(id));
    }

    @RequestMapping(value = "/{id}.json", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public MediaObject getMediaJson(@PathVariable(value = "id") String id) {
        return mediaService.get(Long.valueOf(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<?> getMedia(@PathVariable(value = "id") String mediaId) {
        try {
            try {
                return extMediaService.getMedia(Long.valueOf(mediaId));
            } catch(NumberFormatException e) {
                MediaObject media = mediaService.findByReference(mediaId);
                if(media == null) {
                    return new ErrorList("No media for id " + mediaId);
                }
                return extMediaService.getMedia(media.getId());
            }
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList updateMedia(@PathVariable(value = "id") String id, @RequestBody MultiValueMap<String, String> jsonMedia) {

        try {
            return extMediaService.updateMedia(Long.valueOf(id), jsonMedia);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public TransferList deleteMedia(@PathVariable(value = "id") String id) {
        try {
            return extMediaService.deleteMedia(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/workflow", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getWorkflow(@PathVariable(value = "id") String id) {
        String workflow = "UNKNOWN";

        MediaObject media = get(id);
        if(media != null) {
            workflow = media.getWorkflow().name();
        }

        Map<String, String> result = new HashMap<>(3);
        result.put("type", "event");
        result.put("name", "workflow");
        result.put("id", id);
        result.put("workflow", workflow);

        return result;
    }

    @RequestMapping(value = "/{id}/segments", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getSegments(@PathVariable(value = "id") String id) {
        try {
            return extMediaService.getSegments(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/segments", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList updateSegment(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateSegments(Long.valueOf(id), body);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/locations", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getLocations(@PathVariable(value = "id") String id) {
        try {
            return extMediaService.getLocations(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/locations", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList updateLocations(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateLocations(Long.valueOf(id), body);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/relations", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getRelations(@PathVariable(value = "id") final String id) {
        try {
            return extMediaService.getRelations(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/relations", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList updateRelations(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateRelations(Long.valueOf(id), body);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/episodes", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getEpisodes(@PathVariable(value = "id") String id) {
        try {
            return extMediaService.getEpisodes(Long.valueOf(id));
        } catch(Exception e) {
            LOG.error(e.getMessage(), e);
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/episodes", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList updateEpisodes(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateEpisodes(Long.valueOf(id), body);
        } catch(CircularReferenceException e) {
            return new ErrorList("Saved media has an ancestor equal to one of the episodes added here. This is not allowed since it would cause a circular reference");
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/episodeof", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getEpisodeOf(@PathVariable(value = "id") String id) {
        try {
            return extMediaService.getEpisodeOf(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/episodeof", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList createEpisodeOf(@PathVariable(value = "id") String id, @RequestBody MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateEpisodeOfs(Long.valueOf(id), body);
        } catch(CircularReferenceException e) {
            return new ErrorList("Saved media has a descendant equal to one of the episodeOfs added here. This is not allowed since it would cause a circular reference");
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/members", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getMembers(@PathVariable(value = "id") String id) {
        try {
            return extMediaService.getMembers(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/members", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList updateMember(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateMembers(Long.valueOf(id), body);
        } catch(CircularReferenceException e) {
            return new ErrorList("Saved media has an ancestor equal to one of the members added here. This is not allowed since it would cause a circular reference");
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/memberof", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getMemberOf(@PathVariable(value = "id") String id) {
        try {
            return extMediaService.getMemberOf(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/memberof", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList createMemberOf(@PathVariable(value = "id") String id, @RequestBody MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateMemberOfs(Long.valueOf(id), body);
        } catch(CircularReferenceException e) {
            return new ErrorList("Saved media has a descendant equal to on of the memberOf added here. This is not allowed since it would cause a circular reference");
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/descendants", method = RequestMethod.GET)
    @ResponseBody
    public Long countDescendants(@PathVariable(value = "id") long id, HttpServletResponse response) throws IOException {
        MediaObject media = mediaService.get(id);
        if(media == null) {
            response.sendError(HttpServletResponse.SC_FOUND);
            return null;
        }

        return mediaService.countDescendants(media);
    }

    @RequestMapping(value = "/{id}/images", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getImages(@PathVariable(value = "id") String id) {
        try {
            return extMediaService.getImages(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/images", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList updateImage(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateImages(Long.valueOf(id), body);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/persons", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getPersons(@PathVariable(value = "id") String id) {
        try {
            return extMediaService.getPersons(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/persons", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList updatePerson(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdatePersons(Long.valueOf(id), body);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/websites", method = RequestMethod.GET)
    @ResponseBody
    public TransferList getWebsites(@PathVariable(value = "id") final String id) {
        try {
            return extMediaService.getWebsites(Long.valueOf(id));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/websites", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList updateWebsites(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateWebsites(Long.valueOf(id), body);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }


    @RequestMapping(value = "/{id}/portalRestrictions", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<?> getPortalRestrictions(@PathVariable(value = "id") final String id) {
        TransferList<?> transferList;
        try {
            transferList = extMediaService.getPortalRestrictions(Long.valueOf(id));
        } catch(Exception e) {
            transferList = new ErrorList(e);
        }

        return transferList;
    }

    @RequestMapping(value = "/{id}/portalRestrictions", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList<?> updatePortalRestrictions(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdatePortalRestrictions(Long.valueOf(id), body);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/geoRestrictions", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<?> getMediaGeoRestrictions(@PathVariable(value = "id") final String id) {

        TransferList<?> transferList;
        try {
            transferList = extMediaService.getGeoRestrictions(Long.valueOf(id));
        } catch(Exception e) {
            transferList = new ErrorList(e);
        }

        return transferList;
    }

    @RequestMapping(value = "/{id}/geoRestrictions", method = RequestMethod.PUT)
    @ResponseBody
    public TransferList<?> updateMediaGeoRestrictions(@PathVariable(value = "id") final String id, @RequestBody final MultiValueMap<String, String> body) {
        try {
            return extMediaService.createOrUpdateGeoRestrictions(Long.valueOf(id), body);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/{id}/ownerdata", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<?> getOwnerData(@PathVariable(value = "id") final String id, @RequestParam(value = "owner", required = true) String owner) {
        try {
            return extMediaService.getOwnerData(Long.valueOf(id), OwnerType.valueOf(owner));
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }


    @RequestMapping(value = "/titles", method = RequestMethod.GET)
    @ResponseBody
    public TransferList suggestTitles(
        @RequestParam(value = "query", required = false, defaultValue = "") String text,
        @RequestParam(value = "mediaType", required = false, defaultValue = "MEDIA") String type,
        @RequestParam(value = "start", required = false, defaultValue = "0") int start,
        @RequestParam(value = "limit", required = false, defaultValue = "25") int limit,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "dir", required = false, defaultValue = "asc") String dir,
        @RequestParam(value = "eventStart", required = false, defaultValue = "") String eventStart,
        @RequestParam(value = "eventStop", required = false, defaultValue = "") String eventStop,
        @RequestParam(value = "createdBy", required = false, defaultValue = "") String createdBy,
        @RequestParam(value = "creationStart", required = false, defaultValue = "") String creationStart,
        @RequestParam(value = "creationStop", required = false, defaultValue = "") String creationStop,
        @RequestParam(value = "modifiedBy", required = false, defaultValue = "") String modifiedBy,
        @RequestParam(value = "modifiedStart", required = false, defaultValue = "") String modifiedStart,
        @RequestParam(value = "modifiedStop", required = false, defaultValue = "") String modifiedStop,
        @RequestParam(value = "noBroadcast", required = false, defaultValue = "false") String noBCString,
        @RequestParam(value = "hasLocations", required = false, defaultValue = "false") String hasLocationsString,
        @RequestParam(value = "noPlaylist", required = false, defaultValue = "false") String noPlaylistString,
        @RequestParam(value = "broadcasters", required = false, defaultValue = "") String broadcastersString,
        @RequestParam(value = "portals", required = false, defaultValue = "") String portalsString,
        @RequestParam(value = "notAnEpisode", required = false, defaultValue = "false") String notAnEpisode

    ) {
        try {
            MediaForm form = createForm(text, type, start, limit, sort, dir, eventStart, eventStop, createdBy, creationStart, creationStop, modifiedBy, modifiedStart, modifiedStop, noBCString, hasLocationsString, noPlaylistString, broadcastersString, portalsString, MediaType.valueOf(type).hasEpisodeOf() ? notAnEpisode : "false");
            return extMediaService.suggestTitles(form);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    @ResponseBody
    public TransferList suggestTags(
        @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
        @RequestParam(value = "query", required = true) String input) {

        try {
            return extMediaService.suggestTags(input, limit);
        } catch(Exception e) {
            return new ErrorList(e);
        }
    }

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    @ResponseBody
    public FormatsList validateText(@RequestBody MultiValueMap<String, String> post, HttpServletResponse response) throws IOException {
        String text = post.getFirst("text");
        if(TextUtil.isValid(text)) {
            return null;
        } else {
            response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return null;
        }
    }

    @RequestMapping(value = "/formats", method = RequestMethod.GET)
    @ResponseBody
    public FormatsList listFormats() {
        return FormatsList.create();
    }

    @RequestMapping(value = "/types", method = RequestMethod.GET)
    @ResponseBody
    public MediaTypesList listMediaTypes() {
        return MediaTypesList.create(permissionEvaluator);
    }

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    @ResponseBody
    public RoleList listRoles() {
        return RoleList.create();
    }

    @RequestMapping(value = "/avtypes", method = RequestMethod.GET)
    @ResponseBody
    public AVTypeList listAVTypes() {
        return AVTypeList.create();
    }

    @RequestMapping(value = "/ownertypes", method = RequestMethod.GET)
    @ResponseBody
    public OwnerTypeList listOwnerTypes() {
        return OwnerTypeList.create();
    }

    @RequestMapping(value = "/genretypes", method = RequestMethod.GET)
    @ResponseBody
    public GenreTypeList listGenreTypes() {
        return GenreTypeList.create();
    }

    @RequestMapping(value = "/relationdefinitions", method = RequestMethod.GET)
    @ResponseBody
    public RelationDefinitionsList listRelationDefinitions() {
        return RelationDefinitionsList.create(relationDefinitionService.findAll());
    }



    protected MediaForm createForm(
        String text,
        String type,
        int start,
        int limit,
        String sort,
        String dir,
        String eventStart,
        String eventStop,
        String createdBy,
        String creationStart,
        String creationStop,
        String modifiedBy,
        String modifiedStart,
        String modifiedStop,
        String noBCString,
        String hasLocationsString,
        String noPlaylistString,
        String broadcastersString,
        String portalsString,
        String notAnEpisode
    ) throws ParseException {

        SimpleDateFormat dayFormat = new SimpleDateFormat("ddMMyyyy");

        Pager pager = new Pager(start, limit, sort, Pager.Direction.valueOf(dir.toUpperCase()));
        Collection<String> broadcasters;
        if(StringUtils.isEmpty(broadcastersString)) {
            broadcasters = null;
        } else {
            broadcasters = Arrays.asList(broadcastersString.split(","));
        }
        Collection<String> portals = StringUtils.isEmpty(portalsString) ? null : Arrays.asList(portalsString.split(","));

        List<MediaType> types = new ArrayList<>();
        for(String s : type.split("\\*,\\s")) {
            types.add(MediaType.valueOf(s.toUpperCase()));
        }

        boolean noBroadcast = Boolean.valueOf(noBCString);

        boolean hasLocations = Boolean.valueOf(hasLocationsString);

        boolean noPlaylist = Boolean.valueOf(noPlaylistString);

        DateRange eventRange = null;
        if(StringUtils.isNotEmpty(eventStart) || StringUtils.isNotEmpty(eventStop)) {
            eventRange = new DateRange(DateUtils.toInstant(dayFormat.parse(eventStart)), DateUtils.toInstant(dayFormat.parse(eventStop)));
        }

        DateRange creationRange = null;
        if(StringUtils.isNotEmpty(creationStart) || StringUtils.isNotEmpty(creationStop)) {
            creationRange = new DateRange(DateUtils.toInstant(dayFormat.parse(creationStart)), DateUtils.toInstant(dayFormat.parse(creationStop)));
        }

        DateRange modifiedRange = null;
        if(StringUtils.isNotEmpty(modifiedStart) || StringUtils.isNotEmpty(modifiedStop)) {
            modifiedRange = new DateRange(DateUtils.toInstant(dayFormat.parse(modifiedStart)), DateUtils.toInstant(dayFormat.parse(modifiedStop)));
        }

        MediaForm form = new MediaForm(
            pager,
            broadcasters,
            portals,
            text,
            types,
            noBroadcast,
            hasLocations,
            noPlaylist,
            eventRange,
            createdBy,
            creationRange,
            modifiedBy,
            modifiedRange,
            Boolean.valueOf(notAnEpisode)
        );

        return form;
    }

    private MediaObject get(String mediaId) {
        MediaObject media;
        try {
            media = mediaService.get(Long.parseLong(mediaId));
        } catch(NumberFormatException nfe) {
            media = mediaService.findByMid(mediaId);
        }
        return media;
    }
}
