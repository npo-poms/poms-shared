package nl.vpro.domain.media;

/**
 * A bit like {@link nl.vpro.domain.media.support.OwnerType} but simpler.
 * It indicates whether a {@link Prediction} was created by in import, or created in POMS itself.
 * <p>
 * It is also available in the corresponding {@link Location locations}
 *
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public enum Authority {

    /**
     * Means that the {@link Prediction} was created e.g. via the GUI or via an API call.
     */
    USER,
    /**
     * Means that the {@link Prediction} was created implicitly by an import from another system
     */
    SYSTEM
}
