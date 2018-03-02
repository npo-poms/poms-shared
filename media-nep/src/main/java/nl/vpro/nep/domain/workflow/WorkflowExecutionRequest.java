package nl.vpro.nep.domain.workflow;

import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@lombok.Builder(builderClassName = "Builder")
@Getter

public class WorkflowExecutionRequest implements Serializable {
    String mid;
    String filename;
    EncryptionType encryption;
    PriorityType priority;
    @lombok.Builder.Default
    Type type = Type.VIDEO;
    @lombok.Builder.Default
    List<String> platforms = new ArrayList<>();
}
