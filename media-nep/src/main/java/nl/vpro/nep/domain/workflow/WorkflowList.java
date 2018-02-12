package nl.vpro.nep.domain.workflow;

import io.openapitools.jackson.dataformat.hal.annotation.EmbeddedResource;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import lombok.Getter;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Resource
public class WorkflowList {

    @EmbeddedResource("wf:workflowExecution")
    @Getter
    List<WorkflowExecution> workflowExecutions;

    @Link
    String self;

    @Link
    String next;


}
