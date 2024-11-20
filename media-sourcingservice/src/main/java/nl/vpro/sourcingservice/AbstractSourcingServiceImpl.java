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
import java.util.function.Supplier;

import org.apache.tika.mime.*;
import org.checkerframework.checker.nullness.qual.Nullable;
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
        V2READER = MAPPER.readerFor(nl.vpro.sourcingservice.v2.StatusResponse.class).with(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);
        JSONREADER = MAPPER.readerFor(ObjectNode.class).with(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION);

    }

    private final HttpClient client = HttpClient
        .newBuilder()
        .version(HttpClient.Version.HTTP_1_1) // GOAWAY trouble?
        .build();

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
        long fileSize,
        String contentType,
        InputStream inputStream,
        @Nullable String errors
    ) throws SourcingServiceException {

        return uploadv2(logger, mid, contentType, inputStream);


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

        return Optional.of(V2READER.readValue(statusResponse.body(), nl.vpro.sourcingservice.v2.StatusResponse.class).normalize());

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
        return configuration.get().callBackUrl(mid);
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


    String forPath(String path) {
        return configuration.get().cleanBaseUrl() + "api/" + path;
    }


    @ManagedAttribute
    @Override
    public String getUploadString() {
        return forPath(uploadRequestBuilder("%s").toString());
    }

    @ManagedAttribute
    public String getBaseUrl() {
        return configuration.get().cleanBaseUrl();
    }


}
