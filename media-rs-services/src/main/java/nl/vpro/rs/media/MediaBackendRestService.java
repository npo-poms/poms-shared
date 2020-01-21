package nl.vpro.rs.media;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.search.MediaForm;
import nl.vpro.domain.media.search.MediaList;
import nl.vpro.domain.media.search.MediaListItem;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.update.*;
import nl.vpro.domain.media.update.action.MoveAction;
import nl.vpro.domain.media.update.collections.XmlCollection;
import nl.vpro.domain.subtitles.StandaloneCue;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesId;
import nl.vpro.domain.subtitles.SubtitlesType;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;
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
    String LOOKUP_CRID = "lookupcrid";
    String STEAL_CRIDS = "stealcrids";

    String IMAGE_METADATA = "imageMetadata";
    String OWNER = "owner";


    String VALIDATE_INPUT = "validateInput";
    String FOLLOW_DESCRIPTION = "validateInput";
    String VALIDATE_INPUT_DESCRIPTION = "If true, the body will be validated against the XSD first";
    String ERRORS_DESCRIPTION = "An optional email address to which errors could be mailed if they occur asynchronously";
    String LOOKUP_CRID_DESCRIPTION = "When set to false, possible CRID's in the update will not be used to look up the media object. When set to true, a MID cannot be created beforehand, since this might not be needed. ";

    String STEAL_CRIDS_DESCRIPTION = "When set to true, and you submit an object with both crid and mid (or you used lookupcrid=false, and generate a mid), and the crid existed already for a different mid, then this crid will be (if allowed) removed from the old object ";
    String IMAGE_METADATA_DESCRIPTION = "When set to true, the image backend server will try to fill in missing image metata automaticly, using several external API's";
    String OWNER_DESCRIPTION = "if your account has sufficient right, you may get and post with a differrent owner type than BROADCASTER";




    String ENCRYPTION = "encryption";
    String PRIORITY   = "priority";
    String LOG        = "log";
    String FILE_NAME = "fileName";


    @POST
    @Path("find")
    MediaList<MediaListItem> find(
        MediaForm form,
        @QueryParam("writable") @DefaultValue("false") boolean writable,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id:.*}")
    MediaUpdate<?> getMedia(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    ) throws IOException;


    @GET
    @Path("/exists/{mid:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    boolean exists(
        @Encoded @PathParam(MID) String mid,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response
    );


    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id:.*}")
    @Produces(MediaType.WILDCARD)
    Response deleteMedia(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id:.*}/full")
    MediaObject getFullMediaObject(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|segment|program|group)}")
    @Produces(MediaType.WILDCARD)
    Response update(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @XopWithMultipartRelated MediaUpdate<?> update,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(LOOKUP_CRID) @DefaultValue("true") Boolean lookupcrid,
        @QueryParam(STEAL_CRIDS) @DefaultValue("false") AssemblageConfig.Steal stealcrids,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput,
        @QueryParam(IMAGE_METADATA) @DefaultValue("false") Boolean imageMetadata,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|segment)}/{id:.*}/location")
    @Produces(MediaType.WILDCARD)
    Response addLocation(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        LocationUpdate location,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput
    );

    @DELETE
    @Path("{entity:(media|program|segment)}/{id:.*}/location/{locationId}")
    @Produces(MediaType.WILDCARD)
    Response removeLocation(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @PathParam("locationId") final String locationId,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    );

    @GET
    @Path("{entity:(media|program|segment)}/{id}/locations")
    XmlCollection<LocationUpdate> getLocations(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id:.*}/image")
    @Produces(MediaType.WILDCARD)
    Response addImage(
        ImageUpdate imageUpdate,
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput,
        @QueryParam(IMAGE_METADATA) @DefaultValue("false") Boolean imageMetadata,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    );


    @GET
    @Path("{entity:(media|program|group|segment)}/{id:.*}/images")
    XmlCollection<ImageUpdate> getImages(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner

    ) throws IOException;


    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/members")
    MediaUpdateList<MemberUpdate> getGroupMembers(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoSegments entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("20") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id:.*}/members/full")
    MediaList<Member> getFullGroupMembers(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoSegments entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("20") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @PUT
    @Path("{entity:(media|program|group|segment)}/{id:.*}/members")
    @Produces(MediaType.WILDCARD)
    Response moveMembers(
        MoveAction move,
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoSegments entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|group|segment)}/{id:.*}/memberOfs")
    MediaUpdateList<MemberRefUpdate> getMemberOfs(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id:.*}/memberOf")
    @Produces(MediaType.WILDCARD)
    Response addMemberOf(
        MemberRefUpdate memberRefUpdate,
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput
    ) throws IOException;


    @DELETE
    @Path("{entity:(media|program|group|segment)}/{id:.*}/memberOf/{owner:.*}")
    @Produces(MediaType.WILDCARD)
    Response removeMemberOf(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @PathParam("owner") final String owner,
        @QueryParam("number") final Integer number,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @GET
    @Path("group/{id:.*}/episodes")
    MediaUpdateList<MemberUpdate> getGroupEpisodes(
        @Encoded @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("10") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    ) throws IOException;


    @GET
    @Path("group/{id:.*}/episodes/full")
    MediaList<Member> getFullGroupEpisodes(
        @Encoded @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("10") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final String order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @PUT
    @Path("{entity:(media|group)}/{id:.*}/episodes")
    @Produces(MediaType.WILDCARD)
    Response moveEpisodes(
        MoveAction move,
        @PathParam(ENTITY) @DefaultValue("group") final EntityType.Group entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;

    @GET
    @Path("program/{id:.*}/episodeOfs")
    MediaUpdateList<MemberRefUpdate> getEpisodeOfs(
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("program/{id:.*}/episodeOf")
    @Produces(MediaType.WILDCARD)
    Response addEpisodeOf(
        MemberRefUpdate memberRefUpdate,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput
    ) throws IOException;


    @DELETE
    @Path("program/{id:.*}/episodeOf/{owner}")
    @Produces(MediaType.WILDCARD)
    Response removeEpisodeOf(
        @Encoded @PathParam(ID) final String id,
        @Encoded @PathParam("owner") final String owner,
        @QueryParam("number") final Integer number,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @DELETE
    @Path("program/{id:.*}/segment/{segmentId}")
    @Produces(MediaType.WILDCARD)
    Response removeSegment(
        @Encoded @PathParam(ID) final String id,
        @Encoded @PathParam("segmentId") final String segment,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors
    ) throws IOException;


    @GET
    @Path("subtitles/{mid:.*}/{language}/{type}")
    @Produces({VTT, TT888, SRT, APPLICATION_XML})
    Subtitles getSubtitles(
        @Encoded @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    );


    @GET
    @Path("subtitles/{mid:.*}/{language}/{type}/{seq}")
    StandaloneCue getCue(
        @Encoded @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @PathParam("seq") Integer seq,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam("guessOffset") @DefaultValue("false") Boolean guessOffset
    );

    @GET
    @Path("subtitles/{mid:.*}")
    @Wrapped(element = "subtitles", namespace = Xmlns.MEDIA_SUBTITLES_NAMESPACE)
    List<SubtitlesId> getAllSubtitles(
        @Encoded @PathParam(MID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("subtitles/{mid:.*}/{language}/{type}")
    @Consumes({VTT, EBU, TT888, SRT})
    Response setSubtitles(
        @Encoded @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @QueryParam(OFFSET) @DefaultValue("0") Duration offset,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        Subtitles subtitles);

    @POST
    @Path("subtitles/{mid:.*}/{language}/{type}/{offset}")
    Response setSubtitlesOffset(
        @Encoded @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @PathParam("offset") @DefaultValue("0") Duration offset,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors);

    @DELETE
    @Path("subtitles/{mid:.*}/{language}/{type}")
    Response deleteSubtitles(
        @Encoded @PathParam(MID) String mid,
        @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors);

    @GET
    @Path("streamingstatus/{mid:.*}")
    StreamingStatus getStreamingstatus(
        @Encoded @PathParam(MID) String mid,
        @Context HttpServletRequest request
    ) throws IOException, URISyntaxException;

    @GET
    @Path("{entity:(media|program|segment)}/{id:.*}/predictions")
    XmlCollection<PredictionUpdate> getPredictions(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @GET
    @Path("{entity:(media|program|segment)}/{id:.*}/predictions/{platform}")
    PredictionUpdate getPrediction(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @PathParam("platform") final Platform platform,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|segment)}/{id:.*}/predictions")
    Response setPredictions(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        XmlCollection<PredictionUpdate> collection
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|group|segment)}/{id:.*}/predictions/{platform}")
    Response setPrediction(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @PathParam("platform") final Platform platform,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        PredictionUpdate prediction
    ) throws IOException;

    @POST
    @Path("{entity:(media|program|segment)}/{mid:.*}/transcode")
    @Produces(MediaType.TEXT_PLAIN)
    Response transcode(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(MID) final String mid,
        @QueryParam(ERRORS) String errors,
        TranscodeRequest transcodeRequest);

    @POST
    @Path("transcode")
    Response transcode(
        @QueryParam(ERRORS) String errors,
        TranscodeRequest transcodeRequest);

    @POST
    @Path("upload/{mid}/{fileName}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, "video/*", "application/mxf"})
    TranscodeRequest upload(
        @Encoded @PathParam(MID) final String mid,
        @Encoded @PathParam(FILE_NAME) final String fileName,
        InputStream inputStream,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @HeaderParam(HttpHeaders.CONTENT_LENGTH) Long contentLength,
        @QueryParam(LOG) @DefaultValue("false") Boolean log,
        @QueryParam("replace") @DefaultValue("false") Boolean replace,
        @Context HttpServletResponse response) throws IOException;

    @POST
    @Path("upload/{mid}/{encryption}/{priority}/{fileName}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, "video/*", "application/mxf"})
    Response uploadAndTranscode(
        @Encoded @PathParam(MID) final String mid,
        @Encoded @PathParam(ENCRYPTION) final Encryption  encryption,
        @Encoded @PathParam(PRIORITY) final TranscodeRequest.Priority priority,
        @Encoded @PathParam(FILE_NAME) final String fileName,
        InputStream inputStream,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @HeaderParam(HttpHeaders.CONTENT_LENGTH) Long contentLength,
        @QueryParam(LOG) @DefaultValue("false") Boolean log,
        @QueryParam("replace") @DefaultValue("false") Boolean replace,
        @QueryParam(ERRORS) String errors,
        @Context HttpServletResponse response) throws IOException;


    @GET
    @Path("{entity:(media|program|segment)}/{mid:.*}/transcodingstatus")
    XmlCollection<TranscodeStatus> getTranscodeStatus(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(MID) final String mid
    );


    @GET
    @Path("transcodingstatuses")
    XmlCollection<TranscodeStatus> getTranscodeStatusForBroadcaster(
        @QueryParam("from") final Instant since,
        @QueryParam("status") @DefaultValue("RUNNING") final TranscodeStatus.Status status,
        @QueryParam(MAX) @DefaultValue("20") final Integer max
    );


    @POST
    @Path("{entity:(media|program|group|segment)}/{mid:.*}/itemize")
    ItemizeResponse itemize(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(MID) String mid,
        @Context HttpServletRequest request,
        ItemizeRequest itemizeRequest
    );

    /**
     * @since 5.12
     */
    @GET
    @Path("transcodingstatus/{id:.*")
    TranscodeStatus getTranscodeStatus(
        @Encoded @PathParam("id") final String id
    );


}


