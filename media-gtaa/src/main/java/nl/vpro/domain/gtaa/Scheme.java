package nl.vpro.domain.gtaa;

import lombok.Getter;

import java.util.Optional;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public enum Scheme {

    PERSOONSNAMEN("http://data.beeldengeluid.nl/gtaa/Persoonsnamen"),

    GEOGRAFISCHENAMEN("http://data.beeldengeluid.nl/gtaa/GeografischeNamen"),

    ONDERWERPEN("http://data.beeldengeluid.nl/gtaa/Onderwerpen"),

    ONDERWERPENBENG("http://data.beeldengeluid.nl/gtaa/OnderwerpenBenG"),

    CLASSIFICATIE("http://data.beeldengeluid.nl/gtaa/Classificatie"),

    MAKER("http://data.beeldengeluid.nl/gtaa/Maker"),

    GENRE("http://data.beeldengeluid.nl/gtaa/Genre"),

    NAMEN("http://data.beeldengeluid.nl/gtaa/Namen");

    @Getter
    private final String url;

    Scheme(String url) {
        this.url = url;
    }

    public static Optional<Scheme> ofUrl(String url) {
        for (Scheme s: values()) {
            if (s.getUrl().equals(url)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }
}

