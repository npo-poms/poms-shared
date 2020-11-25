package nl.vpro.domain.media;

import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.xml.sax.InputSource;

import nl.vpro.domain.classification.*;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
public class MediaClassificationService extends AbstractClassificationServiceImpl {

    private static MediaClassificationService instance;

    public static MediaClassificationService getInstance() {
        if (instance != null){
            return instance;
        }
        return new MediaClassificationService();
    }
    public MediaClassificationService() {
        if (instance == null) {
            instance = this;
        }
    }



    static final String EPG_PREFIX = "urn:tva:metadata:cs:2004:";

    static final String MIS_PREFIX = "urn:mis:genre:";


    @NonNull
    public static Term getTermByEpgCode(String code) {
        if (!code.startsWith(EPG_PREFIX)) {
            // To allow migration
            code = EPG_PREFIX + code;
        }
        for (Term term : getInstance().values()) {
            if (getEpgCodes(term).contains(code)) {
                return term;
            }
        }
        throw new IllegalArgumentException("No such EPG code " + code);
    }


    static List<String> getMisCodes(Term term) {
        List<String> misCodes = new ArrayList<>();
        for (Reference reference : term.getReferences()) {
            if (reference.getValue().startsWith(MIS_PREFIX)) {
                misCodes.add(reference.getValue());
            }
        }
        return misCodes;
    }


    public static List<Term> getTermsByMisGenreType(String... pointers) {
        List<MisGenreType> misTypes = new ArrayList<>(pointers.length);
        for (String pointer : pointers) {
            misTypes.add(MisGenreType.find(pointer));
        }

        return getTermsByMisGenreType(misTypes);
    }


    public static List<String> getEpgCodes(Term term) {
        final List<String> epgCodes = new ArrayList<>();
        for (Reference reference : term.getReferences()) {
            if (reference.getValue().startsWith(EPG_PREFIX)) {
                epgCodes.add(reference.getValue());
            }
        }
        return epgCodes;
    }

    public static List<Term> getTermsByMisGenreType(List<MisGenreType> misTypes) {
        final SortedSet<EpgGenreType> epgTypes = EpgGenreType.valueOfLegacy(misTypes);

        SortedSet<Term> terms = new TreeSet<>();
        for (EpgGenreType epgType : epgTypes) {
            Term term = getTermByEpgCode(epgType.name().substring(1));
            if (term != null) {
                terms.add(term);
            }
        }

        return new ArrayList<>(terms);
    }

    public static List<String> getLegacyMisGenres(String termId) throws TermNotFoundException {
        final List<String> answer = new ArrayList<>();

        Term term = getInstance().getTerm(termId);

        if (term.getParent() != null && !getMisCodes(term.getParent()).isEmpty()) {
            String raw = getRawMisCode(getMisCodes(term.getParent()).get(0));
            if (! answer.contains(raw)) {
                answer.add(raw);
            }
        }
        for (String code : getMisCodes(term)) {
            String raw = getRawMisCode(code);
            if (! answer.contains(raw)) {
                answer.add(raw);
            }
        }

        return answer;
    }


    private static String getRawMisCode(String fullMisCode) {
        return fullMisCode.substring(MIS_PREFIX.length());
    }

    private InputSource getInputSource() {
        String path = "/nl/vpro/domain/media/classification/ebu_ContentGenreCS.xml";
        InputSource source = new InputSource(getClass().getResourceAsStream(path));
        source.setSystemId("classpath:" + path);
        return source;
    }

    @Override
    protected List<InputSource> getSources(boolean init) {
        return Collections.singletonList(getInputSource());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + getInputSource().getSystemId();
    }
    @Override
    public boolean equals(Object o) {
        return o != null && o.getClass().equals(MediaClassificationService.class);
    }
}
