package nl.vpro.nep.service;

import nl.vpro.nep.domain.ItemizeRequest;
import nl.vpro.nep.domain.ItemizeResponse;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPService extends
    WorkflowExecutionService,
    NEPFTPDownloadService,
    NEPFTPUploadService {
    ItemizeResponse itemize(ItemizeRequest request);
}
