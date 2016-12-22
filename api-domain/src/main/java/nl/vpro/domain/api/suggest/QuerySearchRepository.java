package nl.vpro.domain.api.suggest;

import nl.vpro.domain.api.SuggestResult;

/**
 * @author Roelof Jan Koekoek
 * @since 3.2
 */
public interface QuerySearchRepository {

    void index(Query query);

    SuggestResult suggest(String input, String profile, Integer max);

}
