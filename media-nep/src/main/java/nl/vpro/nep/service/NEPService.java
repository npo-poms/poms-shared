package nl.vpro.nep.service;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import nl.vpro.nep.domain.workflow.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPService {

    WorkflowExecutionResponse execute(
        String mid,
        Type type,
        List<String> platforms, String fileName, EncryptionType encryption, PriorityType priority) throws IOException;


    Iterator<WorkflowExecution> getStatuses(String mid, StatusType status, Long limit);
}
