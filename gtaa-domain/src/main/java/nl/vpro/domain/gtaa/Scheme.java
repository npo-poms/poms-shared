package nl.vpro.domain.gtaa;

import lombok.Getter;

import java.util.Optional;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public enum Scheme implements Displayable {

    PERSOONSNAMEN("http://data.beeldengeluid.nl/gtaa/Persoonsnamen", "persoonsnamen"),

    GEOGRAFISCHENAMEN("http://data.beeldengeluid.nl/gtaa/GeografischeNamen", "geografische namen"),

    ONDERWERPEN("http://data.beeldengeluid.nl/gtaa/Onderwerpen", "onderwerpen"),

    ONDERWERPENBENG("http://data.beeldengeluid.nl/gtaa/OnderwerpenBenG", "onderwerpen van B&G"),

    CLASSIFICATIE("http://data.beeldengeluid.nl/gtaa/Classificatie", "classificaties"),

    MAKER("http://data.beeldengeluid.nl/gtaa/Maker", "makers"),

    GENRE("http://data.beeldengeluid.nl/gtaa/Genre", "genres"),

    NAMEN("http://data.beeldengeluid.nl/gtaa/Namen", "namen");

    @Getter
    private final String url;

    private final String displayName;

    Scheme(String url, String displayName) {
        this.url = url;
        this.displayName = displayName;
    }

    public static Optional<Scheme> ofUrl(String url) {
        for (Scheme s: values()) {
            if (s.getUrl().equals(url)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    @Override
    public String getDisplayName() {
        return displayName;

    }
}

