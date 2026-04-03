package nl.vpro.domain.media.update;


import lombok.Getter;
import lombok.ToString;

import jakarta.xml.bind.annotation.*;

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

    String version;

    Boolean retryable;

    public UploadResponse(String mid, int statusCode, String status, String response, Long bytes, String version, Boolean retryable) {
        this.statusCode = statusCode;
        this.status = status;
        this.response = response;
        this.mid = mid;
        this.bytes = bytes;
        this.version = version;
        this.retryable = retryable;
    }
    private UploadResponse() {

    }

    public boolean isSuccessFull() {
        return statusCode >= 200 && statusCode < 300;
    }

    public boolean isRetryable() {
        return retryable == null || retryable;
    }
}
