package nl.vpro.nep.service;

import lombok.SneakyThrows;

import java.time.Duration;
import java.util.function.Consumer;

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
    NEPDownloadService,
    NEPUploadService,
    NEPItemizeService,
    NEPSAMService,
    NEPPlayerTokenService {



    enum RateRapType {
        ITEMIZE,

    }

    interface RateRapHandling extends Consumer<Duration> {
    }

    public static RateRapHandling DELAY = new RateRapHandling() {
        @SneakyThrows
        @Override
        public void accept(Duration duration) {
            Thread.sleep(duration.toMillis());
        }
    };

    ThreadLocal<RateRapHandling> handling  = ThreadLocal.withInitial(() -> DELAY);

}
