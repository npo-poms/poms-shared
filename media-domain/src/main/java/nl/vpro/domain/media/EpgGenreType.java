package nl.vpro.domain.media;

import java.util.*;

import nl.vpro.i18n.Displayable;


/**
 * This was the original implementation of MSE-2417.  It is not used now, only for legacy mapping.
 */

public enum EpgGenreType implements Displayable {

    _0102("Jeugd - Film", MisGenreType.YOUTH, MisGenreType.MOVIE),
    _0103("Jeugd - Serie", MisGenreType.YOUTH, MisGenreType.SERIES_SOAP),
    _0104("Jeugd - Sport", MisGenreType.YOUTH, MisGenreType.SPORT),
    _0105("Jeugd - Muziek", MisGenreType.YOUTH, MisGenreType.MUSIC),
    _0106("Jeugd - Amusement", MisGenreType.YOUTH, MisGenreType.ENTERTAINMENT),
    _0107("Jeugd - Informatief", MisGenreType.YOUTH, MisGenreType.INFORMATIVE),
    _0111("Jeugd - Animatie", MisGenreType.YOUTH, MisGenreType.CARTOON),
    _0119("Jeugd - Spel/quiz", MisGenreType.YOUTH, MisGenreType.ENTERTAINMENT),
    _0108("Jeugd - Documentaire", MisGenreType.YOUTH, MisGenreType.DOCUMENTARY),
    _0125("Jeugd - Natuur", MisGenreType.YOUTH, MisGenreType.NATURE),
    _01("Jeugd", MisGenreType.YOUTH),

    _0209("Film - Komisch", MisGenreType.MOVIE, MisGenreType.COMEDY),
    _0210("Film - Spanning", MisGenreType.MOVIE, MisGenreType.CRIME),
    _0211("Film - Animatie", MisGenreType.MOVIE, MisGenreType.CARTOON),
    _0212("Film - Drama", MisGenreType.MOVIE, MisGenreType.DRAMA),
    _02("Film", MisGenreType.MOVIE),

    _0309("Serie - Komisch", MisGenreType.SERIES_SOAP, MisGenreType.COMEDY),
    _0310("Serie - Spanning", MisGenreType.SERIES_SOAP, MisGenreType.CRIME),
    _0311("Serie - Animatie", MisGenreType.SERIES_SOAP, MisGenreType.CARTOON),
    _0313("Serie - Soap serie", MisGenreType.SERIES_SOAP),
    _0312("Serie - Drama", MisGenreType.SERIES_SOAP, MisGenreType.DRAMA),
    _03("Serie", MisGenreType.SERIES_SOAP),

    _0414("Sport - Sport informatie", MisGenreType.SPORT),
    _0415("Sport - Sport wedstrijd", MisGenreType.SPORT),
    _04("Sport", MisGenreType.SPORT),

    _0516("Muziek - Klassieke muziek", MisGenreType.MUSIC),
    _0517("Muziek - Populaire muziek", MisGenreType.MUSIC),
    _05("Muziek", MisGenreType.MUSIC),

    _0619("Amusement - Spel/quiz", MisGenreType.ENTERTAINMENT),
    _0620("Amusement - Cabaret", MisGenreType.ENTERTAINMENT),
    _0609("Amusement - Komisch", MisGenreType.ENTERTAINMENT, MisGenreType.COMEDY),
    _06("Amusement", MisGenreType.ENTERTAINMENT),

    _0719("Informatief - Spel/quiz", MisGenreType.INFORMATIVE, MisGenreType.ENTERTAINMENT),
    _0721("Informatief - Nieuws/actualiteiten", MisGenreType.NEWS),
    _0722("Informatief - Gezondheid/opvoeding", MisGenreType.INFORMATIVE, MisGenreType.HEALTH),
    _0723("Informatief - Koken/eten", MisGenreType.INFORMATIVE),
    _0724("Informatief - Kunst/cultuur", MisGenreType.INFORMATIVE, MisGenreType.ART_CULTURE),
    _0725("Informatief - Natuur", MisGenreType.INFORMATIVE, MisGenreType.NATURE),
    _0726("Informatief - Religieus", MisGenreType.INFORMATIVE, MisGenreType.RELIGIOUS),
    _0727("Informatief - Wetenschap", MisGenreType.INFORMATIVE, MisGenreType.SCIENCE),
    _0728("Informatief - Consumenten informatie", MisGenreType.INFORMATIVE),
    _0729("Informatief - Reizen", MisGenreType.INFORMATIVE),
    _0730("Informatief - Geschiedenis", MisGenreType.INFORMATIVE),
    _0731("Informatief - Wonen/Tuin", MisGenreType.INFORMATIVE),
    _07("Informatief", MisGenreType.INFORMATIVE),

