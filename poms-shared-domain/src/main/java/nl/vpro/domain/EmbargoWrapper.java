package nl.vpro.domain;

import java.time.Instant;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class EmbargoWrapper implements MutableEmbargo<EmbargoWrapper> {

    private final Supplier<Instant> start;
    private final Consumer<Instant> startSetter;
    private final Supplier<Instant> stop;
    private final Consumer<Instant> stopSetter;

    public EmbargoWrapper(Supplier<Instant> start, Consumer<Instant> startSetter, Supplier<Instant> stop, Consumer<Instant> stopSetter) {
        this.start = start;
        this.startSetter = startSetter;
        this.stop = stop;
        this.stopSetter = stopSetter;
    }

    @Override
    public Instant getPublishStartInstant() {
        return start.get();
    }

    @Override
    public Instant getPublishStopInstant() {
        return stop.get();
    }

    @Override
    public @NonNull EmbargoWrapper setPublishStartInstant(@Nullable Instant publishStart) {
        startSetter.accept(publishStart);
        return this;
    }

    @Override
    public @NonNull EmbargoWrapper setPublishStopInstant(@Nullable Instant publishStop) {
        stopSetter.accept(publishStop);
        return this;
    }
}
