package nl.vpro.nicam;

import java.util.ArrayList;
import java.util.List;

import nl.vpro.domain.media.AgeRating;
import nl.vpro.domain.media.ContentRating;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
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

    @Override
    public AgeRating getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(AgeRating ageRating) {
        this.ageRating = ageRating;
    }

    @Override
    public List<ContentRating> getContentRatings() {
        return contentRatings;
    }

    public void setContentRatings(List<ContentRating> contentRatings) {
        this.contentRatings = contentRatings;
    }
}
