package nl.vpro.domain;

import lombok.Getter;

import java.io.Serializable;

/**
 * An exception that can be thrown if something is not find. E.g. a profile or a mediaobject
 * <p>
 *
 * This is a runtime exception. Since it is thrown by rest service interfaces
 *
 * @author Michiel Meeuwissen
 * @since 4.4
 */
public class NotFoundException extends IllegalArgumentException {

    private static final long serialVersionUID = 1756255370140166823L;
    @Getter
    private final Serializable identifier;

    public NotFoundException(String identifier, String message) {
        super("For '" + identifier + "'. " + message);
        this.identifier = identifier;
    }

    public NotFoundException(String s, String message, Exception cause) {
        super(message, cause);
        this.identifier = s;
    }
}
