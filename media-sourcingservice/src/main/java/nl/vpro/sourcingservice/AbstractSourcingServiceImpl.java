package nl.vpro.sourcingservice;

import io.github.yskszk63.jnhttpmultipartformdatabodypublisher.MultipartFormDataBodyPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
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
import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileSizeFormatter;
import nl.vpro.util.InputStreamChunk;


/**
 * Straight forward client for some relevant parts of the
 * sourcing services (
 * '<a href="https://test.sourcing-audio.cdn.npoaudio.nl/api/documentation">audio</a>'
 * '<a href="https://test.sourcing-video.cdn.npoaudio.nl/api/documentation">video</a>').
 *
 */
@Log4j2
public abstract class AbstractSourcingServiceImpl implements SourcingService {

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

    AbstractSourcingServiceImpl(
        String baseUrl,
        @Nullable String callbackBaseUrl,
        String token,
        int chunkSize,
        String defaultEmail,
        MeterRegistry meterRegistry) {
        this.baseUrl = baseUrl.replaceAll("([^/])$","$1/");

        this.callbackBaseUrl = callbackBaseUrl;
        this.token = token;
        this.chunkSize = chunkSize;
        this.defaultEmail = defaultEmail;
        this.meterRegistry = meterRegistry;
    }


    protected abstract String getFileName(String mid);


   @Override
   public UploadResponse upload(
       SimpleLogger logger,
       final String mid,
       @Nullable Restrictions restrictions,
       final long fileSize,
       InputStream inputStream,
       String errors,
       Consumer<Phase> phase) throws IOException, InterruptedException {

       final AtomicLong uploaded = new AtomicLong(0);
       try (inputStream) {
           // this is not needed (as I've tested)
           //ingest(logger, mid, getFileName(mid), restrictions);
           phase.accept(Phase.START);
           uploadStart(logger, mid, fileSize, errors, restrictions);
           phase.accept(Phase.UPLOAD);
           AtomicInteger partNumber = new AtomicInteger(1);
           while (uploaded.get() < fileSize) {
               uploadChunk(logger, mid, inputStream, uploaded, restrictions, fileSize, partNumber);
           }
       }
       assert uploaded.get() == fileSize;

       phase.accept(Phase.FINISH);
       return uploadFinish(logger, mid, uploaded, restrictions);
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
        return Optional.of(MAPPER.readValue(statusResponse.body(), StatusResponse.class));
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

    private void uploadStart(
        final SimpleLogger logger,
        final String mid,
        final long fileSize,
        final @Nullable String errors,
        final Restrictions restrictions) throws IOException, InterruptedException {

        final String email = Optional.ofNullable(errors).orElse(defaultEmail);
        final MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add("upload_phase", "start");
        if (email != null) {
            body.add("email", email);
        }

        addRegion(logger, body, restrictions);

        body.add("file_size", String.valueOf(fileSize));
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
        logger.info("start: {} ({}) filesize: {},  response: {}, email={}{}",
            node.get("status").textValue(),
            start.statusCode(),
            FileSizeFormatter.DEFAULT.format(fileSize),
            node.get("response").textValue(),
            email,
            callBackUrl
        );
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
        final AtomicInteger partNumber) throws IOException, InterruptedException {
        try (InputStreamChunk chunkStream = new InputStreamChunk(chunkSize, inputStream)) {
            MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
                .add("upload_phase", "transfer")
                .addStream(
                    "file_chunk",
                    "part" + (partNumber.getAndIncrement()), () -> chunkStream);

            addRegion(logger, body, restrictions);
            HttpRequest transferRequest = multipart(mid, body);
            HttpResponse<String> transfer = client.send(
                transferRequest, HttpResponse.BodyHandlers.ofString());
            meter("transfer", transfer);

            long l = uploaded.addAndGet(chunkStream.getCount());
            JsonNode node = MAPPER.readTree(transfer.body());
            logger.info("{} transfer: {} ({}) {}/{}",
                mid,
                node.get("status").textValue(),
                transfer.statusCode(),
                FileSizeFormatter.DEFAULT.format(l),
                FileSizeFormatter.DEFAULT.format(total)
            );
        }
    }

    private UploadResponse uploadFinish(SimpleLogger logger, String mid, AtomicLong uploaded, Restrictions restrictions) throws IOException, InterruptedException {
        final MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add("upload_phase", "finish");

        addRegion(logger, body, restrictions);

        final HttpResponse<String> finish = client.send(multipart(mid, body), HttpResponse.BodyHandlers.ofString());
        meter("finish", finish);

        final JsonNode node = MAPPER.readTree(finish.body());

        logger.info("{} finish: {} ({}) {}", mid, node.get("status").textValue(), finish.statusCode(), FileSizeFormatter.DEFAULT.format(uploaded));
        JsonNode bodyNode = MAPPER.readTree(finish.body());
        return new UploadResponse(
            mid,
            finish.statusCode(),
            bodyNode.get("status").textValue(),
            bodyNode.get("response").textValue(),
            uploaded.get()
        );
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
