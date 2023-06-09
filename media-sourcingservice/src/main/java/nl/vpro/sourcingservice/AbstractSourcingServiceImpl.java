package nl.vpro.sourcingservice;

import io.github.yskszk63.jnhttpmultipartformdatabodypublisher.MultipartFormDataBodyPublisher;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.domain.user.User;
import nl.vpro.domain.user.UserService;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileSizeFormatter;
import nl.vpro.util.InputStreamChunk;


@Log4j2
public abstract class AbstractSourcingServiceImpl implements SourcingService {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.registerModule( new JavaTimeModule());
    }

    private final HttpClient client = HttpClient.newHttpClient();

    private final String baseUrl;

    private final String callbackBaseUrl;
    private final String token;
    private final UserService<?> userService;
    private final int chunkSize;
    private final String defaultEmail;

    AbstractSourcingServiceImpl(String baseUrl,  String callbackBaseUrl, String token, UserService<?> userService, int chunkSize, String defaultEmail) {
        this.baseUrl = baseUrl;

        this.callbackBaseUrl = callbackBaseUrl;
        this.token = token;
        this.userService = userService;
        this.chunkSize = chunkSize;
        this.defaultEmail = defaultEmail;
    }


    protected abstract String getFileName(String mid);


   @Override
   public UploadResponse upload(
       SimpleLogger logger,
       final String mid,
       Restrictions restrictions,
       final long fileSize, InputStream inputStream, String errors) throws IOException, InterruptedException {

        ingest(logger, mid, getFileName(mid), restrictions);

        uploadStart(logger, mid, fileSize, errors);

        AtomicLong uploaded = new AtomicLong(0);
        while(uploaded.get() < fileSize) {
            uploadChunk(logger, mid, inputStream, uploaded);
        }
        inputStream.close();
        assert uploaded.get() == fileSize;

        return uploadFinish(logger, mid, uploaded);
    }


    @Override
    public StatusResponse status(String mid) throws IOException, InterruptedException {
        HttpRequest statusRequest = statusRequest(mid);
        HttpResponse<String> ingest = client.send(statusRequest, HttpResponse.BodyHandlers.ofString());
        if (ingest.statusCode() > 299) {
            throw new IllegalArgumentException(statusRequest + ":" + ingest.statusCode() + ":" +  ingest.body());
        }
        return MAPPER.readValue(ingest.body(), StatusResponse.class);
    }
    private void ingest(SimpleLogger logger, String mid, String filename, Restrictions restrictions) throws IOException, InterruptedException {
        ObjectNode metaData = MAPPER.createObjectNode();
        metaData.put("mid", mid);
        metaData.put("callback_url", getCallbackUrl(mid));

        // I don't get the point of this
        metaData.put("filename", filename);

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
                metaData.put("geo_restriction", restrictions.getGeoRestriction().getRegion().name());
            }
        }
        if (restrictions.getAgeRating() != null && restrictions.getAgeRating() != AgeRating.ALL) {
            logger.info("Specified age rating {} is not supported for audio, and will be ignored", restrictions.getAgeRating());
        }

        // These should not be needed
        metaData.put("broadcaster", "VPRO");
        metaData.put("title", "should not be needed");

        HttpRequest ingestRequest = ingest(HttpRequest.BodyPublishers.ofByteArray(MAPPER.writeValueAsBytes(metaData)));
        HttpResponse<byte[]> ingest = client.send(ingestRequest,
            HttpResponse.BodyHandlers.ofByteArray());
        if (ingest.statusCode() > 299) {
            throw new IllegalArgumentException(ingestRequest + ":" + ingest.statusCode() + ":" + new String(ingest.body()));
        }
        IngestResponse response = MAPPER.readValue(ingest.body(), IngestResponse.class);
        logger.info("ingest {}", response);
    }

    private void uploadStart(SimpleLogger logger, String mid, long fileSize, @Nullable String errors) throws IOException, InterruptedException {

        final String email = Optional.ofNullable(errors)
            .orElse(
                userService.currentUser().map(User::getEmail)
                    .orElse(defaultEmail)
            );
        MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add("upload_phase", "start");
        if (email != null) {
            body.add("email", email);
        }
        body.add("file_size", String.valueOf(fileSize));
        HttpRequest multipart = multipart(mid, body);
        HttpResponse<String> start = client.send(multipart, HttpResponse.BodyHandlers.ofString());
        if (start.statusCode() > 299) {
            throw new IllegalArgumentException(multipart + ": " + start.statusCode() + ":" + start.body());
        }
        JsonNode node = MAPPER.readTree(start.body());
        logger.info("start: {} ({}) filesize: {},  response: {}, email={}, callback_url={}", node.get("status").textValue(), start.statusCode(), FileSizeFormatter.DEFAULT.format(fileSize), node.get("response").textValue(), email, getCallbackUrl(mid).replaceAll("^(.*://)(.*?:).*?@", "$1$2xxxx@"));
    }

    private void uploadChunk(SimpleLogger logger, String mid, InputStream inputStream, AtomicLong uploaded) throws IOException, InterruptedException {
        InputStreamChunk chunkStream = new InputStreamChunk(chunkSize, inputStream);
        MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add("upload_phase", "transfer")
            .addStream("file_chunk", "part", () -> chunkStream);
        HttpRequest transferRequest = multipart(mid, body);
        HttpResponse<String> transfer = client.send(
            transferRequest, HttpResponse.BodyHandlers.ofString());
        long l = uploaded.addAndGet(chunkStream.getCount());
        JsonNode node = MAPPER.readTree(transfer.body());
        logger.info("transfer: {} ({}) {}", node.get("status").textValue(), transfer.statusCode(), FileSizeFormatter.DEFAULT.format(l));
    }

    private UploadResponse uploadFinish(SimpleLogger logger, String mid, AtomicLong uploaded) throws IOException, InterruptedException {
        MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add("upload_phase", "finish");

        HttpResponse<String> finish = client.send(multipart(mid, body), HttpResponse.BodyHandlers.ofString());

        JsonNode node = MAPPER.readTree(finish.body());

        logger.info("finish: {} ({}) {}", node.get("status").textValue(), finish.statusCode(), FileSizeFormatter.DEFAULT.format(uploaded));
        JsonNode bodyNode = MAPPER.readTree(finish.body());
        return new UploadResponse(
            mid,
            finish.statusCode(),
            bodyNode.get("status").textValue(),
            bodyNode.get("response").textValue(),
            uploaded.get()
        );
    }

    protected String getCallbackUrl(String mid) {
        return callbackBaseUrl.formatted(mid);

    }

    protected HttpRequest multipart(String mid, MultipartFormDataBodyPublisher body) {
        return request(pathForIngestMultipart(mid))
            .header("Content-Type", body.contentType())
            .POST(body).build();
    }


    protected HttpRequest ingest(HttpRequest.BodyPublisher body) {
        return request("ingest")
            .header("Content-Type", "application/json")
            .POST(body).build();
    }


    protected HttpRequest statusRequest(String mid) {
            return request("ingest/" + mid + "/status")
                .GET().build();
    }

    protected HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder()
            .header("Authorization", "Bearer " + token)
            .uri(URI.create(forPath(path)));
    }

    String pathForIngestMultipart(String mid) {
        return "ingest/" + mid + "/multipart";
    }

    String forPath(String path) {
        return baseUrl + "api/" + path;
    }


    @Override
    public String getUploadString() {
        return forPath(pathForIngestMultipart("%s"));
    }


}
