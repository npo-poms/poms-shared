package nl.vpro.domain;

import lombok.Getter;

import java.io.Serializable;

/**
 * A checked not found exception.
 *
 * @author Michiel Meeuwissen
 * @since 4.4
 */
public class NotFoundException extends Exception {

    @Getter
    private final Serializable identifier;

    public NotFoundException(String identifier, String message) {
        super("For '" + identifier + "'. " + message);
        this.identifier = identifier;
    }

}
