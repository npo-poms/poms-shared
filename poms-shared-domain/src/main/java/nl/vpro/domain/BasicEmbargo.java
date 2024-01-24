package nl.vpro.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Getter
@EqualsAndHashCode
public class BasicEmbargo implements MutableEmbargo<BasicEmbargo> {
    @Nullable
    private Instant publishStartInstant;

    @Nullable
    private Instant publishStopInstant;

    public BasicEmbargo(@Nullable Instant publishStartInstant, @Nullable Instant publishStopInstant) {
        this.publishStartInstant = publishStartInstant;
        this.publishStopInstant = publishStopInstant;
    }

    @NonNull
    @Override
    public BasicEmbargo setPublishStartInstant(@Nullable Instant publishStartInstant) {
        this.publishStartInstant = publishStartInstant;
        return this;
    }

    @NonNull
    @Override
    public BasicEmbargo setPublishStopInstant(Instant publishStopInstant) {
        this.publishStopInstant = publishStopInstant;
        return this;
    }

    @Override
    public String toString() {
        return Embargos.toString(this);
    }
}
