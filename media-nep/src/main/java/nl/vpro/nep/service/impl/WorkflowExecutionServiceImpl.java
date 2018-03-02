package nl.vpro.nep.service.impl;


import io.openapitools.jackson.dataformat.hal.HALMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.nep.domain.workflow.StatusType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;
import nl.vpro.nep.domain.workflow.WorkflowList;
import nl.vpro.nep.service.NEPService;
import nl.vpro.util.BatchedReceiver;
import nl.vpro.util.FilteringIterator;
import nl.vpro.util.MaxOffsetIterator;

@Slf4j
@Service
public class WorkflowExecutionServiceImpl implements NEPService {

    public static final HALMapper MAPPER = new HALMapper();

    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.registerModule(new JavaTimeModule());
    }

    @Value("${nep.workflows.url}")
    private String url;
    @Value("${nep.workflows.authorisation.username}")
    private String userName;
    @Value("${nep.workflows.authorisation.password}")
    private String password;

    private HttpClientContext clientContext;

    @PostConstruct
    public void init() {
        URI uri = URI.create(url);
        HttpHost host = new HttpHost(uri.getHost());
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));

        // basic authentication
        AuthCache authCache = new BasicAuthCache();
        authCache.put(host, new BasicScheme());
        clientContext = HttpClientContext.create();
        clientContext.setCredentialsProvider(credentialsProvider);
        clientContext.setAuthCache(authCache);
    }

    @Override
    public WorkflowExecution execute(WorkflowExecutionRequest request) throws IOException {
        CloseableHttpClient client = getHttpClient();

        try {
            String json = MAPPER.writeValueAsString(request);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);

            HttpResponse response = client.execute(httpPost, clientContext);

            if (response.getStatusLine().getStatusCode() >= 300) {
                ByteArrayOutputStream body = new ByteArrayOutputStream();
                IOUtils.copy(response.getEntity().getContent(), body);
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\n" + json + "\n->\n" + body);
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            return MAPPER.readValue(IOUtils.toString(br), WorkflowExecution.class);

        } catch (JsonProcessingException e) {
            throw e;
        }
    }

    @Override
    public Iterator<WorkflowExecution> getStatuses(String mid, StatusType status, Instant from, Long limit) {
        int batchSize = 20;
        URIBuilder builder;
        try {
            builder = new URIBuilder(url);
            if (status != null) {
                builder.setParameter("status", status.name());
            }
            builder.addParameter("size", String.valueOf(batchSize));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        AtomicLong totalSize = new AtomicLong(-1);

        Supplier<Iterator<WorkflowExecution>> getter = new Supplier<Iterator<WorkflowExecution>>() {
            String next = builder.toString();

            @Override
            public Iterator<WorkflowExecution> get() {
                try {
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
                            throw new RuntimeException(execute.getStatusLine().toString());
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
            .batchSize(batchSize)
            .build();
        return new MaxOffsetIterator<>(
            FilteringIterator.<WorkflowExecution>builder()
                .filter(o -> mid == null || Objects.equals(o.getCustomerMetadata().getMid(), mid))
                .wrapped(br)
                .build(), limit, 0L);
    }

    CloseableHttpResponse executeGet(String url) throws IOException {
        if (clientContext == null) {
            throw new IllegalStateException("Not initialized");
        }
        return getHttpClient().execute(new HttpGet(url), clientContext);
    }
    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
            .build();
    }
}