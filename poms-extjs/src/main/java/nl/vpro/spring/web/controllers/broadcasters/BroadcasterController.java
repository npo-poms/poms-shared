package nl.vpro.spring.web.controllers.broadcasters;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.domain.user.Organization;

/**
 * Straight-forward servlet that serves our broadcasters id mapping to the outside world. Just as key/value's. Used by npo-publish. Used to be used by junction too.
 * @author Michiel Meeuwissen
 * @since 1.8
 */
@Controller
@RequestMapping(value = "/", produces = org.springframework.http.MediaType.TEXT_PLAIN_VALUE)
public class BroadcasterController {

    private static final Logger LOG = LoggerFactory.getLogger(BroadcasterController.class);

    private final Map<String, Response> responses = new ConcurrentHashMap<>();

    private final Integer expiry = 3600;

    BroadcasterService broadcasterService;

    @Autowired
    public BroadcasterController(BroadcasterService broadcasterService) {
        this.broadcasterService = broadcasterService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public void broadcasters(
        @RequestHeader(value = HttpHeaders.IF_MODIFIED_SINCE, required = false) Date ifModifiedSince,
        HttpServletResponse response) throws IOException {

        sendAnswer(ifModifiedSince, "", getProperties(), "id, displayName", response);
    }

    @RequestMapping(value = "/{owner}", method = RequestMethod.GET)
    public void broadcastersByType(
        @PathVariable(value = "owner") String owner,
        @RequestHeader(value = HttpHeaders.IF_MODIFIED_SINCE, required = false) Date ifModifiedSince,
        HttpServletResponse response) throws IOException {

        sendAnswer(ifModifiedSince, owner, getProperties(owner), "id, " + owner + " id", response);
    }

    protected void sendAnswer(Date ifModifiedSince, String key, Properties properties, String propertiesHeader, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_PLAIN);

        Response answer = responses.get(key);
        if (answer == null) {
            answer = new Response(properties);
            responses.put(key, answer);
        } else {
            if (!answer.properties.equals(properties)) {
                answer = new Response(properties);
                responses.put(key, answer);
                LOG.info("New broadcaster properties {} at {}", (StringUtils.isEmpty(key) ? "" : "(" + key + ")"), answer.lastModified);
            }
            if (ifModifiedSince != null) {
                if (! answer.lastModified.isAfter(ifModifiedSince.toInstant())) {
                    response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    return;
                }
            }

        }
        if (expiry != null && expiry > 0) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, "public, max-age: " + expiry);
        }
        response.setDateHeader(HttpHeaders.LAST_MODIFIED, answer.lastModified.toEpochMilli());
        answer.properties.store(response.getOutputStream(), propertiesHeader);
    }

    protected static class Response {
        private final Properties properties;
        private final Instant lastModified = Instant.now().truncatedTo(ChronoUnit.SECONDS);


        public Response(Properties properties) {
            this.properties = properties;
        }
    }

    private Properties getProperties() {
        Properties props = new Properties();
        for (Organization org : broadcasterService.findAll()) {
            props.put(org.getId(), org.getDisplayName());
        }
        return props;
    }

    private Properties getProperties(String owner) {
        Properties props = new Properties();
        OwnerType ot = OwnerType.valueOf(owner.toUpperCase());
        for (Broadcaster org : broadcasterService.findAll()) {
            switch (ot) {
                case NEBO:
                    props.put(org.getId(), org.getNeboId());
                    break;
                case MIS:
                    props.put(org.getId(), org.getMisId());
                    break;
                case WHATS_ON:
                    props.put(org.getId(), org.getWhatsOnId());
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return props;
    }
}
