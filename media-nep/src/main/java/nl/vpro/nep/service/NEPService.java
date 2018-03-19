package nl.vpro.nep.service;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPService extends
    WorkflowExecutionService,
    NEPFTPDownloadService,
    NEPFTPUploadService,
    ItemizeService{
}
