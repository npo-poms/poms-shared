package nl.vpro.sourcingservice;

import io.github.yskszk63.jnhttpmultipartformdatabodypublisher.MultipartFormDataBodyPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.*;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.domain.media.*;

import nl.vpro.logging.simple.Level;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.sourcingservice.v1.*;
import nl.vpro.util.FileSizeFormatter;
import nl.vpro.util.InputStreamChunk;


/**
 * Straight forward client for some relevant parts of the
 * sourcing services (
 * '<a href="https://test.sourcing-audio.cdn.npoaudio.nl/api/documentation">audio</a>'
 * '<a href="https://test.sourcing-video.cdn.npoaudio.nl/api/documentation">video</a>').
 *  <p>
 * <a href='https://sourcing-service.acc.metadata.bijnpo.nl/docs#endpoints-POSTapi-ingest--assetIngest--multipart-assetonly'>version 2</a>
 *
 */
@Log4j2
public abstract class AbstractSourcingServiceImpl implements SourcingService {

    /***
     * Field in the multipart body
     */
    private static final String CHECKSUM     = "checksum";

    /***
     * Field in the multipart body
     */
    private static final String FILE_SIZE    = "file_size";
    private static final String FILE_CHUNK   = "file_chunk";
    private static final String UPLOAD_PHASE = "upload_phase";


