package nl.vpro.nep.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.logging.SimpleLogger;
import nl.vpro.nep.domain.ItemizeRequest;
import nl.vpro.nep.domain.ItemizeResponse;
import nl.vpro.nep.domain.workflow.StatusType;
import nl.vpro.nep.domain.workflow.WorkflowExecution;
import nl.vpro.nep.domain.workflow.WorkflowExecutionRequest;
import nl.vpro.nep.service.NEPFTPDownloadService;
import nl.vpro.nep.service.NEPFTPUploadService;
import nl.vpro.nep.service.NEPService;
import nl.vpro.nep.service.WorkflowExecutionService;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Service

public class NEPServiceImpl implements NEPService {
    private final WorkflowExecutionService workflowExecutionService;
    private final NEPFTPUploadService nepftpUploadService;
    private final NEPFTPDownloadService nepftpDownloadService;


    private final String itemizeKey;
    private final String itemizeUrl;

    @Inject
    public NEPServiceImpl(
        WorkflowExecutionService workflowExecutionService,
        NEPFTPUploadService nepftpUploadService,
        NEPFTPDownloadService nepftpDownloadService,

        @Value("${nep.player.itemize.key}") String itemizeKey,
        @Value("${nep.player.itemize.url}") String itemizeUrl
        ) {
        this.workflowExecutionService = workflowExecutionService;
        this.nepftpUploadService = nepftpUploadService;
        this.nepftpDownloadService = nepftpDownloadService;
        this.itemizeKey  = itemizeKey;
        this.itemizeUrl = itemizeUrl;
    }

    @Override
    public ItemizeResponse itemize(ItemizeRequest request) {
        CloseableHttpClient httpClient = HttpClients.custom()
            .build();
        HttpClientContext clientContext = HttpClientContext.create();

        try {
            String json = Jackson2Mapper.getLenientInstance().writeValueAsString(request);
            StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
            HttpPost httpPost = new HttpPost(itemizeUrl);
            httpPost.addHeader(new BasicHeader("Authentication", itemizeKey));
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, clientContext);

            if (response.getStatusLine().getStatusCode() >= 300) {
                ByteArrayOutputStream body = new ByteArrayOutputStream();
                IOUtils.copy(response.getEntity().getContent(), body);
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\n" + json + "\n->\n" + body);
            }

            return Jackson2Mapper.getLenientInstance().readValue(response.getEntity().getContent(), ItemizeResponse.class);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            IOUtils.closeQuietly(httpClient);
        }


    }

    @Override
    public WorkflowExecution execute(WorkflowExecutionRequest request) throws IOException {
        return workflowExecutionService.execute(request);

    }

    @Override
    public Iterator<WorkflowExecution> getStatuses(String mid, StatusType status, Instant from, Long limit) {
        return workflowExecutionService.getStatuses(mid, status, from, limit);

    }

    @Override
    public CompletableFuture<?> download(String nepFile) throws IOException {
        return nepftpDownloadService.download(nepFile);

    }

    @Override
    public CompletableFuture<?> upload(SimpleLogger logger, String nepFile, Long size, InputStream stream) {
        return nepftpUploadService.upload(logger, nepFile, size, stream);

    }
}
