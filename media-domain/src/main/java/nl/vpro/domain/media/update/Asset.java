/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.Instant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.MutableEmbargo;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assetType", propOrder = {
    "source"
})
public class Asset implements MutableEmbargo<Asset> {

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStart;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    private Instant publishStop;

    @Setter
    @Getter
    @XmlElements(value = {
        @XmlElement(name = "assetData",  type = AssetData.class),
        @XmlElement(name = "assetLocation", type = AssetLocation.class)
    })
    @NotNull
    @Valid
    private AssetSource source;

    public Asset() {
    }

    public Asset(Instant publishStart, Instant publishStop) {
        this.publishStart = publishStart;
        this.publishStop = publishStop;
    }

    public boolean isLocationAsset() {
        return source instanceof AssetLocation;
    }

    public boolean isDataAsset() {
        return source instanceof AssetData;
    }

    public void resolve(String parent) throws URISyntaxException {
        if(isLocationAsset()) {
            ((AssetLocation)source).resolve(parent);
        }
    }

    @Override
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @NonNull
    @Override
    public Asset setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return this;
    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @NonNull
    @Override
    public Asset setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return this;
    }

    public InputStream getInputStream() {
        return source.getInputStream();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Asset{");
        if(publishStart != null) {
            sb.append("publishStart=").append(publishStart);
        }
        if(publishStop != null) {
            if(publishStart != null) {
                sb.append(", ").append(publishStart);
            }
            sb.append("publishStop=").append(publishStop);
        }
        sb.append('}');
        return sb.toString();
    }

    }
