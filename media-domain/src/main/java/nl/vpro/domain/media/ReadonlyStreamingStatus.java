package nl.vpro.domain.media;

import lombok.Getter;

import java.io.Serial;
import java.time.Instant;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Getter
public class ReadonlyStreamingStatus implements StreamingStatus {

    @Serial
    private static final long serialVersionUID = 3307679919618834468L;

    private final Value withDrm;

    private final Instant withDrmOffline;

    private final Value withoutDrm;

    private final Instant withoutDrmOffline;

    private final Value audioWithoutDrm;


    public ReadonlyStreamingStatus(Value withDrm, Instant withDrmOffline, Value withoutDrm, Instant withoutDrmOffline, Value audioWithoutDrm) {
        this.withDrm = withDrm;
        this.withDrmOffline = withDrmOffline;
        this.withoutDrm = withoutDrm;
        this.withoutDrmOffline = withoutDrmOffline;
        this.audioWithoutDrm = audioWithoutDrm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StreamingStatus that)) return false;

        if (withDrm != that.getWithDrm()) return false;
        if (withoutDrm != that.getWithoutDrm()) return false;
        return audioWithoutDrm == that.getAudioWithoutDrm();
    }

    @Override
    public int hashCode() {
        int result = withDrm != null ? withDrm.hashCode() : 0;
        result = 31 * result + (withoutDrm != null ? withoutDrm.hashCode() : 0);
        result = 31 * result + (audioWithoutDrm != null ? audioWithoutDrm.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
         return withDrm + (withDrmOffline != null ? ("(-" + withDrmOffline + ")") : "") +  "_" +
             withoutDrm + (withoutDrmOffline != null ? ("(-" + withoutDrmOffline + ")") : "") +
            (audioWithoutDrm == Value.ONLINE ? "_A:" + audioWithoutDrm : "");
    }

}
