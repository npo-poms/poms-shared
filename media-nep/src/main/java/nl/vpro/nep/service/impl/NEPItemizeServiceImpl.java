package nl.vpro.nep.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Named;

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

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.nep.domain.NEPItemizeRequest;
import nl.vpro.nep.domain.NEPItemizeResponse;
import nl.vpro.nep.service.NEPItemizeService;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Named("NEPItemizeService")
public class NEPItemizeServiceImpl implements NEPItemizeService {
    private final String itemizeKey;
    private final String itemizeUrl;

    private final ContentType JSON = ContentType.APPLICATION_JSON.withCharset(Charset.forName("UTF-8"));

    @Inject
    public NEPItemizeServiceImpl(
        @Value("${nep.player.itemizer.url}") String itemizeUrl,
        @Value("${nep.player.itemizer.key}") String itemizeKey) {

        this.itemizeKey = itemizeKey;
        this.itemizeUrl = itemizeUrl;
    }

    @Override
    public NEPItemizeResponse itemize(NEPItemizeRequest request) {
        try(CloseableHttpClient httpClient = HttpClients.custom()
            .build()) {
            HttpClientContext clientContext = HttpClientContext.create();
            String json = Jackson2Mapper.getLenientInstance().writeValueAsString(request);
            StringEntity entity = new StringEntity(json, JSON);
            HttpPost httpPost = new HttpPost(itemizeUrl);
            httpPost.addHeader(new BasicHeader("Authorization", itemizeKey));
            httpPost.addHeader(new BasicHeader("Accept", JSON.toString()));

            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, clientContext);

            if (response.getStatusLine().getStatusCode() >= 300) {
                ByteArrayOutputStream body = new ByteArrayOutputStream();
                IOUtils.copy(response.getEntity().getContent(), body);
                throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode() + "\n" + json + "\n->\n" + body);
            }

            return Jackson2Mapper.getLenientInstance().readValue(response.getEntity().getContent(), NEPItemizeResponse.class);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

    }

    @Override
    public String toString() {
        return itemizeUrl;
    }
}
