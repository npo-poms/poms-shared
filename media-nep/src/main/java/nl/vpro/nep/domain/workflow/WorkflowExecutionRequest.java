package nl.vpro.nep.domain.workflow;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
public class WorkflowExecutionRequest implements Serializable {
    String mid;
    String broadcaster;
    String fileName;
    EncryptionType encryption;
    PriorityType priority;
    Type type;
    List<String> platforms;
}
