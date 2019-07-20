package nl.vpro.domain.gtaa;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public enum Scheme implements Displayable {

    PERSON("http://data.beeldengeluid.nl/gtaa/Persoonsnamen",
        "persoonsnaam",
        "persoonsnamen"),

    GEOGRAPHICNAME("http://data.beeldengeluid.nl/gtaa/GeografischeNamen",
        "geografische naam",
        "geografische namen"),

    TOPIC("http://data.beeldengeluid.nl/gtaa/Onderwerpen", "onderwerp", "onderwerpen"),

    TOPICBANDG("http://data.beeldengeluid.nl/gtaa/OnderwerpenBenG", "onderwerp van B&G", "onderwerpen van B&G"),

    CLASSIFICATION("http://data.beeldengeluid.nl/gtaa/Classificatie", "classificatie", "classificaties"),

    MAKER("http://data.beeldengeluid.nl/gtaa/Maker", "maker", "makers"),

    GENRE("http://data.beeldengeluid.nl/gtaa/Genre", "genre", "genres"),

    NAMES("http://data.beeldengeluid.nl/gtaa/Namen", "naam", "namen"),

    ITEM(null, "item", "items")

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

    public static Class[] classes() {
        ThesaurusObjectIdResolver.init();
        return Arrays.stream(values()).map(s -> s.implementation)
            .filter(Objects::nonNull)
            .toArray(Class[]::new);
    }
}


