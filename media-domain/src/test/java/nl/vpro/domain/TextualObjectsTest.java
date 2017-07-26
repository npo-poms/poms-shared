package nl.vpro.domain;

import org.junit.Test;

import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.support.Description;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;

import static nl.vpro.domain.TextualObjects.findOwnersForTextFields;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class TextualObjectsTest {


    @Test
    public void testFindOwnersForTextFieldsOnOrdering() throws Exception {
        final Program program = MediaBuilder.program()
            .titles(new Title("1", OwnerType.CERES, TextualType.MAIN))
            .descriptions(new Description("1", OwnerType.BROADCASTER, TextualType.EPISODE))
            .build();

        assertThat(findOwnersForTextFields(program).length).isEqualTo(2);
        assertThat(findOwnersForTextFields(program)[0]).isEqualTo(OwnerType.BROADCASTER);
    }

    @Test
    public void testFindOwnersForTextFieldsOnDuplicates() throws Exception {
        final Program program = MediaBuilder.program().titles(
            new Title("1", OwnerType.CERES, TextualType.MAIN),
            new Title("1", OwnerType.CERES, TextualType.EPISODE)
        ).build();

        assertThat(findOwnersForTextFields(program).length).isEqualTo(1);
    }

    @Test
    public void getMainTitle() {
        BasicTextualObject object = new BasicTextualObject();
        object.addTitle("a", OwnerType.BROADCASTER, TextualType.MAIN);
        object.addTitle("b", OwnerType.MIS, TextualType.MAIN);

        //TextualObjects.get(object.getTitles(), Arrays.asList())


    }


}
