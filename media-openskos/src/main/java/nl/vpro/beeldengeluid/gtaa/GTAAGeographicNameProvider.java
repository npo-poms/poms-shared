package nl.vpro.beeldengeluid.gtaa;

import java.util.Optional;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.meeuw.i18n.spi.RegionProvider;

import nl.vpro.domain.gtaa.GTAAGeographicName;

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
        // TODO
        return Stream.empty();

    }
    @Override
    public Optional<GTAAGeographicName> getByCode(@NonNull String code, boolean lenient) {
        // TODO
        return values().filter(r -> r.getCode().equals(code)).findFirst();
    }
}
