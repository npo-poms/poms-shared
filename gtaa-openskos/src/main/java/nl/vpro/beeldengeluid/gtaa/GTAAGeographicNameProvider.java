package nl.vpro.beeldengeluid.gtaa;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.i18n.regions.spi.RegionProvider;

import nl.vpro.domain.gtaa.GTAAGeographicName;

import static nl.vpro.beeldengeluid.gtaa.OpenskosRepository.getInstance;

/**
 * OpenSkos as a {@link RegionProvider}
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class GTAAGeographicNameProvider implements RegionProvider<GTAAGeographicName> {

    @Override
    public Class<GTAAGeographicName> getProvidedClass() {
        return GTAAGeographicName.class;
    }

    @Override
    public Stream<GTAAGeographicName> values() {
        try {
            return
                getInstance().getGeoLocationsUpdates(Instant.EPOCH, Instant.now())
                    .stream()
                    .map(r -> {
                        try {
                            return GTAAGeographicName.create(r.getMetaData().getFirstDescription());
                        }  catch (Exception e) {
                            log.warn("For {}: {}", r, e.getClass() + ":" + e.getMessage());
                            return null;
                        }
                        }
                    )
                    .filter(Objects::nonNull);
        } catch (IllegalStateException illegalStateException) {
            return Stream.empty();
        }
    }

    @Override
    public Optional<GTAAGeographicName> getByCode(@NonNull String code, boolean lenient) {
        try {
            return getInstance().get(code).filter(p -> p instanceof GTAAGeographicName).map(p -> (GTAAGeographicName) p);
        } catch (IllegalStateException  illegalStateException) {
            return Optional.empty();
        }
    }
}
