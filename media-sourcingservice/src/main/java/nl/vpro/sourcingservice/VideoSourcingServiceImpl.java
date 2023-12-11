package nl.vpro.sourcingservice;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.log4j.Log4j2;

import java.util.function.Supplier;

import javax.inject.Named;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.AVFileFormat;


/**
 * Video part of sourcing service. This is currently not used, as poms interfaces with NEP directly for that.
 * @implNote See {@link AbstractSourcingServiceImpl}
 */
@Log4j2
public class VideoSourcingServiceImpl extends  AbstractSourcingServiceImpl implements VideoSourcingService {



    public VideoSourcingServiceImpl(
        @NonNull @Named("videoConfiguration") Supplier<Configuration> configuration,
        MeterRegistry meterRegistry
       ) {
        super(configuration, meterRegistry);
    }


    @Override
    protected AVFileFormat defaultFormat() {
        return AVFileFormat.MP4;
    }

    @Override
    protected String implName() {
        return "video";
    }


}
