package nl.vpro.services;

import java.security.Principal;
import java.util.function.Supplier;

import nl.vpro.domain.user.Editor;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public interface EditorProvider {

    /**
     * Given a principal load an object representing its details. This may e.g. be a spring security UserDetails object,
     * but at least it should supply an {@link Editor} also.
     */
    Supplier<Editor> loadDetails(Principal principal);

    default Editor getEditor(Principal principal) {
        return loadDetails(principal).get();
    }
}
