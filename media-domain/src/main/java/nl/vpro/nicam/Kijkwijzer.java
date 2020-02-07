package nl.vpro.nicam;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

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
    private List<@NonNull ContentRating> contentRatings;

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

    @Deprecated
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

    public Kijkwijzer(AgeRating ageRating, List<@NonNull ContentRating> contentRatings) {
        this.ageRating = ageRating;
        this.contentRatings = contentRatings == null ? new ArrayList<>() : contentRatings;
    }
    public Kijkwijzer(AgeRating ageRating, @NonNull ContentRating... contentRatings) {
        this(ageRating, Arrays.asList(contentRatings));
    }

    public Kijkwijzer() {
        this.ageRating = null;
        this.contentRatings = new ArrayList<>();
    }

    @Deprecated
    public String toDonnaCode() {
        StringBuilder result = new StringBuilder();
        Character ageRatingCode = toDonnaCode(ageRating);
        if (ageRatingCode != null) {
            result.append(ageRatingCode);
        }
        appendContentRatings(result);
        return result.toString();
    }

    public String toCode() {
        StringBuilder result = new StringBuilder();
        Character ageRatingCode = toCode(ageRating);
        if (ageRatingCode != null) {
            result.append(ageRatingCode);
        }
        appendContentRatings(result);
        return result.toString();
    }

    /**
     * @since 5.12
     */
    public String toPaddedCode() {
        StringBuilder result = new StringBuilder();
        result.append(toPaddedCode(ageRating));
        appendContentRatings(result);
        return result.toString();
    }

    private void appendContentRatings(StringBuilder result) {
        for (ContentRating rating : contentRatings) {
            if (rating == null) {
                log.warn("null rating in {}", contentRatings);
            } else {
                result.append(rating.toChar());
            }
        }
    }

    /**
     * @since 5.12
     */
    public static Optional<Kijkwijzer> parsePaddedCode(CharSequence value) {
        if (StringUtils.isEmpty(value)) {
            return Optional.empty();
        }
        String valueAsString = value.toString();
        AgeRating ageRating = null;
        if (value.length() >= 2 && (Character.isDigit(value.charAt(0)) || value.charAt(0) == '-') && Character.isDigit(value.charAt(1))) {
            ageRating = AgeRating.valueOf(Integer.parseInt(valueAsString.substring(0, 2)));
            value = valueAsString.substring(2);
        } else {
            if (Character.isDigit(value.charAt(0))) {
                return Optional.empty();
            }
        }
        List<ContentRating> contentRatings = new ArrayList<>();
        for (char c : valueAsString.toCharArray()) {
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
