package nl.vpro.sourcingservice;

import io.github.yskszk63.jnhttpmultipartformdatabodypublisher.MultipartFormDataBodyPublisher;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.net.URI;
import java.net.http.*;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.JsonNode;

import nl.vpro.domain.media.update.UploadResponse;
import nl.vpro.domain.user.User;
import nl.vpro.domain.user.UserService;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.util.FileSizeFormatter;
import nl.vpro.util.InputStreamChunk;


@Log4j2
public class SourcingServiceImpl implements SourcingService {




    private final String baseUrl;
    private final String callbackBaseUrl;
    private final String token;
    private final UserService<?> userService;
    private final int chunkSize;

    HttpClient client = HttpClient.newHttpClient();


    public SourcingServiceImpl(
        @Value("${sourcingservice.baseUrl}") String baseUrl,
        @Value("${sourcingservice.token}") String token,
        @Value("${sourcingservice.callbackBaseUrl}") String callbackBaseUrl,
        @Value("${sourcingservice.chunkSize:10_000_000}") int chunkSize,
        UserService<?> userService) {
        this.baseUrl = baseUrl;
        this.token = token;
        this.callbackBaseUrl = callbackBaseUrl;
        this.userService = userService;
        this.chunkSize = chunkSize;
    }


    @Override
    @SneakyThrows
    public UploadResponse upload(SimpleLogger logger, String mid, final long fileSize, InputStream inputStream)  {
        {
            MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
                .add("upload_phase", "start")
                .add("email", userService.currentUser().map(User::getEmail).orElse("m.meeuwissen.vpro@gmail.com"))
                .add("callback_url", callbackBaseUrl.formatted(mid))
                .add("file_size", String.valueOf(fileSize));
            HttpResponse<String> start = client.send(multipart(mid, body), HttpResponse.BodyHandlers.ofString());
            if (start.statusCode() > 299) {
                throw new IllegalArgumentException(start.body());
            }
            logger.info("start: {} {} {}", FileSizeFormatter.DEFAULT.format(fileSize), start.statusCode(), start.body());
        }
        long uploaded = 0;
        while(uploaded < fileSize) {
            InputStreamChunk chunkStream = new InputStreamChunk(chunkSize, inputStream);
            MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
                .add("upload_phase", "transfer")
                .addStream("file_chunk", "part" , () -> {
                    return chunkStream;

                })
                .add("file_size", String.valueOf(fileSize));
            HttpRequest transferRequest = multipart(mid, body);
            HttpResponse<String> transfer = client.send(
                transferRequest, HttpResponse.BodyHandlers.ofString());
            uploaded += chunkStream.getCount();
            logger.info("transfer: {},  {} {}", FileSizeFormatter.DEFAULT.format(chunkStream.getCount()), transfer.statusCode(), transfer.body());
            uploaded += chunkStream.getCount();
        }
        inputStream.close();
        MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add("upload_phase", "finish");

        HttpResponse<String> finish = client.send(multipart(mid, body), HttpResponse.BodyHandlers.ofString());


        logger.info("finish: {} {}", finish.statusCode(), finish.body());
        JsonNode bodyNode = Jackson2Mapper.getLenientInstance().readTree(finish.body());
        return new UploadResponse(finish.statusCode(), bodyNode.get("status").textValue(), bodyNode.get("response").textValue());
    }

    protected HttpRequest multipart(String mid, MultipartFormDataBodyPublisher body) {
        return request(pathForIngest(mid))
            .header("Content-Type", body.contentType())
            .POST(body).build();
    }

    protected HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder()
            .header("Authorization", "Bearer " + token)
            .uri(URI.create(forPath(path)));
    }

    String pathForIngest(String mid) {
        return "ingest/" + mid + "/multipart";
    }

    String forPath(String path) {
        return baseUrl + "api/" + path;
    }

    @Override
    public String getUploadString() {
        return forPath(pathForIngest("%s"));
    }


}
