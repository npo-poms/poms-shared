package nl.vpro.domain;

import java.io.Serializable;

/**
 * @author Michiel Meeuwissen
 * @since 4.4
 */
public class NotFoundException extends Exception {

    private static final long serialVersionUID = 1756255370140166823L;
    private final Serializable identifier;

    public NotFoundException(String identifier, String message) {
        super("For '" + identifier + "'. " + message);
        this.identifier = identifier;
    }

    public Serializable getIdentifier() {
        return identifier;
    }
}
