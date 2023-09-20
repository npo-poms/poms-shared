/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.Setter;

import javax.activation.DataHandler;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "imageDataType",
    propOrder = {
        "data"
        })
public class ImageData {

    @XmlElement
    @XmlMimeType("application/octet-stream")
    @NotNull(message = "provide image data")
    @Getter
    @Setter
    private DataHandler data;

    public ImageData() {

    }

    public ImageData(DataHandler data) {
        this.data = data;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ImageData");
        sb.append("{}");
        return sb.toString();
    }
}
