/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

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
    private DataHandler data;

    public DataHandler getData() {
        return data;
    }

    public void setData(DataHandler data) {
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
