package nl.vpro.rs.media;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Member;
import nl.vpro.domain.media.Platform;
import nl.vpro.domain.media.search.MediaForm;
import nl.vpro.domain.media.search.MediaList;
import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.domain.media.StreamingStatus;
import nl.vpro.domain.media.update.*;
import nl.vpro.domain.media.update.action.MoveAction;
import nl.vpro.domain.media.update.collections.XmlCollection;
import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesId;
import nl.vpro.domain.subtitles.SubtitlesType;

import static nl.vpro.api.rs.subtitles.Constants.*;

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
    String ID     = "id";
    String MID = "mid";
    String MAX = "max";
    String OFFSET = "offset";
    String ORDER = "order";
    String LANGUAGE = "language";
    String TYPE = "type";

    String VALIDATE_INPUT = "validateInput";
    String VALIDATE_INPUT_DESCRIPTION = "If true, the body will be validated against the XSD first";
    String ERRORS_DESCRIPTION = "An optional email address to which errors could be mailed if they occur asynchronously";
    String IMAGE_METADATA = "imageMetadata";

    String ENCRYPTION = "encryption";
    String PRIOTRITY = "priority";
    String FILE_NAME = "fileName";

    @POST
    @Path("find")
    MediaList<MediaListItem> find(
        MediaForm form,
        @QueryParam("writable") @DefaultValue("false") boolean writable,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}")
    MediaUpdate<?> getMedia(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;


    @GET
    @Path("/exists/{mid:.*}")
    boolean exists(
        @Encoded @PathParam(MID) String mid,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
    );


    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id}")
    @Produces(MediaType.WILDCARD)
    Response deleteMedia(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/full")
    MediaObject getFullMediaObject(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|segment|program|group)}")
    @Produces(MediaType.WILDCARD)
    Response update(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @XopWithMultipartRelated MediaUpdate<?> update,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam("lookupcrid") @DefaultValue("true") Boolean lookupcrid,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput,
        @QueryParam(IMAGE_METADATA) @DefaultValue("false") Boolean imageMetadata
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/location")
    @Produces(MediaType.WILDCARD)
    Response addLocation(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        LocationUpdate location,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput
    );

    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id}/location/{locationId}")
    @Produces(MediaType.WILDCARD)
    Response removeLocation(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @PathParam("locationId") final String locationId,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    );

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/locations")
    XmlCollection<LocationUpdate> getLocations(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/image")
    @Produces(MediaType.WILDCARD)
    Response addImage(
        ImageUpdate imageUpdate,
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput,
        @QueryParam(IMAGE_METADATA) @DefaultValue("false") Boolean imageMetadata

    );


    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/images")
    XmlCollection<ImageUpdate> getImages(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;


    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/members")
    MediaUpdateList<MemberUpdate> getGroupMembers(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("20") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/members/full")
    MediaList<Member> getFullGroupMembers(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("20") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @PUT
    @Path("{entity:(media|program|group|segment)}/{id}/members")
    @Produces(MediaType.WILDCARD)
    Response moveMembers(
        MoveAction move,
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/memberOfs")
    MediaUpdateList<MemberRefUpdate> getMemberOfs(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/memberOf")
    @Produces(MediaType.WILDCARD)
    Response addMemberOf(
        MemberRefUpdate memberRefUpdate,
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput
    ) throws IOException;


    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id}/memberOf/{owner}")
    @Produces(MediaType.WILDCARD)
    Response removeMemberOf(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @PathParam("owner") final String owner,
        @QueryParam("number") final Integer number,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @GET
    @Path("group/{id}/episodes")
    MediaUpdateList<MemberUpdate> getGroupEpisodes(
        @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("10") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;


    @GET
    @Path("group/{id}/episodes/full")
    MediaList<Member> getFullGroupEpisodes(
        @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("10") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @PUT
    @Path("{entity:(media|group)}/{id}/episodes")
    @Produces(MediaType.WILDCARD)
    Response moveEpisodes(
        MoveAction move,
        @PathParam(ENTITY) @DefaultValue("group") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;

    @GET
    @Path("program/{id}/episodeOfs")
    MediaUpdateList<MemberRefUpdate> getEpisodeOfs(
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("program/{id}/episodeOf")
    @Produces(MediaType.WILDCARD)
    Response addEpisodeOf(
        MemberRefUpdate memberRefUpdate,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput
    ) throws IOException;


    @DELETE
    @Path("program/{id}/episodeOf/{owner}")
    @Produces(MediaType.WILDCARD)
    Response removeEpisodeOf(
        @PathParam(ID) final String id,
        @PathParam("owner") final String owner,
        @QueryParam("number") final Integer number,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @DELETE
    @Path("program/{id}/segment/{segmentId}")
    @Produces(MediaType.WILDCARD)
    Response removeSegment(
        @PathParam(ID) final String id,
        @PathParam("segmentId") final String segment,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @GET
    @Path("subtitles/{mid}/{language}/{type}")
    @Produces({VTT, TT888, SRT})
    Subtitles getSubtitles(
        @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    );


    @GET
    @Path("subtitles/{mid}/{language}/{type}/{seq}")
    StandaloneCue getCue(
        @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @PathParam("seq") Integer seq,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam("guessOffset") @DefaultValue("false") Boolean guessOffset
    );

    @GET
    @Path("subtitles/{mid}")
    @Wrapped(element = "subtitles", namespace = Xmlns.MEDIA_SUBTITLES_NAMESPACE)
    List<SubtitlesId> getAllSubtitles(
        @PathParam(MID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("subtitles/{mid}/{language}/{type}")
    @Consumes({VTT, EBU, TT888, SRT})
    Response setSubtitles(
        @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @QueryParam(OFFSET) @DefaultValue("0") Duration offset,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        Subtitles subtitles);

    @POST
    @Path("subtitles/{mid}/{language}/{type}/{offset}")
    Response setSubtitlesOffset(
        @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @PathParam("offset") @DefaultValue("0") Duration offset,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors);


    @DELETE
    @Path("subtitles/{mid}/{language}/{type}")
    Response deleteSubtitles(
        @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors);


    @GET
    @Path("streamingstatus/{mid}")
    StreamingStatus getStreamingstatus(
        @PathParam(MID) String mid,
        @Context HttpServletRequest request
    ) throws IOException, URISyntaxException;


    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/predictions")
    XmlCollection<PredictionUpdate> getPredictions(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;


    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/predictions/{platform}")
    PredictionUpdate getPrediction(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @PathParam("platform") final Platform platform,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;


    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/predictions")
    Response setPredictions(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        XmlCollection<PredictionUpdate> collection
    ) throws IOException;


    @POST
    @Path("{entity:(media|program|group|segment)}/{id}/predictions/{platform}")
    Response setPrediction(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(MID) final String mid,
        @PathParam("platform") final Platform platform,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        PredictionUpdate prediction
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{mid}/transcode")
    Response transcode(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(MID) final String mid,
        @QueryParam(ERRORS) String errors,
        TranscodeRequest transcodeRequest);


    @POST
    @Path("transcode")
    Response transcode(
        @QueryParam(ERRORS) String errors,
        TranscodeRequest transcodeRequest);



    @POST
    @Path("upload/{mid}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, "video/*"})
    TranscodeRequest upload(
        @Encoded @PathParam(MID) final String mid,
        @QueryParam(LOG) @DefaultValue("false") Boolean log,
        @QueryParam("replace") @DefaultValue("false") Boolean replace,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response) throws IOException;



    @POST
    @Path("upload/{mid}/{encryption}/{priority}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, "video/*"})
    Response upload(
        @Encoded @PathParam(MID) final String mid,
        @Encoded @PathParam(ENCRYPTION) final Encryption  encryption,
        @Encoded @PathParam(PRIORITY) final TranscodeRequest.Priority priority,
        @QueryParam(LOG) @DefaultValue("false") Boolean log,
        @QueryParam("replace") @DefaultValue("false") Boolean replace,
        @QueryParam(ERRORS) String errors,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response) throws IOException;






    @GET
    @Path("{entity:(media|program|group|segment)}/{mid}/transcodingstatus")
    XmlCollection<TranscodeStatus> getTranscodeStatus(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(MID) final String mid
    );


    @GET
    @Path("transcodingstatuses")
    XmlCollection<TranscodeStatus> getTranscodeStatusForBroadcaster(
        @QueryParam("from") final Instant maxAge,
        @QueryParam("status") @DefaultValue("RUNNING") final  TranscodeStatus.Status status,
        @QueryParam(MAX) @DefaultValue("20") final Integer max


    );


    @POST
    @Path("{entity:(media|program|group|segment)}/{mid}/itemize")
    ItemizeResponse itemize(
        @PathParam(ENTITY) @DefaultValue("media") final String entity,
        @PathParam(MID) String mid,
        @Context HttpServletRequest request,
        ItemizeRequest itemizeRequest
    );


}


