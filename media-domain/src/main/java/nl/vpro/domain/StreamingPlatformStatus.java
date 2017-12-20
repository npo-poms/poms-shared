package nl.vpro.domain;

import lombok.Getter;
import lombok.Setter;

public class StreamingPlatformStatus {

    @Getter
    @Setter
    private String status;

    public StreamingPlatformStatus(String status) {
        this.status = status;
    }
}
