package nl.vpro.api.rs.v3.thesaurus;

import javax.ws.rs.ext.ParamConverter;

import nl.vpro.domain.gtaa.Scheme;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class SchemeConverter implements ParamConverter<Scheme> {
    static SchemeConverter INSTANCE = new SchemeConverter();

    @Override
    public Scheme fromString(String value) {
        return Scheme.ofUrl(value).orElseGet(() -> Scheme.valueOf(value));
    }

    @Override
    public String toString(Scheme value) {
        return value.name();

    }
}
