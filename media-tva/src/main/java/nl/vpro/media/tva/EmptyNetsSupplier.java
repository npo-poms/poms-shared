package nl.vpro.media.tva;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;

import nl.vpro.domain.media.Net;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class EmptyNetsSupplier implements Supplier<Collection<Net>> {
    @Override
    public Collection<Net> get() {
        return Collections.emptySet();
    }
}
