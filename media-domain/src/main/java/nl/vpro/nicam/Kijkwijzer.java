package nl.vpro.nicam;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.ContentRating;

/**
 * http://www.kijkwijzer.nl/about-kijkwijzer
 *
 * Combines an {@link AgeRating} with {@link ContentRating}s (which can be seen as the 'reason' for the rating).
 *
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@Data
@Slf4j
public class Kijkwijzer implements NicamRated {

    private final AgeRating ageRating;
    private final List<@NonNull ContentRating> contentRatings;

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

    public Kijkwijzer(@Nullable AgeRating ageRating, List<@NonNull ContentRating> contentRatings) {
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
                // impossible, but hibernate seems to cause it some times
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
        StringBuilder ageRatingString = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isDigit(c) || (c == '-' && ageRatingString.length() == 0)) {
                ageRatingString.append(c);
            }
        }
        AgeRating ageRating = null;
        if (ageRatingString.length() == 1) {
            return Optional.empty();
        } else if (ageRatingString.length() == 2) {
            ageRating = AgeRating.valueOf(Integer.parseInt(ageRatingString.toString()));
        } else if (ageRatingString.length() > 2) {
            return Optional.empty();
        }
        List<ContentRating> contentRatings = new ArrayList<>();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (! Character.isDigit(c)) {
                ContentRating r = ContentRating.valueOf(c);
                if (r != null) {
                    contentRatings.add(r);
                } else {
                    log.warn("Unrecognized content rating char {}", c);
                }
            }
        }

        return Optional.of(new Kijkwijzer(ageRating, contentRatings));
    }

    @Nullable
    public static Character toCode(@Nullable AgeRating ageRating) {
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
    public static String toPaddedCode(@Nullable AgeRating ageRating) {
        if (ageRating == null) {
            return "";
        }
        return String.format("%02d", ageRating.getIntValue());
    }

    @Nullable
    public static Character toDonnaCode(@Nullable AgeRating ageRating) {
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
