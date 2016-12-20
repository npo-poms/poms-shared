package nl.vpro.rs.media;

import java.io.IOException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.search.MediaForm;
import nl.vpro.domain.media.search.MediaList;
import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.domain.media.update.*;
import nl.vpro.domain.media.update.action.MoveAction;
import nl.vpro.domain.media.update.collections.XmlCollection;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@Path("/media")
@Consumes({MediaType.APPLICATION_XML, MultipartConstants.MULTIPART_RELATED})
@Produces(MediaType.APPLICATION_XML)
public interface MediaBackendRestService {


    @POST
    @Path("find")
    MediaList<MediaListItem> find(
        MediaForm form,
        @QueryParam("writable") @DefaultValue("false") boolean writable
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}")
    MediaUpdate getMedia(
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    Response deleteMedia(
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/full")
    MediaObject getFullMediaObject(
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|segment|program|group)}")
    @Produces(MediaType.TEXT_PLAIN)
    Response update(
        @PathParam("entity") final String entity,
        @XopWithMultipartRelated MediaUpdate update,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors,
        @QueryParam("lookupcrid") @DefaultValue("true") Boolean lookupcrid
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/location")
    //@Produces(MediaType.TEXT_PLAIN)
    Response addLocation(
        @PathParam("entity") final String entity,
        LocationUpdate location,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    );

    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id}/location/{locationId}")
    @Produces(MediaType.TEXT_PLAIN)
    Response removeLocation(
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @PathParam("locationId") final String locationId,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    );

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/locations")
    XmlCollection<LocationUpdate> getLocations(
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/image")
    //@Produces(MediaType.TEXT_PLAIN)
    Response addImage(
        ImageUpdate imageUpdate,
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    );


    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/images")
    XmlCollection<ImageUpdate> getImages(
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges
    ) throws IOException;


    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/members")
    MediaUpdateList<MemberUpdate> getGroupMembers(
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("offset") @DefaultValue("0") final Long offset,
        @QueryParam("max") @DefaultValue("20") final Integer max,
        @QueryParam("order") @DefaultValue("ASC") final String order,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @PUT
    @Path("{entity:(media|program|group|segment)}/{id}/members")
    @Produces(MediaType.TEXT_PLAIN)
    Response moveMembers(
        MoveAction move,
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/memberOfs")
    MediaUpdateList<MemberRefUpdate> getMemberOfs(
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/memberOf")
    @Produces(MediaType.TEXT_PLAIN)
    Response addMemberOf(
        MemberRefUpdate memberRefUpdate,
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    ) throws IOException;


    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id}/memberOf/{owner}")
    @Produces(MediaType.TEXT_PLAIN)
    Response removeMemberOf(
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @PathParam("owner") final String owner,
        @QueryParam("number") final Integer number,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    ) throws IOException;


    @GET
    @Path("group/{id}/episodes")
    MediaUpdateList<MemberUpdate> getGroupEpisodes(
        @PathParam("id") final String id,
        @QueryParam("offset") @DefaultValue("0") final Long offset,
        @QueryParam("max") @DefaultValue("10") final Integer max,
        @QueryParam("order") @DefaultValue("ASC") final String order,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @PUT
    @Path("{entity:(media|program|group|segment)}/{id}/episodes")
    @Produces(MediaType.TEXT_PLAIN)
    Response moveEpisodes(
        MoveAction move,
        @PathParam("entity") final String entity,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    ) throws IOException;

    @GET
    @Path("program/{id}/episodeOfs")
    MediaUpdateList<MemberRefUpdate> getEpisodeOfs(
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges
    ) throws IOException;

    @POST
    @Path("program/{id}/episodeOf")
    @Produces(MediaType.TEXT_PLAIN)
    Response addEpisodeOf(
        MemberRefUpdate memberRefUpdate,
        @PathParam("id") final String id,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    ) throws IOException;


    @DELETE
    @Path("program/{id}/episodeOf/{owner}")
    @Produces(MediaType.TEXT_PLAIN)
    Response removeEpisodeOf(
        @PathParam("id") final String id,
        @PathParam("owner") final String owner,
        @QueryParam("number") final Integer number,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    ) throws IOException;


    @DELETE
    @Path("program/{id}/segment/{segmentId}")
    @Produces(MediaType.TEXT_PLAIN)
    Response removeSegment(
        @PathParam("id") final String id,
        @PathParam("segmentId") final String segment,
        @QueryParam("followMerges") @DefaultValue("true") boolean followMerges,
        @QueryParam("errors") String errors
    ) throws IOException;

}
