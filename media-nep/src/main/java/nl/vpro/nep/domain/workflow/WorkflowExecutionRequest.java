package nl.vpro.nep.domain.workflow;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@lombok.Builder(builderClassName = "Builder")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

public class WorkflowExecutionRequest implements Serializable {
    private String mid;
    private String filename;
    private EncryptionType encryption;
    private PriorityType priority;
    @lombok.Builder.Default
    private Type type = Type.VIDEO;
    @lombok.Builder.Default
    private List<String> platforms = new ArrayList<>();


    public WorkflowExecutionRequest() {

    }

}
