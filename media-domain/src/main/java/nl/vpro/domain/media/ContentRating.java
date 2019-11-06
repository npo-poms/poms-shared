package nl.vpro.domain.media;

import java.util.*;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Displayable;

/**
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

    private static final Map<String, ContentRating> stringToEnum = new HashMap<>();

    static {
        for(ContentRating r : values()) {
            charToEnum.put(r.letter, r);
            stringToEnum.put(r.displayName, r);
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
    public Optional<String> getIcon() {
        return Optional.of("/kijkwijzer/icons/contentrating/" + name().toLowerCase() + ".svg");
    }
}
