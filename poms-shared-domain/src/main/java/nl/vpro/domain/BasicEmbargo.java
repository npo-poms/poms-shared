package nl.vpro.domain;

import java.time.Instant;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class BasicEmbargo implements MutableEmbargo<BasicEmbargo> {
    private Instant publishStartInstant;
    private Instant publishStopInstant;

    public BasicEmbargo(Instant publishStartInstant, Instant publishStopInstant) {
        this.publishStartInstant = publishStartInstant;
        this.publishStopInstant = publishStopInstant;
    }

    @Override
    public Instant getPublishStartInstant() {
        return publishStartInstant;
    }

    @NonNull
    @Override
    public BasicEmbargo setPublishStartInstant(Instant publishStartInstant) {
        this.publishStartInstant = publishStartInstant;
        return this;
    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStopInstant;
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
