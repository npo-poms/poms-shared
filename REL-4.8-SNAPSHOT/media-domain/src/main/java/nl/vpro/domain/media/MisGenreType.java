package nl.vpro.domain.media;

import java.util.Arrays;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
public enum MisGenreType {
    // WARNING: Keep this in order (by display name)!
    ENTERTAINMENT("Amusement", "_06"), // cant use constants because that causes circular instatation problems
    CARTOON("Animatie", "_0211"),
    COMEDY("Comedy", "_0609"),
    DOCUMENTARY("Documentaire", "_08"),
    DRAMA("Drama", "_0312"),
    EDUCATION("Educatief"),
    EROTICA("Erotiek"),
    MOVIE("Film", "_02"),
    HEALTH("Gezondheid", "_0722"),
    INFORMATIVE("Informatief", "_07"),
    YOUTH("Jeugd", "_01"),
    CHILDREN_2_5("Kinderen 2-5"),
    CHILDREN_6_12("Kinderen 6-12"),
    CLASSIC("Klassiek", "_0516"),
    ART_CULTURE("Kunst/Cultuur", "_0724"),
    COMMUNITY("Maatschappij"),
    CRIME("Misdaad", "_0310"),
    MUSIC("Muziek", "_05"),
    NATURE("Natuur", "_0725"),
    NEWS("Nieuws/actualiteiten", "_0721"),
    OTHER("Overige"),
    RELIGIOUS("Religieus", "_0726"),
    SERIES_SOAP("Serie/soap", "_03"),
    SPORT("Sport", "_04"),
    SCIENCE("Wetenschap", "_0727");

    private final String displayName;

    private final Collection<String> genreType;

    MisGenreType(String displayName, String... EpgGenreType) {
        this.displayName = displayName;
        this.genreType = Arrays.asList(EpgGenreType);
    }

    public String getDisplayName() {
        return displayName;
    }

    public Collection<EpgGenreType> getEpgGenreType() {
        return EpgGenreType.valueOf(genreType);
    }



    /**
     * Searches in both enum names and display names.
     * Not case sensitive!
     */
    public static MisGenreType find(String pointer) throws IllegalArgumentException {
        for(MisGenreType misGenreType : MisGenreType.values()) {
            if(misGenreType.getDisplayName().equalsIgnoreCase(pointer)) {
                return misGenreType;
            }
        }
        try {
            return MisGenreType.valueOf(pointer);
        } catch(IllegalArgumentException e) {
            return null;
        }
    }

    public static SortedSet<MisGenreType> valueOf(Collection<String> genres) {
        SortedSet<MisGenreType> result = new TreeSet<>();
        for(String genre : genres) {
            if(genre.length() > 0) {
                result.add(valueOf(genre));
            }
        }
        return result;
    }

    public static SortedSet<MisGenreType> valueOfGenre(Collection<Genre> genres) {
        SortedSet<MisGenreType> result = new TreeSet<>();
        for (Genre genre : genres) {
            for (String misGenre : genre.getMisGenres()) {
                result.add(MisGenreType.valueOf(misGenre));
            }
        }
        return result;
    }

    public static SortedSet<MisGenreType> find(Collection<String> genres) {
        SortedSet<MisGenreType> result = new TreeSet<>();
        for(String genre : genres) {
            if(genre.length() > 0) {
                MisGenreType type = find(genre);
                if(type != null) {
                    result.add(type);
                }
            }
        }
        return result;
    }


}
