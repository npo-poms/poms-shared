package nl.vpro.nep.service.impl;


import io.openapitools.jackson.dataformat.hal.HALMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.logging.LoggerOutputStream;
import nl.vpro.nep.domain.workflow.*;
import nl.vpro.nep.service.NEPGatekeeperService;
import nl.vpro.nep.service.exception.NEPException;
import nl.vpro.poms.shared.UploadUtils;
import nl.vpro.util.*;

import static nl.vpro.util.TimeUtils.parseDuration;


/**
 * Wrapper for https://npo-webonly-gatekeeper.nepworldwide.nl
 * <p>

 *
 * TODO, where's the documentation of that?
 * <p>
 *  * http://npo-gatekeeper-prd.cdn2.usvc.nepworldwide.nl/swagger-ui.html
 * <p>
 *  Swagger:   http://npo-gatekeeper-prd.cdn2.usvc.nepworldwide.nl/swagger-ui.html
 * <p>
 *  http://npo-gatekeeper-prd.cdn2.usvc.nepworldwide.nl/v2/api-docs
 */
@Slf4j
public class NEPGatekeeperServiceImpl implements NEPGatekeeperService {

    private static final HALMapper MAPPER = createMapper();

    public static HALMapper createMapper() {
        HALMapper mapper = new HALMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
    @Getter
    private final String url;

    @Getter
    private final String userName;

    private final String password;

    @Getter
    private final String ftpUserName;

    private HttpClientContext clientContext;

    @Getter
    private final Duration connectTimeout;
    @Getter
    private final Duration connectionRequestTimeout;
    @Getter
    private final Duration socketTimeout;

    @Getter
    private int pageSize = 200;

    CloseableHttpClient httpClient = null;


    public NEPGatekeeperServiceImpl(
        String url,
        String userName,
        String password,
        String connectTimeout,
        String connectionRequestTimeout,
        String socketTimeout,
        int pageSize,
        String ftpUserName) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.connectTimeout = parseDuration(connectTimeout).orElse(Duration.ofSeconds(1));
        this.connectionRequestTimeout = parseDuration(connectionRequestTimeout).orElse(this.connectTimeout);
        this.socketTimeout= parseDuration(socketTimeout).orElse(this.connectTimeout);
        this.pageSize = pageSize;
        this.ftpUserName = ftpUserName;
    }

    protected NEPGatekeeperServiceImpl(Properties properties) {
        this(properties.getProperty("nep.gatekeeper-api.baseUrl"),
            properties.getProperty("nep.gatekeeper-api.authorization.username"),
            properties.getProperty("nep.gatekeeper-api.authorization.password"),
            properties.getProperty("nep.gatekeeper-api.connectTimeout"),
            properties.getProperty("nep.gatekeeper-api.connectionRequestTimeout"),
            properties.getProperty("nep.gatekeeper-api.socketTimeout"),
            Integer.parseInt(properties.getProperty("nep.gatekeeper-api.pageSize")),
            properties.getProperty("nep.gatekeeper-upload.username")
        );
        init();
    }

    @PostConstruct
    protected void init() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

        // basic authentication
        AuthCache authCache = new BasicAuthCache();
        authCache.put(getHttpHost(), new BasicScheme());
        clientContext = HttpClientContext.create();
        clientContext.setCredentialsProvider(credentialsProvider);
        clientContext.setAuthCache(authCache);

