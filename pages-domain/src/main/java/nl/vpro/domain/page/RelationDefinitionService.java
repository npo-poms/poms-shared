package nl.vpro.domain.page;

import nl.vpro.domain.user.Broadcaster;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
public interface RelationDefinitionService {




    @Deprecated
    static RelationDefinitionService getInstance() {
        return RelationDefinitionServiceProvider.getInstance();
    }

    RelationDefinition get(String type, Broadcaster broadcaster);


}
