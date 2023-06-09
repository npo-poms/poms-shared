package nl.vpro.poms.shared;

import lombok.SneakyThrows;

import java.util.Properties;

import nl.vpro.util.Env;

/**
 * These are currently known deployments.
 * <p>
 * They can e.g. be used to determine the base url of the apis.
 */
public enum Deployment {

    media,
    media_api_backend,

    api,

    images,
    images_backend,

    pages_publisher,
    media_publisher;

    final Properties properties = new Properties();
    @SneakyThrows
    Deployment() {
        properties.load(Deployment.class.getResourceAsStream("/poms-urls.properties"));

    }
    public String getBaseUrl(Env env) {
        return properties.getProperty("npo-" + name() + ".baseUrl." + env.name().toLowerCase(),
            properties.getProperty("npo-" + name() + ".baseUrl"));
    }


}
