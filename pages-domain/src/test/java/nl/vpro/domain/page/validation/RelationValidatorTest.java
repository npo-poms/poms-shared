package nl.vpro.domain.page.validation;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.page.*;
import nl.vpro.domain.page.update.RelationUpdate;
import nl.vpro.domain.user.Broadcaster;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Michiel Meeuwissen
 * @since 5.15.4
 */
class RelationValidatorTest {

    @BeforeEach
    public void clearInstance() throws NoSuchFieldException, IllegalAccessException {

        Field instance = RelationDefinitionServiceProvider.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Test
    public void noInstance() {
        RelationValidator  v = new RelationValidator();
        assertThat(v.isValid(new RelationUpdate(), null)).isTrue();
    }

    @Test
    public void emptyInstance() {
        RelationDefinitionService service = mock(RelationDefinitionService.class);
        RelationDefinitionServiceProvider.setInstance(service);
        RelationValidator  v = new RelationValidator();
        assertThat(v.isValid(new RelationUpdate(), null)).isFalse();
    }

    @Test
    public void filledInstance() {
        RelationDefinitionService service = mock(RelationDefinitionService.class);
        RelationDefinitionServiceProvider.setInstance(service);
        when(service.get(eq("foo"), eq(new Broadcaster("VPRO")))).thenReturn(new RelationDefinition("foo", "VPRO"));
        RelationValidator  v = new RelationValidator();

        assertThat(v.isValid(new RelationUpdate("bar", "VPRO", null, "bla"), null)).isFalse();
        assertThat(v.isValid(new RelationUpdate("foo", "VPRO", null, "xxx"), null)).isTrue();
    }

}
