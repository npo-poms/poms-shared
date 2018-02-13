package nl.vpro.nep.domain.workflow;

import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;

@lombok.Builder(builderClassName = "Builder")
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
@Resource
@ToString
public class WorkflowExecution {

    @Link
    HALLink self;
    @Getter
    String workflowId;
    @Getter
    StatusType status;
    @Getter
    String statusMessage;
    @Getter
    String workflowType;
    @Getter
    CustomerMetadata customerMetadata;
    @Getter
    Instant startTime;
    @Getter
    Instant updateTime;
    @Getter
    Instant endTime;

    public static class Builder {

        public Builder mid(String mid) {
            if (customerMetadata == null) {
                customerMetadata = new CustomerMetadata();
            }
            customerMetadata.setMid(mid);
            return this;
        }
    }


    public String getMid() {
        if (customerMetadata != null) {
            return customerMetadata.getMid();
        } else {
            return null;
        }
    }
}
