package nl.vpro.domain.gtaa;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.Displayable;
import nl.vpro.i18n.Locales;
import nl.vpro.i18n.LocalizedString;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public enum Scheme implements Displayable {

    person("http://data.beeldengeluid.nl/gtaa/Persoonsnamen",
        "persoonsnaam",
        "persoonsnamen"),

    geographicname("http://data.beeldengeluid.nl/gtaa/GeografischeNamen",
        "geografische naam",
        "geografische namen"),

    topic("http://data.beeldengeluid.nl/gtaa/Onderwerpen", "onderwerp", "onderwerpen"),

    topicbandg("http://data.beeldengeluid.nl/gtaa/OnderwerpenBenG", "onderwerp van B&G", "onderwerpen van B&G"),

    classification("http://data.beeldengeluid.nl/gtaa/Classificatie", "classificatie", "classificaties"),

    maker("http://data.beeldengeluid.nl/gtaa/Maker", "maker", "makers"),

    genre("http://data.beeldengeluid.nl/gtaa/Genre", "genre", "genres"),

    name("http://data.beeldengeluid.nl/gtaa/Namen", "naam", "namen")


    ;
    static {
        GTAAConceptIdResolver.init();
    }
    @Getter
    private final String url;

    private final String pluralDisplayName;
    private final String displayName;

    @Getter
    private Class<? extends GTAAConcept> implementation;


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

    public static Scheme ofJsonObjectType(String objectType) {
        return Scheme.valueOf(objectType);
    }

    static void init(Class<?> gtaaClass) {
        GTAAScheme annotation = gtaaClass.getAnnotation(GTAAScheme.class);
        if (annotation != null) {
            log.info("Registering {}", gtaaClass);
            annotation.value().implementation = (Class<? extends GTAAConcept>) gtaaClass;
        } else {

        }
    }

    @Override
    public Optional<LocalizedString> getPluralDisplayName() {
        return Optional.of(LocalizedString.of(pluralDisplayName, Locales.DUTCH));
    }
    @Override
    public String getDisplayName() {
        return displayName;
    }

    public String getXmlElement() {
        XmlRootElement annotation = getImplementation().getAnnotation(XmlRootElement.class);
        if (annotation == null) {
            throw new RuntimeException("No root element defined for " + this);
        }
        return annotation.name();
    }

    public String getJsonObjectType() {
        return name();
    }

    public static Class[] classes() {
        return Arrays.stream(values()).map(s -> s.implementation)
            .filter(Objects::nonNull)
            .toArray(Class[]::new);
    }
}


