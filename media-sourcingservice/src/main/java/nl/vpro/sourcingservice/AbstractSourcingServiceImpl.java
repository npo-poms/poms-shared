package nl.vpro.sourcingservice;

import io.github.yskszk63.jnhttpmultipartformdatabodypublisher.MultipartFormDataBodyPublisher;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.apache.tika.mime.*;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.jmx.export.annotation.ManagedAttribute;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.logging.simple.Level;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.*;

import static nl.vpro.i18n.MultiLanguageString.en;
import static nl.vpro.i18n.MultiLanguageString.nl;


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

    private static final String FILE   = "file";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final ObjectReader V2READER;
    private static final ObjectReader JSONREADER;
    static {
        MAPPER.registerModule( new JavaTimeModule());
        V2READER = MAPPER.readerFor(StatusResponse.class).with(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);
        JSONREADER = MAPPER.readerFor(ObjectNode.class).with(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);

    }

    private final HttpClient client = HttpClient
        .newBuilder()
        .version(HttpClient.Version.HTTP_1_1) // GOAWAY trouble?
        .build();

    private final Configuration configuration;

    private final MeterRegistry meterRegistry;

    AbstractSourcingServiceImpl(
        Configuration configuration,
        MeterRegistry meterRegistry) {

        this.configuration = configuration;
        this.meterRegistry = meterRegistry;
    }


    protected  String getFileName(String mid, String mimeType) {
        String ext;
        final String defaultExt = "." + defaultFormat().name().toLowerCase();
        try {

            MimeType tika = MimeTypes.getDefaultMimeTypes().getRegisteredMimeType(mimeType);
            if (tika == null) {
                tika = MimeTypes.getDefaultMimeTypes().forName(mimeType);
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
    public CompletableFuture<UploadResponse> upload(
        SimpleLogger logger,
        String mid,
        long fileSize,
        String mimeType,
        InputStream inputStream,
        @Nullable String profile,
        @Nullable String errors
    ) {
        if (inputStream instanceof FileCachingInputStream fileCachingInputStream) {
            return upload(logger, mid, fileSize, mimeType, fileCachingInputStream, profile, errors);
        } else {
            logger.warn("InputStream is not a FileCachingInputStream, falling back to non-caching upload for {}", mid);
            return upload(logger, mid, fileSize, mimeType, inputStream, profile, errors, null);
        }
    }


    @Override
    public CompletableFuture<UploadResponse> upload(
        SimpleLogger logger,
        String mid,
        long fileSize,
        String contentType,
        FileCachingInputStream inputStream,
        @Nullable String profile,
        @Nullable String errors
    ) {
        return upload(logger, mid, fileSize, contentType, inputStream, profile, errors, inputStream.getCount());
    }


    protected CompletableFuture<UploadResponse> upload(
        SimpleLogger logger,
        String mid,
        long fileSize,
        String contentType,
        InputStream inputStream,
        @Nullable String profile,
        @Nullable String errors,
        Long count
    ) {
        final HttpRequest.Builder uploadRequestBuilder = uploadRequestBuilder(mid);

        final MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher();
        final String fileName = getFileName(mid, contentType);
        body.addChannel(FILE, fileName,
            () -> WrappedReadableByteChannel
                .builder()
                .inputStream(inputStream)
                .batchSize((long) configuration.chunkSize())
                .consumer(l -> logger.info(
                    en("Uploaded %s/%s to %s")
                        .nl("Geüpload %s/%s naar %s")
                        .formatted(
                            FileSizeFormatter.DEFAULT.format(l),
                            FileSizeFormatter.DEFAULT.format(fileSize),
                            configuration.cleanBaseUrl()))
                )
                .build(),
            contentType
        );
        if (profile != null) {
            logger.info("Profile for {}: {}", mid, profile);
            body.add("profile", profile);
        }
        String callbackUrl = getCallbackUrl(mid);
        if (callbackUrl != null) {
            String authentication = configuration.callbackAuthentication();
            if (authentication != null) {
                callbackUrl = URLUtils.addAuthentication(callbackUrl, authentication);
            }
            logger.info("Callback URL for {}: {}", mid, URLUtils.hidePassword(callbackUrl));
            body.add("callback_url", callbackUrl);
        }

        final HttpRequest post = uploadRequestBuilder
            .header("Content-Type", body.contentType())
            .POST(body)
            .build();

        logger.info(en("Posting {} for {} to {}")
            .nl("Posting {} voor {} naar {}").slf4jArgs(fileName, mid, post.uri()));

        final CompletableFuture<HttpResponse<String>> asyncSend = client.sendAsync(post, HttpResponse.BodyHandlers.ofString());


        return asyncSend.thenApply((response) -> {
            final boolean success = response.statusCode() >= 200 && response.statusCode() < 300;


            if (!success) {
                logger.warn("Status code for {}: {}", post.uri(), response.statusCode());
            }  else {
                logger.info("Status code for {}: {}", post.uri(), response.statusCode());

            }


            String status;
            String responseBody;
            try {
                final JsonNode bodyNode = JSONREADER.readTree(response.body());
                logger.info("{} {}", mid, bodyNode);
                status = Optional.ofNullable(bodyNode.get("status")).map(JsonNode::textValue).orElse("<no status>");
                responseBody = Optional.ofNullable(bodyNode.get("response")).map(JsonNode::textValue).orElse("<no response>");
                if (!success) {
                    throw new SourcingServiceException(response.statusCode(), bodyNode);
                }
            } catch (SourcingServiceException sse) {
                throw sse;
            } catch (Exception e) {
                if (success) {
                    logger.error(response.uri() + ":" + e.getClass() + " " + e.getMessage(), e);
                }
                status = null;
                responseBody = null;

            }

            final String httpBody;
            if (response.headers().firstValue("content-type").orElse("text/plain").startsWith("text/html")) {
                // Silly sourcing service gives HTML back on 404's. This is unpresentable.
                httpBody = "HTML:" + Jsoup.clean(response.body(), Safelist.none());
            } else {
                httpBody = response.body();
            }

            logger.log(success ? Level.INFO : Level.ERROR,
                nl("{} {} geüpload: {} ({}) {} {}")
                    .en("{} {} uploaded: {} ({}) {} {}")
                        .slf4jArgs(
                            mid,
                            response.uri(),
                            status == null ? "no status" : status, response.statusCode(),
                            FileSizeFormatter.DEFAULT.format(count),
                            httpBody).build());

            boolean retryable = true;
            if (response.statusCode() == 404) {
                retryable = false;
            }


            return new UploadResponse(
                mid,
                response.statusCode(),
                status,
                responseBody == null ? httpBody : responseBody,
                count,
                "v2:" + configuration.cleanBaseUrl(),
                retryable
            );
        });
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

        return Optional.of(V2READER.readValue(statusResponse.body(), StatusResponse.class));
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


    protected abstract String implName();

    protected void meter(String name, HttpResponse<?> response) {
        meterRegistry.counter("sourcing." + implName() + "." + name , "status", String.valueOf(response.statusCode())).increment();
    }

    @Nullable
    protected String getCallbackUrl(String mid) {
        return configuration.callBackUrl(mid);
    }


    protected HttpRequest statusRequest(String mid) {
        return request("ingest/" + mid + "/status")
            .GET().build();
    }

    protected HttpRequest.Builder uploadRequestBuilder(String mid) {
        return request(getUploadPath().formatted(mid));
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
                Optional.ofNullable(configuration.token()).orElseThrow(() -> new IllegalStateException("No token configured")))
            .uri(URI.create(forPath(path)));
    }


    String forPath(String path) {
        return configuration.cleanBaseUrl() + "api/" + path;
    }


    @ManagedAttribute
    @Override
    public String getUploadString() {
        return forPath(getUploadPath());
    }

    public String getUploadPath() {
        return "ingest/%s/upload";
    }

    @ManagedAttribute
    public String getBaseUrl() {
        return configuration.cleanBaseUrl();
    }


    @Override
    public String toString() {
        return getUploadString();
    }

}
