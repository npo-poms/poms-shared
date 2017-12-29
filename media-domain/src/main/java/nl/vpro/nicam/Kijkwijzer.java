package nl.vpro.nicam;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.ContentRating;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
@Data
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
                        contentRatings.add(ContentRating.valueOf(c));

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

    public String toDonnaCode() {
        StringBuilder result = new StringBuilder();

        if (ageRating != null) {
            switch(ageRating) {
                case ALL:
                    result.append('1');
                    break;
                case _6:
                    result.append('2');
                    break;
                case _9:
                    result.append('5');
                    break;
                case _12:
                    result.append('3');
                    break;
                case _16:
                    result.append('4');
                    break;

            }
        }

        for (ContentRating rating : contentRatings) {
            result.append(rating.toChar());
        }

        return result.toString();
    }

    public String toCode() {
        StringBuilder result = new StringBuilder();

        if (ageRating != null) {
            switch (ageRating) {
                case _6:
                    result.append('2');
                    break;
                case _9:
                    result.append('3');
                    break;
                case _12:
                    result.append('4');
                    break;
                case _16:
                    result.append('5');
                    break;
            }
        }

        for (ContentRating rating : contentRatings) {
            result.append(rating.toChar());
        }

        return result.toString();
    }
}
