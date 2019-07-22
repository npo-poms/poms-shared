package nl.vpro.domain.gtaa;

        import nl.vpro.w3.rdf.Description;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@GTAAScheme(Scheme.topic)
public class GTAATopic extends AbstractThesaurusItem {


    public static GTAATopic create(Description description) {
        final GTAATopic answer = new GTAATopic();
        fill(description, answer);
        return answer;
    }
}


