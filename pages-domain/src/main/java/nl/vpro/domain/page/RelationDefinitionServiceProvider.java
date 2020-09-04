package nl.vpro.domain.page;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Michiel Meeuwissen
 * @since 5.15.4
 */
@Slf4j
public class RelationDefinitionServiceProvider {

    private RelationDefinitionServiceProvider() {

    }

    private static RelationDefinitionService instance;

    public static void setInstance(RelationDefinitionService instance) {
        RelationDefinitionService previous = RelationDefinitionServiceProvider.instance;
        RelationDefinitionServiceProvider.instance = instance;
        if (previous == null || instance.equals(previous)) {
            log.info("Set relation definition service to {}", instance);
        } else {
            throw new IllegalStateException("Trying to replace a relation definition service " +  previous + " -> " + instance);
        }
    }

    public static RelationDefinitionService getInstance() {
        return instance;
    }
}
