/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assetLocationType", propOrder = {
    "url"
})
public class AssetLocation implements AssetSource {

    @XmlElement(namespace = Xmlns.UPDATE_NAMESPACE)
    @NotNull(message = "provide asset location")
    @nl.vpro.validation.URI
    private String url;

    private AssetLocation() {
    }

    public AssetLocation(String url) {
        this.url = url;
    }

    public void resolve(String basePath) throws URISyntaxException {
        if(this.url.contains("..")) {
            throw new SecurityException("Assets should reside in a sub-folder, navigating up is not allowed");
        }

        URI url = new URI(this.url);
        if(url.isAbsolute() && !"file".equals(url.getScheme())) {
            return;
        }

        if(url.getSchemeSpecificPart().startsWith("/")) {
            throw new IllegalArgumentException("Must provide a relative url for an asset");
        }

        URI base = new URI("file", null, basePath + '/', null);

        this.url = base.resolve(url.getSchemeSpecificPart()).toString();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public InputStream getInputStream() {
        try {
            URL url = URI.create(this.url).toURL();
            URLConnection urlConnection = url.openConnection();

            if(urlConnection instanceof HttpURLConnection) {
                HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                huc.setConnectTimeout(10000);
                huc.setReadTimeout(20000);
            }

            return urlConnection.getInputStream();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "AssetLocation" +
            "{url='" + url + '\'' +
            '}';
    }
}
