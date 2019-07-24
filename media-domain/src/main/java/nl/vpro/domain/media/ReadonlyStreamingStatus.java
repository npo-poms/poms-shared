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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamingStatus)) return false;

        StreamingStatus that = (StreamingStatus) o;

        if (withDrm != that.getWithDrm()) return false;
        return withoutDrm == that.getWithoutDrm();
    }

    @Override
    public String toString() {
         return withDrm + (withDrmOffline != null ? ("(-" + withDrmOffline + ")") : "") +  "_" +
             withoutDrm + (withoutDrmOffline != null ? ("(-" + withoutDrmOffline + ")") : "");
     }


}
