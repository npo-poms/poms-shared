package nl.vpro.nep.domain.workflow;

import io.openapitools.jackson.dataformat.hal.HALLink;
import io.openapitools.jackson.dataformat.hal.annotation.EmbeddedResource;
import io.openapitools.jackson.dataformat.hal.annotation.Link;
import io.openapitools.jackson.dataformat.hal.annotation.Resource;
import lombok.Data;
import lombok.Getter;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Resource
@Data
public class WorkflowList {

    @EmbeddedResource("wf:workflowExecution")
    @Getter
    List<WorkflowExecution> workflowExecutions;

    Long totalResults;

    @Link
    HALLink self;

    @Link
    @Getter
    HALLink next;




}
