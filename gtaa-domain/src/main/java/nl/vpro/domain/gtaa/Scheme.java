package nl.vpro.domain.gtaa;

import lombok.Getter;

import java.util.Optional;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public enum Scheme implements Displayable {

    PERSOONSNAMEN("http://data.beeldengeluid.nl/gtaa/Persoonsnamen",
        "persoonsnaam",
        "persoonsnamen"),

    GEOGRAFISCHENAMEN("http://data.beeldengeluid.nl/gtaa/GeografischeNamen",
        "geografische naam",
        "geografische namen"),

    ONDERWERPEN("http://data.beeldengeluid.nl/gtaa/Onderwerpen", "onderwerp", "onderwerpen"),

    ONDERWERPENBENG("http://data.beeldengeluid.nl/gtaa/OnderwerpenBenG", "onderwerp van B&G", "onderwerpen van B&G"),

    CLASSIFICATIE("http://data.beeldengeluid.nl/gtaa/Classificatie", "classificatie", "classificaties"),

    MAKER("http://data.beeldengeluid.nl/gtaa/Maker", "maker", "makers"),

    GENRE("http://data.beeldengeluid.nl/gtaa/Genre", "genre", "genres"),

    NAMEN("http://data.beeldengeluid.nl/gtaa/Namen", "naam", "namen")
    ;

    @Getter
    private final String url;

    private final String pluralDisplayName;
    private final String displayName;


    Scheme(String url, String displayName, String pluralDisplayName) {
        this.url = url;
        this.displayName = displayName;
        this.pluralDisplayName = pluralDisplayName;
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
    public String getPluralDisplayName() {
        return pluralDisplayName;
    }
    @Override
    public String getDisplayName() {
        return displayName;

    }
}

