package nl.vpro.domain.media;

import java.net.URI;
import java.util.*;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.i18n.Displayable;

/**
 * A <a href="https://nicam.nl">NICAM</a> content rating
 *
 * @author Michiel Meeuwissen
 */
@XmlEnum
@XmlType(name = "contentRatingEnum")
public enum ContentRating implements Displayable {
    DISCRIMINATIE("Discriminatie", 'd'),
    GROF_TAALGEBRUIK("Grof taalgebruik", 't'),
    ANGST("Angst", 'a'),
    GEWELD("Geweld", 'g'),
    SEKS("Seks", 's'),
    DRUGS_EN_ALCOHOL("Drugs- en/of alcoholmisbruik", 'h');

    private static final Map<Character, ContentRating> charToEnum = new HashMap<>();


    static {
        for(ContentRating r : values()) {
            charToEnum.put(r.letter, r);
        }
    }


    private final String displayName;

    private final char letter;

    ContentRating(String displayName, char letter) {
        this.displayName = displayName;
        this.letter = letter;
    }

    public char toChar() {
        return letter;
    }

    public static ContentRating valueOf(char c) {
        return charToEnum.get(c);
    }

    public static List<ContentRating> valueOf(List<String> list) {
        List<ContentRating> rating = new ArrayList<>();
        if(list != null) {
            for(String c : list) {
                rating.add(valueOf(c));
            }
        }
        return rating;
    }


    @Override
    public String getDisplayName() {
        return displayName;
    }


    @Override
    public Optional<URI> getIcon() {
        return Optional.of(URI.create("/kijkwijzer/icons/contentrating/" + name().toLowerCase() + ".svg"));
    }
    @Override
    public Optional<String> getIconClass() {
        return Optional.of("kijkwijzer-icon kijkwijzer-icon-contentrating-" + name().toLowerCase());
    }
}
