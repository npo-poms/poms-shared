package nl.vpro.domain;

import java.time.Instant;

import javax.annotation.Nonnull;

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

    @Nonnull
    @Override
    public BasicEmbargo setPublishStartInstant(Instant publishStartInstant) {
        this.publishStartInstant = publishStartInstant;
        return this;
    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStopInstant;
    }

    @Nonnull
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
