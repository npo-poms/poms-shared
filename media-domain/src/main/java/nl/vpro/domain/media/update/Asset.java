/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Date;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Embargo;
import nl.vpro.util.DateUtils;

/**
 * @author Roelof Jan Koekoek
 * @since 2.1
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "assetType", propOrder = {
    "source"
})
public class Asset implements Embargo {

    @XmlAttribute
    private Date publishStart;

    @XmlAttribute
    private Date publishStop;

    @XmlElements(value = {
        @XmlElement(name = "assetData",  type = AssetData.class),
        @XmlElement(name = "assetLocation", type = AssetLocation.class)
    })
    @NotNull
    @Valid
    private AssetSource source;

    public Asset() {
    }

    public Asset(Date publishStart, Date publishStop) {
        this.publishStart = publishStart;
        this.publishStop = publishStop;
    }

    public boolean isLocationAsset() {
        return source != null && source instanceof AssetLocation;
    }

    public boolean isDataAsset() {
        return source != null && source instanceof AssetData;
    }

    public void resolve(String parent) throws URISyntaxException {
        if(isLocationAsset()) {
            ((AssetLocation)source).resolve(parent);
        }
    }

    public Date getPublishStart() {
        return publishStart;
    }

    public void setPublishStart(Date publishStart) {
        this.publishStart = publishStart;
    }

    public Date getPublishStop() {
        return publishStop;
    }

    public void setPublishStop(Date publishStop) {
        this.publishStop = publishStop;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(AssetSource source) {
        this.source = source;
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

    @Override
    public Instant getEmbargoStart() {
        return DateUtils.toInstant(getPublishStart());

    }

    @Override
    public Asset setEmbargoStart(Instant publishStart) {
        setPublishStart(DateUtils.toDate(publishStart));
        return this;

    }

    @Override
    public Instant getEmbargoStop() {
        return DateUtils.toInstant(getPublishStop());

    }

    @Override
    public Asset setEmbargoStop(Instant publishStop) {
        setPublishStop(DateUtils.toDate(publishStop));
        return null;

    }
}
