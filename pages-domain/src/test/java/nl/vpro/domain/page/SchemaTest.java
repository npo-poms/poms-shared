package nl.vpro.domain.page;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.page.update.*;
import nl.vpro.test.util.jaxb.AbstractSchemaTest;

public class SchemaTest extends AbstractSchemaTest {

    @Override
    // todo, how about jaxb.index, containing the same list?
    protected Class<?>[] getClasses() {
        return new Class[] {
            Page.class,
            PageUpdate.class,
            EmbedUpdate.class,
            ImageUpdate.class,
            LinkUpdate.class,
            PageUpdateList.class,
            SaveResultList.class,
            DeleteResult.class
        };
    }

    @ParameterizedTest
    @ValueSource(strings = {Xmlns.PAGE_NAMESPACE, Xmlns.PAGEUPDATE_NAMESPACE})
    public void test(String namespace) throws IOException {
        testNamespace(namespace);
    }

}
