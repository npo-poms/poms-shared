package nl.vpro.beeldengeluid.gtaa;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import static nl.vpro.beeldengeluid.gtaa.OpenskosRepository.getInstance;
import nl.vpro.domain.gtaa.GTAAGeographicName;
import nl.vpro.domain.gtaa.GTAAGeographicName.Code;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.i18n.regions.spi.RegionProvider;

/**
 * OpenSkos as a {@link RegionProvider}
 * @author Michiel Meeuwissen
 * @since 5.11
 */
@Slf4j
public class GTAAGeographicNameProvider implements RegionProvider<Code> {

    @Override
    public Class<Code> getProvidedClass() {
        return Code.class;
    }

    @Override
    public Stream<Code> values() {
        try {
            return
                getInstance().getGeoLocationsUpdates(Instant.EPOCH, Instant.now())
                    .stream()
                    .map(r -> {
                        try {
                            return GTAAGeographicName.create(r.getMetaData().getFirstDescription()).toCode();
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
    public Optional<Code> getByCode(@NonNull String code, boolean lenient) {
        try {
            return getInstance()
                .get(code)
                .filter(p -> p instanceof GTAAGeographicName).map(p -> (GTAAGeographicName) p).map(GTAAGeographicName::toCode);
        } catch (IllegalStateException  illegalStateException) {
            return Optional.empty();
        }
    }
}
