package nl.vpro.nep.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.vpro.nep.domain.workflow.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WorkflowExecutionService {

    private String URL = "http://npo-gatekeeper-acc.cdn1.usvc.nepworldwide.nl/api/workflows/";
    private String USERNAME = "user";
    private String PASSWORD = "secret";

    public WorkflowExecutionResponse execute(String mid, String type, List<String> platforms, String fileName, String encryption, String priority) throws Exception{

        WorkflowExecutionRequest request = WorkflowExecutionRequest.builder()
                .mid(mid)
                .fileName(fileName)
                .encryption(EncryptionType.valueOf(encryption.toUpperCase()))
                .priority((PriorityType.valueOf(priority.toUpperCase())))
                .type(Type.valueOf(type.toUpperCase()))
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

    private CloseableHttpClient getHttpClient() {
        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(USERNAME, PASSWORD));
        return HttpClients.custom()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();
    }
}
