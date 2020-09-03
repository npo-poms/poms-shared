package nl.vpro.domain.page;

import java.lang.reflect.InvocationTargetException;

import nl.vpro.domain.user.Broadcaster;

/**
 * @author Michiel Meeuwissen
 * @since 4.2
 */
public interface RelationDefinitionService {


    // TODO
    static RelationDefinitionService getInstance() {
        try {
            return (RelationDefinitionService) Class.forName("nl.vpro.domain.page.RelationDefinitionServiceImpl").getMethod("getInstance").invoke(null); } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    RelationDefinition get(String type, Broadcaster broadcaster);


}
