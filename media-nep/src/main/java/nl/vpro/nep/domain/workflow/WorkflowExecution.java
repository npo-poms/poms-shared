package nl.vpro.nep.domain.workflow;

import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import lombok.*;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonIgnore;

@lombok.Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Resource
@ToString
public class WorkflowExecution {

    public static final WorkflowExecution UNKNOWN = WorkflowExecution.builder()
        .status(StatusType.UNKNOWN)
        .statusMessage("Nog niet bekend")
        .build();

    @Link
    private HALLink self;
    @Getter
    private String workflowId;
    @Getter
    private StatusType status;
    @Getter
    private String statusMessage;
    @Getter
    private String workflowType;
    @Getter
    private CustomerMetadata customerMetadata;
    @Getter
    private Instant startTime;
    @Getter
    private Instant updateTime;
    @Getter
    private Instant endTime;

    public static class Builder {
        public Builder mid(String mid) {
            if (customerMetadata == null) {
                customerMetadata = new CustomerMetadata();
            }
            customerMetadata.setMid(mid);
            return this;
        }
    }

    public WorkflowExecution() {
    }

    @JsonIgnore
    public String getMid() {
        if (customerMetadata != null) {
            return customerMetadata.getMid();
        } else {
            return null;
        }
    }
}
