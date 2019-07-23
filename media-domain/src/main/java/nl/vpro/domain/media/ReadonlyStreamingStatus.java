package nl.vpro.domain.media;

import lombok.Getter;

import java.time.Instant;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Getter
public class ReadonlyStreamingStatus implements StreamingStatus {


    private final Value withDrm;

    private final Instant withDrmOffline;

    private final Value withoutDrm;

    private final Instant withoutDrmOffline;


    public ReadonlyStreamingStatus(Value withDrm, Instant withDrmOffline, Value withoutDrm, Instant withoutDrmOffline) {
        this.withDrm = withDrm;
        this.withDrmOffline = withDrmOffline;
        this.withoutDrm = withoutDrm;
        this.withoutDrmOffline = withoutDrmOffline;
    }
}