        log.info("Created {}", this);
    }

    @Override
    @PreDestroy
    public synchronized void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
            httpClient = null;
        }
    }

    @NonNull
    @Override
    public WorkflowExecution transcode(
        @NonNull  WorkflowExecutionRequest request) throws IOException {
        UploadUtils.setPhase(UploadUtils.Phase.transcode_preparing);
        CloseableHttpClient client = getHttpClient();
        String json = MAPPER.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        HttpPost httpPost = new HttpPost(getWorkflowsEndPoint());
        httpPost.setEntity(entity);

        if (!request.getFilename().startsWith(ftpUserName)) {
            log.debug("The file given in {} does not start with ftp user name {}", request, ftpUserName);
        }
        log.info("Transcode request {}", json);
        try (CloseableHttpResponse response = client.execute(httpPost, clientContext);
              InputStream content =  response.getEntity().getContent()) {
            if (response.getStatusLine().getStatusCode() >= 300) {
                ByteArrayOutputStream body = new ByteArrayOutputStream();
                IOUtils.copy(content, body);
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\n" + json + "\n->\n" + body);
            }
            return MAPPER.readValue(content, WorkflowExecution.class);
        }
    }

    @NonNull
    @Override
    public Iterator<WorkflowExecution> getTranscodeStatuses(
        @Nullable String mid,
        @Nullable StatusType status,
        @Nullable Instant from,
        @Nullable Long limit) throws NEPException {
        final int batchSize = pageSize;
        URIBuilder builder;
        try {
            builder = new URIBuilder(getWorkflowsEndPoint());
        } catch (URISyntaxException e) {
            throw new NEPException(e, e.getMessage());
        }
        if (status != null) {
            builder.setParameter("status", status.name());
        }
        builder.addParameter("size", String.valueOf(batchSize));

        AtomicLong totalSize = new AtomicLong(-1);

        URIBuilder finalBuilder = builder;
        Supplier<Iterator<WorkflowExecution>> getter = new Supplier<>() {
            String next = finalBuilder.toString();

            @Override
            public Iterator<WorkflowExecution> get() {
                try {
                    if (next == null) {
                        return Collections.emptyIterator();
                    }
                    try (CloseableHttpResponse execute = executeGet(next)) {
                        if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                            WorkflowList list = MAPPER.readValue(execute.getEntity().getContent(), WorkflowList.class);
                            List<WorkflowExecution> workflowExecutions = list.getWorkflowExecutions().stream()
                                .filter((we) -> from == null || we.getStartTime().isAfter(from))
                                .toList();
                            totalSize.set(list.getTotalResults());
                            if (list.getNext() != null && workflowExecutions.size() == list.getWorkflowExecutions().size()) {
                                next = list.getNext().getHref();
                            } else {
                                next = null;
                            }
                            return workflowExecutions.iterator();
                        } else {
                            log.error("While getting transcode statuses for {} (from {}): {}", mid, next, execute.getStatusLine().toString());
                            execute.getEntity().writeTo(LoggerOutputStream.warn(log));
                        }

                    }
                } catch (IOException e) {
                    log.error("For {}: {}", next, e.getMessage(), e);
                }
                return null;
            }
        };
        BatchedReceiver<WorkflowExecution> br = BatchedReceiver.<WorkflowExecution>builder()
            .batchGetter(getter)
            .build();
        return new MaxOffsetIterator<>(
            FilteringIterator.<WorkflowExecution>builder()
                .filter(o -> mid == null || Objects.equals(o.getCustomerMetadata().getMid(), mid))
                .wrapped(br)
                .build(), limit, 0L);
    }

    @Override
    @SneakyThrows
    public @NonNull Optional<WorkflowExecution> getTranscodeStatus(@NonNull String workflowId) throws NEPException {
        final URIBuilder builder;
        try {
            builder = new URIBuilder(getWorkflowsEndPoint() + workflowId);
        } catch (URISyntaxException e) {
            throw new NEPException(e, e.getMessage());
        }
        try (CloseableHttpResponse closeableHttpResponse = executeGet(builder.toString())) {
            switch (closeableHttpResponse.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK -> {
                    return Optional.of(MAPPER.readValue(closeableHttpResponse.getEntity().getContent(), WorkflowExecution.class));
                }
                case HttpStatus.SC_NOT_FOUND -> {
                    return Optional.empty();
                }
                default -> {
                    StringWriter w = new StringWriter();
                    IOUtils.copy(closeableHttpResponse.getEntity().getContent(), w, StandardCharsets.UTF_8);
                    throw new IllegalStateException(closeableHttpResponse.getStatusLine() + ":" + w.toString());
                }
            }
        }
    }

    private HttpHost getHttpHost() {
        URI uri = URI.create(getWorkflowsEndPoint());
        return new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
    }

    private String getWorkflowsEndPoint() {
        return url + "/api/workflows/";
    }

    private CloseableHttpResponse executeGet(String u) throws IOException {
        if (clientContext == null) {
            throw new IllegalStateException("Not initialized");
        }
        log.debug("Executing {}", u);
        return getHttpClient().execute(new HttpGet(u), clientContext);
    }

    private synchronized CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            RequestConfig config = RequestConfig.custom()
                .setConnectTimeout((int) connectTimeout.toMillis())
                .setConnectionRequestTimeout((int) connectionRequestTimeout.toMillis())
                .setSocketTimeout((int) socketTimeout.toMillis())
                .build();
            httpClient = HttpClients.custom()
                .setDefaultRequestConfig(config)
                .build();
        }
        return httpClient;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getGatekeeperString();
    }

    @Override
    public String getGatekeeperString() {
        return  userName + "@" + getWorkflowsEndPoint();
    }
}
