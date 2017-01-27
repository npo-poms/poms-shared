package nl.vpro.rs.media;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.search.MediaForm;
import nl.vpro.domain.media.search.MediaList;
import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.domain.media.update.*;
import nl.vpro.domain.media.update.action.MoveAction;
import nl.vpro.domain.media.update.collections.XmlCollection;
import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesType;

import static nl.vpro.api.rs.subtitles.Constants.EBU;
import static nl.vpro.api.rs.subtitles.Constants.SRT;
import static nl.vpro.api.rs.subtitles.Constants.VTT;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@Path("/media")
@Consumes({MediaType.APPLICATION_XML, MultipartConstants.MULTIPART_RELATED})
@Produces(MediaType.APPLICATION_XML)
public interface MediaBackendRestService {
    String ENTITY = "entity";
    String FOLLOW = "followMerges";
    String ERRORS = "errors";
    String ID  = "id";


    @POST
    @Path("find")
    MediaList<MediaListItem> find(
        MediaForm form,
        @QueryParam("writable") @DefaultValue("false") boolean writable
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}")
    MediaUpdate getMedia(
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id}")
    @Produces(MediaType.WILDCARD)
    Response deleteMedia(
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/full")
    MediaObject getFullMediaObject(
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|segment|program|group)}")
    @Produces(MediaType.WILDCARD)
    Response update(
        @PathParam(ENTITY) final String entity,
        @XopWithMultipartRelated MediaUpdate update,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam("lookupcrid") @DefaultValue("true") Boolean lookupcrid
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/location")
    @Produces(MediaType.WILDCARD)
    Response addLocation(
        @PathParam(ENTITY) final String entity,
        LocationUpdate location,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    );

    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id}/location/{locationId}")
    @Produces(MediaType.WILDCARD)
    Response removeLocation(
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @PathParam("locationId") final String locationId,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    );

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/locations")
    XmlCollection<LocationUpdate> getLocations(
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/image")
    @Produces(MediaType.WILDCARD)
    Response addImage(
        ImageUpdate imageUpdate,
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    );


    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/images")
    XmlCollection<ImageUpdate> getImages(
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    ) throws IOException;


    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/members")
    MediaUpdateList<MemberUpdate> getGroupMembers(
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam("offset") @DefaultValue("0") final Long offset,
        @QueryParam("max") @DefaultValue("20") final Integer max,
        @QueryParam("order") @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @PUT
    @Path("{entity:(media|program|group|segment)}/{id}/members")
    @Produces(MediaType.WILDCARD)
    Response moveMembers(
        MoveAction move,
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/memberOfs")
    MediaUpdateList<MemberRefUpdate> getMemberOfs(
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/memberOf")
    @Produces(MediaType.WILDCARD)
    Response addMemberOf(
        MemberRefUpdate memberRefUpdate,
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id}/memberOf/{owner}")
    @Produces(MediaType.WILDCARD)
    Response removeMemberOf(
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @PathParam("owner") final String owner,
        @QueryParam("number") final Integer number,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @GET
    @Path("group/{id}/episodes")
    MediaUpdateList<MemberUpdate> getGroupEpisodes(
        @PathParam(ID) final String id,
        @QueryParam("offset") @DefaultValue("0") final Long offset,
        @QueryParam("max") @DefaultValue("10") final Integer max,
        @QueryParam("order") @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @PUT
    @Path("{entity:(media|program|group|segment)}/{id}/episodes")
    @Produces(MediaType.WILDCARD)
    Response moveEpisodes(
        MoveAction move,
        @PathParam(ENTITY) final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;

    @GET
    @Path("program/{id}/episodeOfs")
    MediaUpdateList<MemberRefUpdate> getEpisodeOfs(
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @POST
    @Path("program/{id}/episodeOf")
    @Produces(MediaType.WILDCARD)
    Response addEpisodeOf(
        MemberRefUpdate memberRefUpdate,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @DELETE
    @Path("program/{id}/episodeOf/{owner}")
    @Produces(MediaType.WILDCARD)
    Response removeEpisodeOf(
        @PathParam(ID) final String id,
        @PathParam("owner") final String owner,
        @QueryParam("number") final Integer number,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @DELETE
    @Path("program/{id}/segment/{segmentId}")
    @Produces(MediaType.WILDCARD)
    Response removeSegment(
        @PathParam(ID) final String id,
        @PathParam("segmentId") final String segment,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @GET
    @Path("subtitles/{id}/{language}/{type}")
    @Produces({VTT, EBU, SRT})
    Iterator<StandaloneCue> get(
        @PathParam("id") String id,
        @PathParam("language") Locale language,
        @PathParam("type") SubtitlesType type,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    );

    @GET
    @Path("subtitles/{id}")
    @Wrapped(element = "subtitles", namespace = Xmlns.MEDIA_SUBTITLES_NAMESPACE)
    List<Subtitles> getAllSubtitles(
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges
    ) throws IOException;


    @POST
    @Path("subtitles/{id}/{language}/{type}")
    @Consumes({VTT, EBU, SRT})
    Response setSubtitles(
        @PathParam("id") String id,
        @PathParam("language") Locale language,
        @PathParam("type") SubtitlesType type,
        @QueryParam(FOLLOW) @DefaultValue("true") boolean followMerges,
        @QueryParam(ERRORS) String errors,
        Iterator<StandaloneCue> cues);

}