    private static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.registerModule( new JavaTimeModule());
    }

    private final HttpClient client = HttpClient
        .newBuilder()
        .version(HttpClient.Version.HTTP_1_1) // GOAWAY trouble?
        .build();

    private String baseUrl;

    private String multipartPart = "ingest/%s/multipart-assetonly";

    @Nullable
    private final String callbackBaseUrl; // this seems not to get called?
    private final String token;
    private final int chunkSize;
    private final String defaultEmail;

    private final MeterRegistry meterRegistry;

    /**
     * Whether to determin checksum of incoming stream
     */


    int version = 2;

    AbstractSourcingServiceImpl(
        String baseUrl,
        @Nullable String callbackBaseUrl,
        String token,
        int chunkSize,
        String defaultEmail,
        MeterRegistry meterRegistry,
        int version) {
        this.baseUrl = baseUrl.replaceAll("([^/])$","$1/");

        this.callbackBaseUrl = callbackBaseUrl;
        this.token = token;
        this.chunkSize = chunkSize;
        this.defaultEmail = defaultEmail;
        this.meterRegistry = meterRegistry;
        this.version = version;
    }


    protected abstract String getFileName(String mid);


    @Override
    public UploadResponse upload(
        SimpleLogger logger,
        String mid,
        @Nullable Restrictions restrictions,
        long fileSize,
        InputStream inputStream,
        @Nullable String errors
    ) throws IOException, InterruptedException, SourcingServiceException {
        return switch (version) {
            case 1 ->
                uploadv1(logger, mid, restrictions, fileSize, null, inputStream, errors, SourcingService.phaseLogger(logger));
            case 2 ->
                uploadv2(logger, mid, restrictions, inputStream);
            default -> throw new IllegalArgumentException("Unknown version " + version);
        };
    }


   @SneakyThrows
   protected UploadResponse uploadv1(
       SimpleLogger logger,
       final String mid,
       @Nullable Restrictions restrictions,
       final long fileSize,
       byte @Nullable[]  checksum,
       InputStream inputStream,
       String errors,
       Consumer<Phase> phase) throws SourcingServiceException {

       final AtomicLong uploaded = new AtomicLong(0);


       final InputStream finalInputStream = inputStream;
       try (finalInputStream) {
           // this is not needed (as I've tested)
           //ingest(logger, mid, getFileName(mid), restrictions);
           phase.accept(Phase.START);
           logger.info("Uploading {} B", fileSize);
           uploadStart(logger, mid, fileSize, checksum, errors, restrictions);
           phase.accept(Phase.UPLOAD);
           final AtomicInteger partNumber = new AtomicInteger(1);
           while (uploaded.get() < fileSize) {
               uploadChunk(logger, mid, inputStream, uploaded, restrictions, fileSize, partNumber);
           }
       }
       assert uploaded.get() == fileSize;


       phase.accept(Phase.FINISH);

       return uploadFinish(logger, mid, uploaded, restrictions);
   }

    @SneakyThrows
   protected UploadResponse uploadv2(
       SimpleLogger logger,
       final String mid,
       @Nullable Restrictions restrictions,
       final InputStream inputStream) throws SourcingServiceException {


        if (restrictions != null) {

        }

        HttpRequest.Builder uploadRequestBuilder = uploadRequestBuilder(mid);


        final MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher();
        body.addStream("file",  mid, () -> inputStream);

        HttpRequest.Builder post = uploadRequestBuilder
            .header("Content-Type", body.contentType())
            .POST(body);

        HttpResponse<String> send = client.send(post.build(), HttpResponse.BodyHandlers.ofString());


//        assert uploaded.get() == fileSize;

        JsonNode bodyNode = MAPPER.readTree(send.body());
        logger.info("{} {}", mid, bodyNode);

       return null;
   }


    @Override
    public Optional<StatusResponse> status(String mid) throws IOException, InterruptedException {
        final HttpRequest statusRequest = statusRequest(mid);
        final HttpResponse<String> statusResponse = client.send(statusRequest, HttpResponse.BodyHandlers.ofString());
        meter("status", statusResponse);
        if (statusResponse.statusCode() >= 400 && statusResponse.statusCode() < 500) {
            return Optional.empty();
        }
        if (statusResponse.statusCode() > 299) {
            throw new IllegalArgumentException(statusRequest + ":" + statusResponse.statusCode() + ":" +  statusResponse.body());
        }
        return Optional.of(
            MAPPER.readValue(statusResponse.body(), StatusResponse.class)
        );
    }

    @Override
    public DeleteResponse delete(String mid, int daysBeforeHardDelete) throws IOException, InterruptedException {
        final HttpRequest statusRequest = deleteRequest(mid, daysBeforeHardDelete);
        final HttpResponse<String> statusResponse = client.send(statusRequest, HttpResponse.BodyHandlers.ofString());
        meter("status.delete", statusResponse);
        if (statusResponse.statusCode() > 299) {
            throw new IllegalArgumentException(statusRequest + ":" + statusResponse.statusCode() + ":" +  statusResponse.body());
        }
        return MAPPER.readValue(statusResponse.body(), DeleteResponse.class);
    }

    private void ingest(SimpleLogger logger, String mid, String filename, Restrictions restrictions) throws IOException, InterruptedException {

        final ObjectNode metaData = metadata(logger, mid, filename, restrictions);

        HttpRequest ingestRequest = ingest(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(metaData)));
        HttpResponse<byte[]> ingest = client.send(ingestRequest,
            HttpResponse.BodyHandlers.ofByteArray());
        meter("ingest", ingest);

        if (ingest.statusCode() > 299) {
            throw new IllegalArgumentException(ingestRequest + ":" + ingest.statusCode() + ":" + new String(ingest.body()));
        }
        IngestResponse response = MAPPER.readValue(ingest.body(), IngestResponse.class);
        logger.info("ingest {}", response);
    }

    @Deprecated
    private ObjectNode metadata(SimpleLogger logger, String mid, String filename, Restrictions restrictions) {
         final ObjectNode metaData = MAPPER.createObjectNode();
        metaData.put("mid", mid);
        String callbackUrl = getCallbackUrl(mid);
        if (StringUtils.isNotBlank(callbackUrl)) {
            metaData.put("callback_url", callbackUrl);
        }

        if (filename != null) {
            // I don't get the point of this
            metaData.put("filename", filename);
        }

        if (restrictions.getGeoRestriction() != null && restrictions.getGeoRestriction().getPlatform() == Platform.INTERNETVOD) {
            GeoRestriction geoRestriction = restrictions.getGeoRestriction();
            if (geoRestriction.isUnderEmbargo()) {
                if (geoRestriction.willBePublished()) {
                    logger.warn("Specified geo restriction {} is under embargo, but will not be. This is not yet supported", geoRestriction);
                } else {
                    logger.info("Specified geo restriction {} is under embargo", geoRestriction);
                }
            } else {
                if (geoRestriction.willBeUnderEmbargo()) {
                    logger.warn("Specified geo restriction {} will be under embargo. This is not yet supported", geoRestriction);
                }
                logger.debug("Sending with geo restriction {}", restrictions.getGeoRestriction().getRegion().name());
                metaData.put("geo_restriction", restrictions.getGeoRestriction().getRegion().name());
            }
        }
        if (restrictions.getAgeRating() != null && restrictions.getAgeRating() != AgeRating.ALL) {
            logger.info("Specified age rating {} is not supported for audio, and will be ignored", restrictions.getAgeRating());
        }
        return metaData;
    }


    @Deprecated
    private void uploadStart(
        final SimpleLogger logger,
        final String mid,
        final long fileSize,
        final byte@Nullable[] checksum,
        final @Nullable String errors,
        final Restrictions restrictions) throws IOException, InterruptedException {

        final String email = Optional.ofNullable(errors).orElse(defaultEmail);
        final MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add(UPLOAD_PHASE, "start");
        if (email != null) {
            body.add("email", email);
        }
        if (checksum != null) {
            String checksumAsString = new BigInteger(1, checksum).toString(16);
            logger.info("Checksum {}", checksumAsString);
            body.add(CHECKSUM, checksumAsString);
        }

        addRegion(logger, body, restrictions);

        body.add(FILE_SIZE, String.valueOf(fileSize));
        HttpRequest multipart = multipart(mid, body);
        HttpResponse<String> start = client.send(multipart, HttpResponse.BodyHandlers.ofString());
        meter("multipart", start);

        if (start.statusCode() > 299) {
            throw new IllegalArgumentException(multipart + ": " + start.statusCode() + ":" + start.body());
        }
        JsonNode node = MAPPER.readTree(start.body());
        String callBackUrl = getCallbackUrl(mid);
        if (StringUtils.isNotBlank(callBackUrl)) {
            callBackUrl = ", %s".formatted(callBackUrl).replaceAll("^(.*://)(.*?:).*?@", "$1$2xxxx@");
        }
        boolean  success = start.statusCode() >= 200 && start.statusCode() < 300 ;
        logger.log(success ? Level.INFO : Level.ERROR, "start: {} ({}) filesize: {},  response: {}, email={}{}",
                node.get("status").textValue(),
                start.statusCode(),
                FileSizeFormatter.DEFAULT.format(fileSize),
                node.get("response").textValue(),
                email,
                (callBackUrl == null ? "" : callBackUrl)
            );
        if (! success){
            throw new RuntimeException();
        }
    }

    private void addRegion(SimpleLogger logger, MultipartFormDataBodyPublisher body, Restrictions restrictions) {
        if (restrictions != null && restrictions.getGeoRestriction() != null && restrictions.getGeoRestriction().inPublicationWindow()) {
            if (Arrays.stream(Region.RESTRICTED_REGIONS).anyMatch(r -> r == restrictions.getGeoRestriction().getRegion())) {
                logger.debug("Sending with region {}", restrictions.getGeoRestriction().getRegion().name());
                body.add("region", restrictions.getGeoRestriction().getRegion().name());
            } else {
                logger.warn("Ignored region {}", restrictions.getGeoRestriction());
            }
        }
    }

    private void uploadChunk(
        final SimpleLogger logger,
        final String mid,
        final InputStream inputStream,
        final AtomicLong uploaded,
        final Restrictions restrictions,
        final long total,
        final AtomicInteger partNumber) throws IOException, InterruptedException, SourcingServiceException {
        try (InputStreamChunk chunkStream = new InputStreamChunk(chunkSize, inputStream)) {
            MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
                .add(UPLOAD_PHASE, "transfer")
                .addStream(
                    FILE_CHUNK,
                    "part" + (partNumber.getAndIncrement()), () -> chunkStream);

            addRegion(logger, body, restrictions);
            HttpRequest transferRequest = multipart(mid, body);
            HttpResponse<String> transfer = client.send(
                transferRequest, HttpResponse.BodyHandlers.ofString());
            meter("transfer", transfer);

            long currentCount = uploaded.addAndGet(chunkStream.getCount());
            final JsonNode node = MAPPER.readTree(transfer.body());
            final boolean  success = transfer.statusCode() >= 200 && transfer.statusCode() < 300 ;

            final String status = Optional.ofNullable(node.get("status")).map(JsonNode::textValue).orElse("<no status>");
            logger.log(success ? Level.INFO : Level.ERROR, "{} transfer: {} ({}) {}/{}",
                mid,
                status,
                transfer.statusCode(),
                FileSizeFormatter.DEFAULT.format(currentCount),
                FileSizeFormatter.DEFAULT.format(total)
            );
            if (! success){
                throw new SourcingServiceException(transfer.statusCode(), node);
            }
        }
    }

    private UploadResponse uploadFinish(
        SimpleLogger logger,
        String mid,
        AtomicLong uploaded,
        Restrictions restrictions) throws IOException, InterruptedException {
        final MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add(UPLOAD_PHASE, "finish");



        addRegion(logger, body, restrictions);

        final HttpResponse<String> finish = client.send(multipart(mid, body), HttpResponse.BodyHandlers.ofString());
        meter("finish", finish);

        final JsonNode node = MAPPER.readTree(finish.body());
        final String status = Optional.ofNullable(node.get("status")).map(JsonNode::textValue).orElse("<no status>");
        final String response = Optional.ofNullable(node.get("response")).map(JsonNode::textValue).orElse("<no response>");

        final boolean  success = finish.statusCode() >= 200 && finish.statusCode() < 300 ;
        logger.log(success?  Level.INFO : Level.ERROR, "{} finish: {} ({}) {} {}", mid, status, finish.statusCode(), FileSizeFormatter.DEFAULT.format(uploaded), MAPPER.writeValueAsString(node));
        JsonNode bodyNode = MAPPER.readTree(finish.body());
        return null;
        /*return new UploadResponse(
            mid,
            finish.statusCode(),
            status,
            response,
            uploaded.get()
        );*/
    }

    protected abstract String implName();

    protected void meter(String name, HttpResponse<?> response) {
        meterRegistry.counter("sourcing." + implName() + "." + name , "status", String.valueOf(response.statusCode())).increment();
    }

    @Nullable
    protected String getCallbackUrl(String mid) {
        return callbackBaseUrl == null ? null : callbackBaseUrl.formatted(mid);
    }

    protected HttpRequest multipart(String mid, MultipartFormDataBodyPublisher body) {
        return request(pathForIngestMultipart(mid))
            .header("Content-Type", body.contentType())
            .POST(body)
            .build();
    }


    protected HttpRequest ingest(HttpRequest.BodyPublisher body) {
        return request("ingest")
            .header("Content-Type", "application/json")
            .POST(body)
            .build();
    }


    protected HttpRequest statusRequest(String mid) {
        return request("ingest/" + mid + "/status")
            .GET().build();
    }



    protected HttpRequest.Builder uploadRequestBuilder(String mid) {
        return request("ingest/" + mid + "/upload");
    }


    @SneakyThrows
    protected HttpRequest deleteRequest(String mid, int daysBeforeHardDelete) {
            ObjectNode node = MAPPER.createObjectNode();
            node.put("days_before_hard_delete", daysBeforeHardDelete);
            return request("ingest/" + mid + "/delete")
                .POST(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(node)))
                .build();
    }

    protected HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder()
            .header("Authorization", "Bearer " + token)
            .uri(URI.create(forPath(path)));
    }

    String pathForIngestMultipart(String mid) {
        return multipartPart.formatted(mid);
    }

    String forPath(String path) {
        return baseUrl + "api/" + path;
    }


    @ManagedAttribute
    @Override
    public String getUploadString() {
        return forPath(pathForIngestMultipart("%s"));
    }

    @ManagedAttribute
    public String getBaseUrl() {
        return baseUrl;
    }

    @ManagedAttribute
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @ManagedAttribute
    public String getMultipartPart() {
        return multipartPart;
    }

    @ManagedAttribute
    public void setMultipartPart(String multipartPart) {
        this.multipartPart = multipartPart;
    }
}
