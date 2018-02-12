package nl.vpro.nep.service;


import io.openapitools.jackson.dataformat.hal.HALMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import nl.vpro.nep.domain.workflow.*;
import nl.vpro.util.BatchedReceiver;

@Slf4j
public class WorkflowExecutionServiceImpl implements NEPService {

    public static final HALMapper MAPPER = new HALMapper();
    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.registerModule(new JavaTimeModule());

    }

    private String URL = "http://npo-gatekeeper-acc.cdn1.usvc.nepworldwide.nl/api/workflows/";
    private String USERNAME = "user";
    private String PASSWORD = "***REMOVED***";

    @Override
    public WorkflowExecutionResponse execute(String mid, Type type, List<String> platforms, String fileName, EncryptionType encryption, PriorityType priority) throws IOException {

        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
                .mid(mid)
                .fileName(fileName)
                .encryption(encryption)
                .priority(priority)
                .type(type)
                .platforms(platforms)
                .build();

        CloseableHttpClient client = getHttpClient();

        ObjectMapper mapper = new ObjectMapper();
        try {
            StringEntity entity = new StringEntity(mapper.writeValueAsString(request), ContentType.APPLICATION_FORM_URLENCODED);
            HttpPost httpPost = new HttpPost(URL);
            httpPost.setEntity(entity);
            HttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
            }

            BufferedReader br = new BufferedReader(
                    new InputStreamReader((response.getEntity().getContent())));

            return mapper.readValue(IOUtils.toString(br), WorkflowExecutionResponse.class);

        } catch (JsonProcessingException e) {
            throw e;
        }
    }


    @Override
    public Iterator<WorkflowExecution> getStatuses(String mid, StatusType status, Long limit) {
        Supplier<Iterator<WorkflowExecution>> getter = new Supplier<Iterator<WorkflowExecution>>() {
            @Override
            public Iterator<WorkflowExecution> get() {
                try {
                    URIBuilder builder = new URIBuilder(URL);
                    if (status != null) {
                        builder.setParameter("status", status.name());
                    }

                    HttpGet get = new HttpGet(builder.toString());
                    try (CloseableHttpResponse execute = getHttpClient().execute(get)) {
                        WorkflowList list = MAPPER.readValue(execute.getEntity().getContent(), WorkflowList.class);
                        return list.getWorkflowExecutions().iterator();
                        ///return execute.getEntity().getContent();
                    }

                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
                return null;
            }
        };
        BatchedReceiver br = BatchedReceiver.<WorkflowExecution>builder()
            .batchGetter(getter)
            .build();
        return br;
    }

    private CloseableHttpClient getHttpClient() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USERNAME, PASSWORD));
        return HttpClients.custom()
            .setDefaultCredentialsProvider(credentialsProvider)
            .build();
    }
}
