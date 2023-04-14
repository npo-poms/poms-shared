package nl.vpro.domain.media.update;


import lombok.Getter;

import javax.xml.bind.annotation.*;

@Getter
@XmlRootElement(name = "uploadResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadResponse {

    @XmlAttribute
    int statusCode;

    String status;

    String response;

    public UploadResponse(int statusCode, String status, String response) {
        this.statusCode = statusCode;
        this.status = status;
        this.response = response;
    }
    private UploadResponse() {

    }
}
