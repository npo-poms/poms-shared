package nl.vpro.domain;

import java.time.Instant;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class BasicEmbargo implements Embargo<BasicEmbargo> {
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

    @Override
    public BasicEmbargo setPublishStartInstant(Instant publishStartInstant) {
        this.publishStartInstant = publishStartInstant;
        return this;
    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStopInstant;
    }

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