    _0822("Documentaire - Gezondheid/opvoeding", MisGenreType.DOCUMENTARY, MisGenreType.HEALTH),
    _0823("Documentaire - Koken/eten", MisGenreType.DOCUMENTARY),
    _0824("Documentaire - Kunst/cultuur", MisGenreType.INFORMATIVE, MisGenreType.ART_CULTURE),
    _0825("Documentaire - Natuur", MisGenreType.DOCUMENTARY, MisGenreType.NATURE),
    _0826("Documentaire - Religieus", MisGenreType.DOCUMENTARY, MisGenreType.RELIGIOUS),
    _0827("Documentaire - Wetenschap", MisGenreType.DOCUMENTARY, MisGenreType.SCIENCE),
    _0829("Documentaire - Reizen", MisGenreType.DOCUMENTARY),
    _0830("Documentaire - Geschiedenis", MisGenreType.DOCUMENTARY),

    _08("Documentaire", MisGenreType.DOCUMENTARY);

    private final String genreName;

    private final List<MisGenreType> legacyGenre;

    private final SortedSet<MisGenreType> legacyGenreAsSortedSet;


    EpgGenreType(String genreName, MisGenreType... legacy) {
        this.genreName = genreName;
        this.legacyGenre = Arrays.asList(legacy);
        this.legacyGenreAsSortedSet = new TreeSet<>(this.legacyGenre);
    }


    /**
     * Searches in a type on main and sub text values.
     * Not case sensitive!
     *
     * @param main
     * @param sub
     * @return
     */
    public static EpgGenreType find(String main, String sub) throws IllegalArgumentException {
        String value = concat(main, sub);
        for(EpgGenreType genreType : EpgGenreType.values()) {
            if(genreType.getDisplayName().equalsIgnoreCase(value)) {
                return genreType;
            }
        }
        throw new IllegalArgumentException("EpgGenreType: unknown genre: '" + value + "'");
    }

    public String getGenreName() {
        return genreName;
    }

    @Override
    public String getDisplayName() {
        return getGenreName();
    }

    public List<MisGenreType> getLegacyGenre() {
        return Collections.unmodifiableList(legacyGenre);
    }

    private static String concat(String mainGenreName, String subGenreName) {
        return mainGenreName + " - " + subGenreName;
    }

    public static SortedSet<EpgGenreType> valueOf(MisGenreType... more) {
        Set<MisGenreType> set = new HashSet<>(Arrays.asList(more));
        SortedSet<EpgGenreType> result = new TreeSet<>();

        // match all
        for(EpgGenreType EpgGenreType : values()) {
            if(EpgGenreType.legacyGenreAsSortedSet.size() > 1 && set.containsAll(EpgGenreType.legacyGenreAsSortedSet)) {
                result.add(EpgGenreType);
                set.removeAll(EpgGenreType.legacyGenre);
            }
        }
        // handle left overs
        for(MisGenreType l : set) {
            result.addAll(l.getEpgGenreType());
        }
        return result;
        //throw new IllegalArgumentException("Legacy genre not matched " + Arrays.asList(more));
    }

    public static SortedSet<EpgGenreType> valueOfLegacy(Collection<MisGenreType> col) {
        SortedSet<EpgGenreType> result = new TreeSet<>();
        result.addAll(valueOf(new ArrayList<>(col).toArray(new MisGenreType[col.size()])));
        return result;
    }

    /**
     */
    public static SortedSet<EpgGenreType> valueOf(Collection<String> genres) {
        SortedSet<EpgGenreType> result = new TreeSet<>();
        for(String genre : genres) {
            if(genre.length() > 0) {
                result.add(find(genre));
            }
        }
        return result;
    }

    public static EpgGenreType find(String s) {
        return EpgGenreType.valueOf(s);
    }
}
