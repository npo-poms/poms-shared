package nl.vpro.services;

import java.security.Principal;
import java.util.function.Supplier;

import nl.vpro.domain.user.Editor;

/**
 * The generic service that can provide new {@link Editor} objects given authentication information.
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public interface EditorProvider {

    /**
     * Given a principal load an object representing its details. This may e.g. be a spring security UserDetails object,
     * but at least it should supply an {@link Editor} also.
     *
     * @return An object containing the metadata necessary to create an {@link Editor}. Never <code>null</code>
     * @throws RuntimeException if e.g. the user can not be found
     */
    Supplier<Editor> loadDetails(Principal principal);

    /**
     * Shorthand for <code>{@link #loadDetails(Principal)}.get()</code>
     */
    default Editor getEditor(Principal principal) {
        return loadDetails(principal).get();
    }
}
