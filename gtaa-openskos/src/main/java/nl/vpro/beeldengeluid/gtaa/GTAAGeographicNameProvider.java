package nl.vpro.beeldengeluid.gtaa;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.i18n.regions.spi.RegionProvider;

import nl.vpro.domain.gtaa.GTAAGeographicName;

import static nl.vpro.beeldengeluid.gtaa.OpenskosRepository.getInstance;

/**
 * I think we may use OpenSkos as a region provider.
 *
 * Just an idea, will try to test this out.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class GTAAGeographicNameProvider implements RegionProvider<GTAAGeographicName> {

    @Override
    public Class<GTAAGeographicName> getProvidedClass() {
        return GTAAGeographicName.class;
    }

    @Override
    public Stream<GTAAGeographicName> values() {
        return
            getInstance().getGeoLocationsUpdates(Instant.EPOCH, Instant.now())
                .stream()
                .map(r -> GTAAGeographicName.create(r.getMetaData().getFirstDescription()));
    }

    @Override
    public Optional<GTAAGeographicName> getByCode(@NonNull String code, boolean lenient) {
        return getInstance().get(code).filter(p -> p instanceof GTAAGeographicName).map(p -> (GTAAGeographicName) p);
    }
}
