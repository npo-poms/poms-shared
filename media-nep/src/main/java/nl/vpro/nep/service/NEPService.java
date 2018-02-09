package nl.vpro.nep.service;

import java.io.IOException;
import java.util.List;

import nl.vpro.nep.domain.workflow.WorkflowExecutionResponse;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPService {

    WorkflowExecutionResponse execute(String mid, String type, List<String> platforms, String fileName, String encryption, String priority) throws IOException;

}
