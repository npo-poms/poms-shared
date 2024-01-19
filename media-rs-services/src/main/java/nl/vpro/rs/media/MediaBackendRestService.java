package nl.vpro.rs.media;

import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.*;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;
import org.meeuw.i18n.regions.validation.Language;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.search.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.update.*;
import nl.vpro.domain.media.update.action.MoveAction;
import nl.vpro.domain.media.update.collections.XmlCollection;
import nl.vpro.domain.subtitles.*;
import nl.vpro.jmx.Description;
import nl.vpro.poms.shared.Headers;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static nl.vpro.api.rs.subtitles.Constants.*;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@Path("/media")
@Consumes({MediaType.APPLICATION_XML, MultipartConstants.MULTIPART_RELATED})
@Produces(MediaType.APPLICATION_XML)
@Description("Services on https://api.poms.omroep.nl/media")
public interface MediaBackendRestService {

    String VALIDATION_WARNING_HEADER = Headers.NPO_VALIDATION_WARNING_HEADER;
    String VALIDATION_ERROR_HEADER = Headers.NPO_VALIDATION_ERROR_HEADER;

    // some common query and path parameters
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
    String DELETES = "deletes";
    String PUBLISH = "publish";

    String VALIDATE_INPUT = "validateInput";

    String AVOID_PARSING = "avoidParsing";



    // related to transcoding, and uploading.
    String ENCRYPTION = "encryption";
    String PRIORITY   = "priority";
    String LOG        = "log";
    String FILE_NAME = "fileName";


    // some descriptions for common query and path parameters
    String MID_DESCRIPTION = "The 'mediaobject id'. For program sometimes referred to as 'prid' for series/seasons as 'srid'";
    String ID_DESCRIPTION = "The 'mediaobject id'. May be a MID, a database id, or a crid";

    /**
     * Like {@link #ID_DESCRIPTION}, but without the 'database id' part.
     * @since 7.7.0
     */
    String ID_DESCRIPTION_2 = "The 'mediaobject id'. May be a MID or a crid";

    String FOLLOW_DESCRIPTION = "Whether 'merges' need to be implicitly followed. If your ask or do an operation on an object that is merged to another object, it will do it on that other object";
    String VALIDATE_INPUT_DESCRIPTION = "If true, the body will be validated during parsing, against the XSD. If this is false, your input will still be validated, but using so-called java bean validation only. This will give no line and column number information, but is otherwise more complete.";
    String ERRORS_DESCRIPTION =
    """
    An optional address to which errors could be sent if they occur asynchronously. These errors may relate to authorization, or to database related problems.
    This is a comma separated list of email addresses or callback URLs (this new in 7.10). If it is an email-address, the error will marked up in an amil and sent do that adres. If it is a callback URL, the error will be sent as a POST to that URL, with a text/plain body containing the error message.
    """;
    String LOOKUP_CRID_DESCRIPTION = "When set to false, possible CRIDs in the update will not be used to look up the media object. When set to true, a MID cannot be created beforehand, since this might not be needed.";
    String STEAL_CRIDS_DESCRIPTION = "When set to true, and you submit an object with both CRID and mid (or you used lookupcrid=false, and generate a mid), and the CRID existed already for a different mid, then this CRID will be (if allowed) removed from the old object.";
    String IMAGE_METADATA_DESCRIPTION = "When set to true, the image backend server will try to fill in missing image metadata automatically, using several external APIs.";
    String OWNER_DESCRIPTION = "if your account has sufficient rights, you may get and post with a different owner type than BROADCASTER";

    String DELETES_DESCRIPTION = "also include members/episodes that are deleted (if possible, you may lack rights)";

    String PUBLISH_DESCRIPTION = "if you set this to true, then the required change will be published immediately";

    String AVOID_PARSING_DESCRIPTION = "if you request the subtitles in the same format as they were originally supplied, they will be returned untouched as much as possible";

    String LANGUAGE_DESCRIPTION = "Language code (ISO 639), possible postfix with region and variants. ";

    String LOG_DESCRIPTION = "Some calls that could need some time to complete (e.g. because uploading large files) have this query parameter. If set to true, the server may stream some logging back to you during the process";



    @POST
    @Path("find")
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    @Consumes({APPLICATION_XML, APPLICATION_JSON})
    MediaList<MediaListItem> find(
        @Valid MediaForm form,
        @QueryParam("writable") @DefaultValue("false") boolean writable,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput
    );

    @GET
    @Path("{entity:(media|program|group|segment)}/{id:.*}")
    @Produces({APPLICATION_XML, APPLICATION_JSON})
    MediaUpdate<?> getMedia(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    );

