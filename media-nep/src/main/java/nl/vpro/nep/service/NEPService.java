package nl.vpro.nep.service;

/**
 *  Wraps all NEP services ({@link NEPGatekeeperService}, {@link NEPDownloadService}, {@link NEPUploadService}, {@link NEPItemizeService}, {@link NEPSAMService}, {@link NEPPlayerTokenService}) in one service.
 * These things are often related, and it is handy to be able to wire just one service.
 *
 * @see nl.vpro.nep.service.impl.NEPServiceImpl
 * @author Michiel Meeuwissen
 * @since 5.6
 */
public interface NEPService extends
    NEPGatekeeperService,
    NEPSourceServiceIngestService,
    NEPDownloadService,
    NEPUploadService,
    NEPItemizeService,
    NEPSAMService,
    NEPPlayerTokenService {

}
