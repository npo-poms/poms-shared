package nl.vpro.domain.media.update;


import lombok.Getter;
import lombok.ToString;

import javax.xml.bind.annotation.*;

@Getter
@XmlRootElement(name = "uploadResponse")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uploadResponseType")
@ToString
public class UploadResponse {

    @XmlAttribute
    int statusCode;

    @XmlAttribute
    String mid;

    String status;

    String response;

    Long bytes;

    public UploadResponse(String mid, int statusCode, String status, String response, Long bytes) {
        this.statusCode = statusCode;
        this.status = status;
        this.response = response;
        this.mid = mid;
        this.bytes = bytes;
    }
    private UploadResponse() {

    }

    public boolean isSuccessFull() {
        return statusCode >= 200 && statusCode < 300;
    }
}