    @GET
    @Path("/exists/{mid:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    boolean exists(
        @Encoded @PathParam(MID) String mid,
        @QueryParam("registered") @DefaultValue("true") Boolean registered,
        @Context HttpServletResponse response);

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
    );

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
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner,
        @QueryParam(PUBLISH) @DefaultValue("false") Boolean publish

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
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    );

    @DELETE
    @Path("{entity:(media|program|segment)}/{id:.*}/location/{locationId}")
    @Produces(MediaType.WILDCARD)
    Response removeLocation(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @PathParam("locationId") final String locationId,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    );

    @GET
    @Path("{entity:(media|program|segment)}/{id}/locations")
    XmlCollection<LocationUpdate> getLocations(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner
    );

    @POST
    @Path("{entity:(media|program|group|segment)}/{id:.*}/image")
    @Produces(MediaType.WILDCARD)
    Response addImage(
        @Valid ImageUpdate imageUpdate,
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        @QueryParam(VALIDATE_INPUT) @DefaultValue("false") Boolean validateInput,
        @QueryParam(IMAGE_METADATA) @DefaultValue("false") Boolean imageMetadata,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner,
        @QueryParam(PUBLISH) @DefaultValue("false") Boolean publish

    );

    @GET
    @Path("{entity:(media|program|group|segment)}/{id:.*}/images")
    XmlCollection<ImageUpdate> getImages(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.AllMedia entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner

    );

    @GET
    @Path("{entity:(media|program|group|segment)}/{id}/members")
    MediaUpdateList<MemberUpdate> getGroupMembers(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoSegments entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("20") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final Pager.Direction order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner,
        @QueryParam(DELETES) Boolean  deleted
    );

    @GET
    @Path("{entity:(media|program|group|segment)}/{id:.*}/members/full")
    MediaList<Member> getFullGroupMembers(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoSegments entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("20") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final Pager.Direction order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(DELETES) Boolean  deleted
    );

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
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(DELETES) Boolean  deleted
    );

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
        @QueryParam(ORDER) @DefaultValue("ASC") final Pager.Direction order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(OWNER) @DefaultValue("BROADCASTER") OwnerType owner,
        @QueryParam(DELETES) Boolean  deleted
    );

    @GET
    @Path("group/{id:.*}/episodes/full")
    MediaList<Member> getFullGroupEpisodes(
        @Encoded @PathParam(ID) final String id,
        @QueryParam(OFFSET) @DefaultValue("0") final Long offset,
        @QueryParam(MAX) @DefaultValue("10") final Integer max,
        @QueryParam(ORDER) @DefaultValue("ASC") final Pager.Direction order,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(DELETES) Boolean  deleted
    );

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
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(DELETES) Boolean  deleted
    );

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
        @Language @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam("avoidParsing") @DefaultValue("false") Boolean avoidParsing

    );

    @GET
    @Path("subtitles/{mid:.*}/{language}/{type}/{seq}")
    StandaloneCue getCue(
        @Encoded @PathParam(MID) String mid,
        @Language @PathParam(LANGUAGE) Locale language,
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
        @Language @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @QueryParam(OFFSET) @DefaultValue("0") Duration offset,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors,
        Subtitles subtitles);

    @POST
    @Path("subtitles/{mid:.*}/{language}/{type}/{offset}")
    Response setSubtitlesOffset(
        @Encoded @PathParam(MID) String mid,
        @Language @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @PathParam("offset") @DefaultValue("0") Duration offset,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors);

    @DELETE
    @Path("subtitles/{mid:.*}/{language}/{type}")
    Response deleteSubtitles(
        @Encoded @PathParam(MID) String mid,
        @Language @PathParam(LANGUAGE) Locale language,
        @PathParam(TYPE) SubtitlesType type,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges,
        @QueryParam(ERRORS) String errors);

    @GET
    @Path("streamingstatus/{mid:.*}")
    StreamingStatus getStreamingstatus(
        @Encoded @PathParam(MID) String mid,
        @Context HttpServletRequest request
    );

    @GET
    @Path("{entity:(media|program|segment)}/{id:.*}/predictions")
    XmlCollection<PredictionUpdate> getPredictions(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    );

    @GET
    @Path("{entity:(media|program|segment)}/{id:.*}/predictions/{platform}")
    PredictionUpdate getPrediction(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(ID) final String id,
        @PathParam("platform") final Platform platform,
        @QueryParam(FOLLOW) @DefaultValue("true") Boolean followMerges
    );

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
        @Context HttpServletResponse response,
        TranscodeRequest transcodeRequest);

    @POST
    @Path("transcode")
    Response transcode(
        @QueryParam(ERRORS) String errors,
        @Context HttpServletResponse response,
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
        @QueryParam("uploadFirst") @DefaultValue("false") Boolean uploadFirst,
        @Context HttpServletResponse response) throws IOException;

    @POST
    @Path("upload/{mid}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, "video/*", "application/mxf"})
    TranscodeRequest upload(
        @Encoded @PathParam(MID) final String mid,
        InputStream inputStream,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @HeaderParam(HttpHeaders.CONTENT_LENGTH) Long contentLength,
        @QueryParam(LOG) @DefaultValue("false") Boolean log,
        @QueryParam("replace") @DefaultValue("false") Boolean replace,
        @QueryParam("uploadFirst") @DefaultValue("false") Boolean uploadFirst,
        @Context HttpServletResponse response) throws IOException;

    /**
     * @since 7.5
     */
    @POST
    @Path("upload/{audioMid}")
    @Consumes({"audio/*"})
    @Produces(APPLICATION_XML)
    UploadResponse uploadAudio(
        @Encoded @PathParam("audioMid") final String mid,
        InputStream inputStream,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @HeaderParam(HttpHeaders.CONTENT_LENGTH) Long contentLength,
        @QueryParam(LOG) @DefaultValue("false") Boolean log,
        @QueryParam(ERRORS) String errors,
        @Context HttpServletResponse response) throws IOException, InterruptedException;


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
        @QueryParam("uploadFirst") @DefaultValue("false") Boolean uploadFirst,
        @QueryParam(ERRORS) String errors,
        @Context HttpServletResponse response) throws IOException;

    @POST
    @Path("upload/{mid}/{encryption}/{priority}")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, "video/*", "application/mxf"})
    Response uploadAndTranscode(
        @Encoded @PathParam(MID) final String mid,
        @Encoded @PathParam(ENCRYPTION) final Encryption  encryption,
        @Encoded @PathParam(PRIORITY) final TranscodeRequest.Priority priority,
        InputStream inputStream,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @HeaderParam(HttpHeaders.CONTENT_LENGTH) Long contentLength,
        @QueryParam(LOG) @DefaultValue("false") Boolean log,
        @QueryParam("replace") @DefaultValue("false") Boolean replace,
        @QueryParam("uploadFirst") @DefaultValue("false") Boolean uploadFirst,
        @QueryParam(ERRORS) String errors,
        @Context HttpServletResponse response) throws IOException;


    /**
     * @since 7.5
     */
    @POST
    @Operation(tags = {"media",   "streams"},
        summary = "Upload a video",
        description = "This is not supported.",
        hidden = true
    )
    @Path("upload/{mid:[^/]+?}/passthrough")
    @Consumes({MediaType.APPLICATION_OCTET_STREAM, "video/*", "application/mxf"})
    UploadResponse uploadVideo(
        @PathParam(MID) final String mid,
        InputStream inputStream,
        @HeaderParam(HttpHeaders.CONTENT_TYPE) String contentType,
        @HeaderParam(HttpHeaders.CONTENT_LENGTH) Long contentLength,
        @QueryParam(LOG) @DefaultValue("false") Boolean log,
        @QueryParam(ERRORS) String errors,
        @Context HttpServletResponse response) throws IOException, InterruptedException;

    @GET
    @Path("{entity:(media|program|segment)}/{mid:.*}/transcodingstatus")
    XmlCollection<TranscodeStatus> getTranscodeStatus(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(MID) final String mid,
        @Context HttpServletResponse response
    );

    @GET
    @Path("transcodingstatuses")
    XmlCollection<TranscodeStatus> getTranscodeStatusForBroadcaster(
        @QueryParam("from") final Instant since,
        @QueryParam("status") @DefaultValue("RUNNING") final TranscodeStatus.Status status,
        @QueryParam(MAX) @DefaultValue("20") final Integer max,
        @Context HttpServletResponse response
    );

    @POST
    @Path("{entity:(media|program|group|segment)}/{mid:.*}/itemize")
    ItemizeResponse itemize(
        @PathParam(ENTITY) @DefaultValue("media") final EntityType.NoGroups entity,
        @Encoded @PathParam(MID) String mid,
        @Context HttpServletRequest request,
        @Context HttpServletResponse response,
        ItemizeRequest itemizeRequest
    );

    /**
     * @since 5.12
     */
    @GET
    @Path("transcodingstatus/{id:.*}")
    TranscodeStatus getTranscodeStatus(
        @Encoded @PathParam("id") final String id,
        @Context HttpServletResponse response
    );

    @GET
    @Path("mid/{type:.*}")
    @Produces(MediaType.TEXT_PLAIN)
    String createMid(
        @PathParam("type") nl.vpro.domain.media.MediaType mediaType,
        @QueryParam("register") Boolean register);


}


