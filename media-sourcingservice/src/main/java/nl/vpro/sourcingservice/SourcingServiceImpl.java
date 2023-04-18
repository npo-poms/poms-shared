package nl.vpro.sourcingservice;

import io.github.yskszk63.jnhttpmultipartformdatabodypublisher.MultipartFormDataBodyPublisher;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.domain.user.User;
import nl.vpro.domain.user.UserService;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileSizeFormatter;
import nl.vpro.util.InputStreamChunk;


@Log4j2
public class SourcingServiceImpl implements SourcingService {


    final Abstract audio;
    final Abstract video;

    public SourcingServiceImpl(
        @Value("${sourcingservice.audioBaseUrl}") String audioBaseUrl,
        @Value("${sourcingservice.videoBaseUrl}") String videoBaseUrl,
        @Value("${sourcingservice.callbackBaseUrl}") String callbackBaseUrl,
        @Value("${sourcingservice.token}") String token,
        UserService<?> userService,
        @Value("${sourcingservice.chunkSize:10000000}") int chunkSize,
        @Value("${sourcingservice.defaultEmail:#{null}}") String defaultEmail
       ) {
        this.audio = new Abstract(audioBaseUrl, callbackBaseUrl, token,  userService, chunkSize, defaultEmail);

        this.video = new Abstract(videoBaseUrl, callbackBaseUrl, token , userService, chunkSize, defaultEmail);
    }


    @Override
    public UploadResponse uploadAudio(SimpleLogger logger, final String mid, final long fileSize, InputStream inputStream) throws IOException, InterruptedException {

        return upload(audio, logger, mid, fileSize, inputStream);
    }


    @Override
    public UploadResponse uploadVideo(SimpleLogger logger, final String mid, final long fileSize, InputStream inputStream) throws IOException, InterruptedException {
        return upload(video, logger, mid, fileSize, inputStream);

    }

    @Override
    public String getAudioUploadString() {
        return audio.getUploadString();
    }

    @Override
    public String getVideoUploadString() {
        return video.getUploadString();
    }


    public UploadResponse upload(Abstract base, SimpleLogger logger, final String mid, final long fileSize, InputStream inputStream) throws IOException, InterruptedException {

        base.ingest(logger, mid);

        base.uploadStart(logger, mid, fileSize);

        AtomicLong uploaded = new AtomicLong(0);
        while(uploaded.get() < fileSize) {
            base.uploadChunk(logger, mid, inputStream, uploaded);
        }
        inputStream.close();
        assert uploaded.get() == fileSize;

        return base.uploadFinish(logger, mid, uploaded);
    }

    static class Abstract {

        private final HttpClient client = HttpClient.newHttpClient();

        private final String baseUrl;

        private final String callbackBaseUrl;
        private final String token;
        private final UserService<?> userService;
        private final int chunkSize;
        private final String defaultEmail;

        Abstract(String baseUrl,  String callbackBaseUrl, String token, UserService<?> userService, int chunkSize, String defaultEmail) {
            this.baseUrl = baseUrl;

            this.callbackBaseUrl = callbackBaseUrl;
            this.token = token;
            this.userService = userService;
            this.chunkSize = chunkSize;
            this.defaultEmail = defaultEmail;
        }


        private void ingest(SimpleLogger logger, String mid) throws IOException, InterruptedException {
            ObjectNode metaData = Jackson2Mapper.INSTANCE.createObjectNode();
            metaData.put("mid", mid);
            metaData.put("callback_url", getCallbackUrl(mid));

            HttpRequest ingestRequest = ingest(HttpRequest.BodyPublishers.ofByteArray(Jackson2Mapper.INSTANCE.writeValueAsBytes(metaData)));
            HttpResponse<byte[]> ingest = client.send(ingestRequest,
                HttpResponse.BodyHandlers.ofByteArray());
            if (ingest.statusCode() > 299) {
                throw new IllegalArgumentException(ingestRequest + ":" + ingest.statusCode() + ":" + new String(ingest.body()));
            }
            IngestResponse response = Jackson2Mapper.LENIENT.readValue(ingest.body(), IngestResponse.class);
            logger.info("ingest {}", response);
        }

        private void uploadStart(SimpleLogger logger, String mid, long fileSize) throws IOException, InterruptedException {

            final String email = userService.currentUser().map(User::getEmail).orElse(defaultEmail);
            MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
                .add("upload_phase", "start");
            if (email != null) {
                body.add("email", email);
            }
            body.add("file_size", String.valueOf(fileSize));
            HttpResponse<String> start = client.send(multipart(mid, body), HttpResponse.BodyHandlers.ofString());
            if (start.statusCode() > 299) {
                throw new IllegalArgumentException(start.body());
            }
            JsonNode node = Jackson2Mapper.LENIENT.readTree(start.body());
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
            JsonNode node = Jackson2Mapper.LENIENT.readTree(transfer.body());
            logger.info("transfer: {} ({}) {}", node.get("status").textValue(), transfer.statusCode(), FileSizeFormatter.DEFAULT.format(l));
        }

        private UploadResponse uploadFinish(SimpleLogger logger, String mid, AtomicLong uploaded) throws IOException, InterruptedException {
            MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
                .add("upload_phase", "finish");

            HttpResponse<String> finish = client.send(multipart(mid, body), HttpResponse.BodyHandlers.ofString());

            JsonNode node = Jackson2Mapper.LENIENT.readTree(finish.body());

            logger.info("finish: {} ({}) {}", node.get("status").textValue(), finish.statusCode(), FileSizeFormatter.DEFAULT.format(uploaded));
            JsonNode bodyNode = Jackson2Mapper.getLenientInstance().readTree(finish.body());
            return new UploadResponse(finish.statusCode(), bodyNode.get("status").textValue(), bodyNode.get("response").textValue());
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


        String getUploadString() {
            return forPath(pathForIngestMultipart("%s"));
        }


    }
}
