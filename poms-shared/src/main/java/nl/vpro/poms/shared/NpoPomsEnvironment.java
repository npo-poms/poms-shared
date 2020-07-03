package nl.vpro.poms.shared;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author Michiel Meeuwissen
 * @since 3.5
 */
public class NpoPomsEnvironment {


    @Value("${npo-api.baseUrl:}")
    private String apiBase;

    @Value("${media.rs.url}")
    protected String mediaRsUrl;

    @Value("${media.url}")
    protected String mediaUrl;

    private Env env = Env.PROD;



    @PostConstruct
    public void initialize() {
        if (env == null) {
            if (apiBase.contains("-dev.")) {
                env = Env.DEV;
            } else if (apiBase.contains("-test.")) {
                env = Env.TEST;
            } else {
                env = Env.PROD;
            }
        }
    }


    public URL getImageUrl(String format, String id) {
        try {
            return new URL(appendUrl(env.images , "image/" + format + id + ".jpg"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getMediaRsUrl() {
        if (StringUtils.isNotBlank(mediaRsUrl)) {
            return mediaRsUrl;
        }
        return env.mediars;
    }

    public String getMediaUrl() {
        if (StringUtils.isNotBlank(mediaRsUrl)) {
            return mediaUrl;
        }
        return env.poms;
    }

    public String getMediaSelectorUrl() {
        return appendUrl(getMediaUrl(), "CMSSelector/media.js");
    }

    public void fillProperties(Map<String, String> properties) {
        if (! properties.containsKey("media.url")) {
            properties.put("media.url", getMediaUrl());
        }
        if (!properties.containsKey("media.selector.url")) {
            properties.put("media.selector.url", getMediaSelectorUrl());
        }

    }

    public enum Env {
        LOCALHOST("http://localhost:8071/", "http://localhost:8071/images/", "http://localhost:8071/rs/"),
        DEV("http://poms-dev.omroep.nl/", "http://images-dev.poms.omroep.nl/", "https://api-dev.poms.omroep.nl/"),
        TEST("http://poms-test.omroep.nl/", "http://images-test.poms.omroep.nl/", "https://api-test.poms.omroep.nl/"),
        PROD("http://poms.omroep.nl/", "http://images.poms.omroep.nl/", "https://api.poms.omroep.nl/");
        private final String poms;
        private final String images;
        private final String mediars;


        Env(String poms, String images, String mediars) {
            this.poms = poms;
            this.images = images;
            this.mediars = mediars;
        }
    }

    private String appendUrl(String url, String sub) {
        if (url.endsWith("/")) {
            return url + sub;
        } else {
            return url + "/" + sub;
        }
    }

}
