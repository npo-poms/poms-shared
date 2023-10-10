package nl.vpro.domain.gtaa;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.ArrayUtils;

import nl.vpro.i18n.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public enum Scheme implements Displayable {

    person("Persoonsnamen", "http://data.beeldengeluid.nl/gtaa/Persoonsnamen",
        "persoonsnaam",
        "persoonsnamen"),

    geographicname("GeografischeNamen", "http://data.beeldengeluid.nl/gtaa/GeografischeNamen",
        "geografische naam",
        "geografische namen"),

    topic("Onderwerpen", "http://data.beeldengeluid.nl/gtaa/Onderwerpen", "onderwerp", "onderwerpen"),

    topicbandg("OnderwerpenBenG", "http://data.beeldengeluid.nl/gtaa/OnderwerpenBenG", "onderwerp van B&G", "onderwerpen van B&G"),

    classification("Classificatie", "http://data.beeldengeluid.nl/gtaa/Classificatie", "classificatie", "classificaties"),

    maker("Maker", "http://data.beeldengeluid.nl/gtaa/Maker", "maker", "makers"),

    genre("Genre", "http://data.beeldengeluid.nl/gtaa/Genre", "genre", "genres"),

    name("Namen", "http://data.beeldengeluid.nl/gtaa/Namen", "naam", "namen"),

    genrefilmmuseum("Genre van Filmmuseum Eye", "http://data.beeldengeluid.nl/gtaa/Genre_FilmMuseum", "genre filmmuseum", "genres filmmuseum")
    ;

    static {
        GTAAConceptIdResolver.init();
    }
    @Getter
    private final String url;
    @Getter
    private final String id;
    private final String pluralDisplayName;
    private final String displayName;

    @Getter
    private Class<? extends GTAAConcept> implementation;


    Scheme(String id, String url, String displayName, String pluralDisplayName) {
        this.id = id;
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

    @SuppressWarnings("unchecked")
    static void init(Class<?> gtaaClass) {
        GTAAScheme annotation = gtaaClass.getAnnotation(GTAAScheme.class);
        if (annotation != null) {
            log.info("Registering {}", gtaaClass);
            annotation.value().implementation = (Class<? extends GTAAConcept>) gtaaClass;
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

    @SuppressWarnings("unchecked")
    public static Class<? extends GTAAConcept>[] classes() {
        return Arrays.stream(values()).map(s -> s.implementation)
            .filter(Objects::nonNull)
            .toArray(Class[]::new);
    }

    @SuppressWarnings("unchecked")
    public static Class<?>[] classesAndNew() {
        return ArrayUtils.addAll(classes(), GTAANewPerson.class, GTAANewGenericConcept.class);
    }

    /**
     * // doens't work
     * See https://docs.jboss.org/resteasy/docs/3.5.1.Final/userguide/html/StringConverter.html
     */
    public static Scheme fromString(String value) {
        return ofUrl(value).orElseGet(() -> Scheme.valueOf(value));
    }

    /**
     * @since 5.31
     */
    public String getSpec() {
        return "beng:gtaa:" + getId();
    }
}


