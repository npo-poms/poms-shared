package nl.vpro.api.rs.v3.thesaurus;

import jakarta.ws.rs.ext.ParamConverter;

import nl.vpro.domain.gtaa.Scheme;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class SchemeConverter implements ParamConverter<String> {
    static SchemeConverter INSTANCE = new SchemeConverter();

    @Override
    public String fromString(String value) {
        return Scheme.ofUrl(value).orElseGet(() -> Scheme.valueOf(value)).name();
    }

    @Override
    public String toString(String value) {
        return value;

    }
}
