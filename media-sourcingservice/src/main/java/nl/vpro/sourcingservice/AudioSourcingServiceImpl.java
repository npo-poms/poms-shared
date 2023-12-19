package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.jmx.export.annotation.ManagedResource;

import nl.vpro.domain.media.AVFileFormat;


/**
 * @implNote See {@link AbstractSourcingServiceImpl}
 */
@Log4j2
@ManagedResource
public class AudioSourcingServiceImpl extends  AbstractSourcingServiceImpl implements AudioSourcingService {

    @Inject
    public AudioSourcingServiceImpl(
        @NonNull @Named("audioConfiguration") Supplier<Configuration> configuration,
        MeterRegistry meterRegistry
       ) {
        super(configuration, meterRegistry);
    }

    @Override
    protected AVFileFormat defaultFormat() {
        return AVFileFormat.MP3;
    }

    @Override
    protected String implName() {
        return "audio";
    }

}
