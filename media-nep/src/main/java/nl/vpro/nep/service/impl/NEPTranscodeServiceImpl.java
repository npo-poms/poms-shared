package nl.vpro.nep.service.impl;


import io.openapitools.jackson.dataformat.hal.HALMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.logging.LoggerOutputStream;
import nl.vpro.nep.domain.workflow.StatusType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;
import nl.vpro.nep.domain.workflow.WorkflowList;
import nl.vpro.nep.service.NEPTranscodeService;
import nl.vpro.util.BatchedReceiver;
import nl.vpro.util.FilteringIterator;
import nl.vpro.util.MaxOffsetIterator;

@Slf4j
@Named("NEPTranscodeService")
public class NEPTranscodeServiceImpl implements NEPTranscodeService {

    public static final HALMapper MAPPER = new HALMapper();

    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.registerModule(new JavaTimeModule());
    }


    private final String url;

    private final String userName;

    private final String password;

    private final String ftpUserName;

    private HttpClientContext clientContext;

    @Inject
    public NEPTranscodeServiceImpl(
         @Value("${nep.api.baseUrl}") String url,
         @Value("${nep.api.authorization.username}") String userName,
         @Value("${nep.api.authorization.password}") String password,
         @Value("${nep.transcode.sftp.username}") String ftpUserName) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        this.ftpUserName = ftpUserName;
    }

    @PostConstruct
    public void init() {
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

    @Nonnull
    @Override
    public WorkflowExecution transcode(
        @Nonnull  WorkflowExecutionRequest request) throws IOException {
        CloseableHttpClient client = getHttpClient();
        String json = MAPPER.writeValueAsString(request);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        HttpPost httpPost = new HttpPost(getWorkflowsEndPoint());
        httpPost.setEntity(entity);

        if (!request.getFilename().startsWith(ftpUserName)) {
            log.warn("The file given in {} does not start with ftp user name {}", request, ftpUserName);
        }
        log.info("Transcode request {}", json);
        HttpResponse response = client.execute(httpPost, clientContext);

        if (response.getStatusLine().getStatusCode() >= 300) {
            ByteArrayOutputStream body = new ByteArrayOutputStream();
            IOUtils.copy(response.getEntity().getContent(), body);
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\n" + json + "\n->\n" + body);
        }

        return MAPPER.readValue(response.getEntity().getContent(), WorkflowExecution.class);
    }

    @Nonnull
    @Override
    @SneakyThrows
    public Iterator<WorkflowExecution> getTranscodeStatuses(
        @Nullable String mid,
        @Nullable StatusType status,
        @Nullable Instant from,
        @Nullable Long limit) {
        int batchSize = 20;
        URIBuilder builder = new URIBuilder(getWorkflowsEndPoint());
        if (status != null) {
            builder.setParameter("status", status.name());
        }
        builder.addParameter("size", String.valueOf(batchSize));

        AtomicLong totalSize = new AtomicLong(-1);

        Supplier<Iterator<WorkflowExecution>> getter = new Supplier<Iterator<WorkflowExecution>>() {
            String next = builder.toString();

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
                                .collect(Collectors.toList());
                            totalSize.set(list.getTotalResults());
                            if (list.getNext() != null && workflowExecutions.size() == list.getWorkflowExecutions().size()) {
                                next = list.getNext().getHref();
                            } else {
                                next = null;
                            }
                            return workflowExecutions.iterator();
                        } else {
                            execute.getEntity().writeTo(LoggerOutputStream.warn(log));
                            log.error("While getting trancodestatuses for {} (from {}): {}", mid, builder.toString(), execute.getStatusLine().toString());
                        }
                    }

                } catch (IOException e) {
                    log.error(e.getMessage(), e);
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
        return getHttpClient().execute(new HttpGet(u), clientContext);
    }
    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
            .build();
    }

    @Override
    public String toString() {
        return NEPTranscodeServiceImpl.class.getName() + " " + userName + "@" + getWorkflowsEndPoint();
    }
}
