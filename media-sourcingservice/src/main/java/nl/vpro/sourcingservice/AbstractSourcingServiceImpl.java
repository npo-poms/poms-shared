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
import java.util.function.Supplier;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.mime.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.Level;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.sourcingservice.v1.IngestResponse;
import nl.vpro.util.*;

import static nl.vpro.i18n.MultiLanguageString.en;


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
    @Deprecated
    private static final String CHECKSUM     = "checksum";

    /***
     * Field in the multipart body
     */
    @Deprecated
    private static final String FILE_SIZE    = "file_size";
    @Deprecated
    private static final String FILE_CHUNK   = "file_chunk";
    @Deprecated
    private static final String UPLOAD_PHASE = "upload_phase";

    private static final String FILE   = "file";


    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectReader V2READER;
    private static final ObjectReader JSONREADER;
    static {
        MAPPER.registerModule( new JavaTimeModule());
        V2READER = MAPPER.readerFor(nl.vpro.sourcingservice.v2.StatusResponse.class).with(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);
        JSONREADER = MAPPER.readerFor(ObjectNode.class).with(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);

    }

    private final HttpClient client = HttpClient
        .newBuilder()
        .version(HttpClient.Version.HTTP_1_1) // GOAWAY trouble?
        .build();



    @Deprecated
    private String multipartPart = "ingest/%s/multipart-assetonly";

    private final Supplier<Configuration> configuration;

    private final MeterRegistry meterRegistry;

    AbstractSourcingServiceImpl(
        Supplier<Configuration> configuration,
        MeterRegistry meterRegistry) {

        this.configuration = configuration;
        this.meterRegistry = meterRegistry;
    }


    protected  String getFileName(String mid, String mimeType) {
        String ext;
        final String defaultExt = "." + defaultFormat().name().toLowerCase();
        try {

            MimeType tika = MimeTypes.getDefaultMimeTypes().getRegisteredMimeType(mimeType);
            if (tika== null) {
                tika= MimeTypes.getDefaultMimeTypes().forName(mimeType);
            }
            if (!tika.getExtensions().isEmpty()) {
                if (tika.getExtensions().contains(defaultExt)) {
                    ext = defaultExt;
                } else {
                    ext = tika.getExtension();
                }
            } else {
                ext = defaultExt;
            }
        } catch (MimeTypeException e) {
            log.warn(e.getMessage(), e);
            ext = defaultExt;
        }
        return mid + ext;
    }

    protected abstract AVFileFormat defaultFormat();


    @Override
    public UploadResponse upload(
        SimpleLogger logger,
        String mid,
        @Nullable Restrictions restrictions,
        long fileSize,
        String contentType,
        InputStream inputStream,
        @Nullable String errors
    ) throws IOException, InterruptedException, SourcingServiceException {
        return switch (configuration.get().version()) {
            case 1 ->
                uploadv1(logger, mid, restrictions, fileSize, null, inputStream, errors, SourcingService.phaseLogger(logger));
            case 2 ->
                uploadv2(logger, mid, contentType, inputStream);
            default -> throw new IllegalArgumentException("Unknown version " + configuration.get().version());
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
           logger.info(en("Uploading %d B")
               .nl("Uploaden %d B")
               .formatted(fileSize)
           );
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
       final String contentType,
       final InputStream inputStream) throws SourcingServiceException {

        final HttpRequest.Builder uploadRequestBuilder = uploadRequestBuilder(mid);

        final MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher();
        final String fileName = getFileName(mid, contentType);
        body.addChannel(FILE,  fileName,
            () -> WrappedReadableByteChannel
                .builder()
                .inputStream(inputStream)
                .batchSize((long) configuration.get().chunkSize())
                .consumer(l -> logger.info(
                    en("Uploaded %s to %s")
                        .nl("Geüpload %s naar %s")
                        .formatted(FileSizeFormatter.DEFAULT.format(l), configuration.get().cleanBaseUrl()))
                )
                .build(),
            contentType
        );

        final HttpRequest post = uploadRequestBuilder
            .header("Content-Type", body.contentType())
            .POST(body)

            .build();

        logger.info(en("Posting {} for {} to {}")
            .nl("Posting {} voor {} naar {}").slf4jArgs(fileName, mid, post.uri()));

        final HttpResponse<String> send = client.send(post, HttpResponse.BodyHandlers.ofString());


        final boolean  success = send.statusCode() >= 200 && send.statusCode() < 300 ;
        Long count = null;
        if (inputStream instanceof FileCachingInputStream fc) {
            count = fc.getCount();
        }

        String status ;
        String response ;
        try {
            final JsonNode bodyNode = JSONREADER.readTree(send.body());
            logger.info("{} {}", mid, bodyNode);

            status = Optional.ofNullable(bodyNode.get("status")).map(JsonNode::textValue).orElse("<no status>");
            response = Optional.ofNullable(bodyNode.get("response")).map(JsonNode::textValue).orElse("<no response>");
            if (!success) {
                throw new SourcingServiceException(send.statusCode(), bodyNode);
            }
        } catch (SourcingServiceException sse) {
            throw sse;
        } catch (Exception e) {
            if (success) {
                logger.error(e.getMessage(), e);
            }
            status = null;
            response = null;

        }

        logger.log(success?  Level.INFO : Level.ERROR,
            "{} uploaded: {} ({}) {} {}", mid, status, send.statusCode(), FileSizeFormatter.DEFAULT.format(count), send.body());


        return new UploadResponse(
            mid,
            send.statusCode(),
            status,
            response,
            count,
            "2:"+  configuration.get().cleanBaseUrl()
            );

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

        //noinspection SwitchStatementWithTooFewBranches
        return Optional.of(switch(configuration.get().version()) {
            case 1 -> MAPPER.readValue(statusResponse.body(), nl.vpro.sourcingservice.v1.StatusResponse.class).normalize();
            default -> V2READER.readValue(statusResponse.body(), nl.vpro.sourcingservice.v2.StatusResponse.class).normalize();
            }
        );

    }

    @Override
    public DeleteResponse delete(String mid, int daysBeforeHardDelete) throws IOException, InterruptedException {
        final HttpRequest statusRequest = deleteRequest(mid, daysBeforeHardDelete);
        final HttpResponse<String> statusResponse = client.send(statusRequest, HttpResponse.BodyHandlers.ofString());
        meter("status.delete", statusResponse);
        if (statusResponse.statusCode() == 404) {
            return new DeleteResponse("not found", "Already deleted?");
        }
        if (statusResponse.statusCode() > 299) {
            throw new IllegalArgumentException(statusRequest + ":" + statusResponse.statusCode() + ":" +  statusResponse.body());
        }
        try {
            return JSONREADER.readValue(statusResponse.body(), DeleteResponse.class);
        } catch (Exception e) {
            throw new SourcingServiceException(statusResponse.statusCode(), "Could not parse body of delete request " + statusResponse.body());
        }
    }

    @Deprecated
    private void ingest(SimpleLogger logger, String mid, String filename, Restrictions restrictions) throws IOException, InterruptedException {

        final ObjectNode metaData = metadata(logger, mid, filename, restrictions);

        HttpRequest ingestRequest = ingest(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(metaData)));
        HttpResponse<byte[]> ingest = client.send(ingestRequest,
            HttpResponse.BodyHandlers.ofByteArray());
        meter("ingest", ingest);

        if (ingest.statusCode() > 299) {
            throw new IllegalArgumentException(ingestRequest + ":" + ingest.statusCode() + ":" + new String(ingest.body()));
        }
        IngestResponse response = JSONREADER.readValue(ingest.body(), IngestResponse.class);
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

        final String email = Optional.ofNullable(errors).orElse(configuration.get().defaultEmail());
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
        JsonNode node = JSONREADER.readTree(start.body());
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

    @Deprecated
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


    @Deprecated
    private void uploadChunk(
        final SimpleLogger logger,
        final String mid,
        final InputStream inputStream,
        final AtomicLong uploaded,
        final Restrictions restrictions,
        final long total,
        final AtomicInteger partNumber) throws IOException, InterruptedException, SourcingServiceException {
        try (InputStreamChunk chunkStream = new InputStreamChunk(configuration.get().chunkSize(), inputStream)) {
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
            final JsonNode node = JSONREADER.readTree(transfer.body());
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

    @Deprecated
    private UploadResponse uploadFinish(
        SimpleLogger logger,
        String mid,
        AtomicLong uploaded,
        Restrictions restrictions) throws IOException, InterruptedException {
        final MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add(UPLOAD_PHASE, "finish");



        addRegion(logger, body, restrictions);
        HttpRequest multipart = multipart(mid, body);
        final HttpResponse<String> finish = client.send(multipart, HttpResponse.BodyHandlers.ofString());
        meter("finish", finish);

        final JsonNode node = JSONREADER.readTree(finish.body());
        final String status = Optional.ofNullable(node.get("status")).map(JsonNode::textValue).orElse("<no status>");
        final String response = Optional.ofNullable(node.get("response")).map(JsonNode::textValue).orElse("<no response>");

        final boolean  success = finish.statusCode() >= 200 && finish.statusCode() < 300 ;
        logger.log(success?  Level.INFO : Level.ERROR, "{} finish: {} ({}) {} {}", mid, status, finish.statusCode(), FileSizeFormatter.DEFAULT.format(uploaded), MAPPER.writeValueAsString(node));
        JsonNode bodyNode = JSONREADER.readTree(finish.body());
        return new UploadResponse(
            mid,
            finish.statusCode(),
            status,
            response,
            uploaded.get(),
            "1:"+ configuration.get().cleanBaseUrl()
        );
    }

    protected abstract String implName();

    protected void meter(String name, HttpResponse<?> response) {
        meterRegistry.counter("sourcing." + implName() + "." + name , "status", String.valueOf(response.statusCode())).increment();
    }

    @Nullable
    protected String getCallbackUrl(String mid) {
        return configuration.get().callBackUrl(mid);
    }

    @SneakyThrows
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
                .header("Content-Type", "application/json")
                .build();
    }

    protected HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder()
            .header("Authorization", "Bearer " +
                Optional.ofNullable(configuration.get().token()).orElseThrow(() -> new IllegalStateException("No token configured")))
            .uri(URI.create(forPath(path)));
    }

    @ManagedAttribute
    String pathForIngestMultipart(String mid) {
        return multipartPart.formatted(mid);
    }

    String forPath(String path) {
        return configuration.get().cleanBaseUrl() + "api/" + path;
    }


    @ManagedAttribute
    @Override
    public String getUploadString() {
        return forPath(pathForIngestMultipart("%s"));
    }

    @ManagedAttribute
    public String getBaseUrl() {
        return configuration.get().cleanBaseUrl();
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
