package nl.vpro.nep.service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vpro.nep.domain.workflow.*;

public class WorkflowExecutionServiceImpl implements NEPService {

    private String URL = "http://npo-gatekeeper-acc.cdn1.usvc.nepworldwide.nl/api/workflows/";
    private String USERNAME = "user";
    private String PASSWORD = "secret";

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
    public List<WorkflowExecution> getStatus(String mid) {
        // TODO
        return new ArrayList<>();

    }

    private CloseableHttpClient getHttpClient() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USERNAME, PASSWORD));
        return HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }
}
