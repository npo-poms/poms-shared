package nl.vpro.sourcingservice;

import io.github.yskszk63.jnhttpmultipartformdatabodypublisher.MultipartFormDataBodyPublisher;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.net.URI;
import java.net.http.*;

import com.google.common.io.ByteStreams;


@Log4j2
public class SourcingServiceImpl implements SourcingService {

    private final static long CHUNK_SIZE = 10_000_000L;

    private final String baseUrl;
    private final String token;
    private final HttpClient client = HttpClient.newHttpClient();


    public SourcingServiceImpl(String baseUrl, String token) {
        this.baseUrl = baseUrl;
        this.token = token;
    }


    @Override
    @SneakyThrows
    public void upload(String mid, final long fileSize, InputStream inputStream)  {
        {
            MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
                .add("upload_phase", "start")
                .add("email", "michiel.meeuwissen@gmail.com")
                .add("file_size", String.valueOf(fileSize));
            HttpResponse<String> start = client.send(multipart(mid, body), HttpResponse.BodyHandlers.ofString());
            if (start.statusCode() > 299) {
                throw new IllegalArgumentException(start.body());
            }
            log.info("start: {} {}", start.statusCode(), start.body());
        }
        long uploaded = 0;
        while(uploaded < fileSize) {
            MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
                .add("upload_phase", "transfer")
                .addStream("file_chunk", "part" , () -> {
                    return ByteStreams.limit(inputStream, CHUNK_SIZE);
                })
                .add("file_size", String.valueOf(fileSize));
            HttpRequest transferRequest = multipart(mid, body);
            HttpResponse<String> transfer = client.send(transferRequest, HttpResponse.BodyHandlers.ofString());
            log.info("transfer: {} {}", transfer.statusCode(), transfer.body());
            uploaded += CHUNK_SIZE;
        }
        MultipartFormDataBodyPublisher body = new MultipartFormDataBodyPublisher()
            .add("upload_phase", "finish");

        HttpResponse<String> finish = client.send(multipart(mid, body), HttpResponse.BodyHandlers.ofString());

        log.info("finish: {} {}", finish.statusCode(), finish.body());
    }

    protected HttpRequest multipart(String mid, MultipartFormDataBodyPublisher body) {
        return request("ingest/" + mid + "/multipart")
            .header("Content-Type", body.contentType())
            .POST(body).build();
    }

    protected HttpRequest.Builder request(String path) {
        return HttpRequest.newBuilder()
            .header("Authorization", "Bearer " + token)
            .uri(URI.create(baseUrl + "api/" + path));

    }
}
