package nl.vpro.domain.gtaa;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
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

    @Getter
    private Class<? extends ThesaurusObject> implementation;


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

    static void init(Class<?> gtaaClass) {
        GTAAScheme annotation = gtaaClass.getAnnotation(GTAAScheme.class);
        if (annotation != null) {
            log.info("Registering {}", gtaaClass);
            annotation.value().implementation = (Class<? extends ThesaurusObject>) gtaaClass;
        } else {

        }
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

