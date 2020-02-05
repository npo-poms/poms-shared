package nl.vpro.nicam;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.ContentRating;

/**
 * http://www.kijkwijzer.nl/about-kijkwijzer
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@Data
@Slf4j
public class Kijkwijzer implements NicamRated {

    private AgeRating ageRating;
    private List<ContentRating> contentRatings;

    public static Kijkwijzer parse(String value) {
        AgeRating ageRating = null;
        List<ContentRating> contentRatings = new ArrayList<>();
        if (value != null) {
            for (char c : value.toCharArray()) {
                switch (c) {
                    case '2':
                        ageRating = AgeRating._6;
                        break;
                    case '3':
                        ageRating = AgeRating._9;
                        break;
                    case '4':
                        ageRating = AgeRating._12;
                        break;
                    case '5':
                        ageRating = AgeRating._16;
                        break;
                    default:
                        contentRatings.add(
                            ContentRating.valueOf(c)
                        );

                }
            }
        }
        return new Kijkwijzer(ageRating, contentRatings);
    }

    // Used by Cinema
    public static Kijkwijzer parseDonna(String value){
        AgeRating ageRating = null;
        List<ContentRating> contentRatings = new ArrayList<>();
        if (value != null) {
            for (char c : value.toCharArray()) {
                switch (c) {
                    case '1':
                        ageRating = AgeRating.ALL;
                        break;
                    case '2':
                        ageRating = AgeRating._6;
                        break;
                    case '5':
                        ageRating = AgeRating._9;
                        break;
                    case '3':
                        ageRating = AgeRating._12;
                        break;
                    case '4':
                        ageRating = AgeRating._16;
                        break;
                    default:
                        contentRatings.add(ContentRating.valueOf(c));

                }
            }
        }
        return new Kijkwijzer(ageRating, contentRatings);
    }

    public Kijkwijzer(AgeRating ageRating, List<ContentRating> contentRatings) {
        this.ageRating = ageRating;
        this.contentRatings = contentRatings;
    }
    public Kijkwijzer(AgeRating ageRating, ContentRating... contentRatings) {
        this(ageRating, Arrays.asList(contentRatings));
    }

    public Kijkwijzer() {
        this.ageRating = null;
        this.contentRatings = new ArrayList<>();
    }

    public String toDonnaCode() {
        StringBuilder result = new StringBuilder();
        Character ageRatingCode = toDonnaCode(ageRating);
        if (ageRatingCode != null) {
            result.append(ageRatingCode);
        }
        for (ContentRating rating : contentRatings) {
            result.append(rating.toChar());
        }
        return result.toString();
    }

    public String toCode() {
        StringBuilder result = new StringBuilder();
        Character ageRatingCode = toCode(ageRating);
        if (ageRatingCode != null) {
            result.append(ageRatingCode);
        }

        for (ContentRating rating : contentRatings) {
            result.append(rating.toChar());
        }

        return result.toString();
    }

    /**
     * @since 5.12
     */
    public String toPaddedCode() {
        StringBuilder result = new StringBuilder();
        result.append(toPaddedCode(ageRating));
        for (ContentRating rating : contentRatings) {
            result.append(rating.toChar());
        }
        return result.toString();
    }

    /**
     * @since 5.12
     */
    public static Optional<Kijkwijzer> parsePaddedCode(String value) {
        if (StringUtils.isEmpty(value)) {
            return Optional.empty();
        }
        AgeRating ageRating = null;
        if (value.length() >= 2 && (Character.isDigit(value.charAt(0)) || value.charAt(0) == '-') && Character.isDigit(value.charAt(1))) {
            ageRating = AgeRating.valueOf(Integer.parseInt(value.substring(0, 2)));
            value = value.substring(2);
        } else {
            if (Character.isDigit(value.charAt(0))) {
                return Optional.empty();
            }
        }
        List<ContentRating> contentRatings = new ArrayList<>();
        for (char c : value.toCharArray()) {
            contentRatings.add(ContentRating.valueOf(c));
        }
        return Optional.of(new Kijkwijzer(ageRating, contentRatings));
    }

    public static Character toCode(AgeRating ageRating) {
        if (ageRating == null) {
            return null;
        }
        switch (ageRating) {

            case _6:
                return '2';
            case _9:
                return '3';
            case _12:
                return '4';
            case _16:
                return '5';
            case _14:
            case _18:
                log.warn("We don't know the code of {}", ageRating);
                return null;
            default:
            case ALL:
                return null;
        }
    }
    public static String toPaddedCode(AgeRating ageRating) {
        if (ageRating == null) {
            return "";
        }
        return String.format("%02d", ageRating.getIntValue());
    }


    public static Character toDonnaCode(AgeRating ageRating) {
        if (ageRating == null) {
            return null;
        }
        switch (ageRating) {

            case _6: return '2';
            case _9: return '5';
            case _12: return '3';
            case _16: return '4';
            default:
            case ALL:
                return '1';
        }
    }

}
