package nl.vpro.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;

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
@SuppressWarnings("ConstantConditions")
@Slf4j
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
        object.addTitle("c", OwnerType.BEELDENGELUID, TextualType.MAIN);


        assertThat(TextualObjects.expand(object.getTitles(), TextualType.MAIN).get().get()).isEqualTo("a");
        assertThat(TextualObjects.expand(object.getTitles(), TextualType.MAIN, OwnerType.MIS).get().get()).isEqualTo("b");

    }

    @Test
    public void getLexicoTitle() {
        BasicTextualObject object = new BasicTextualObject();
        object.addTitle("a", OwnerType.BROADCASTER, TextualType.MAIN);
        object.addTitle("b", OwnerType.MIS, TextualType.MAIN);
        object.addTitle("c", OwnerType.BEELDENGELUID, TextualType.MAIN);


        assertThat(TextualObjects.expand(object.getTitles(), TextualType.LEXICO).get().get()).isEqualTo("a");
        assertThat(TextualObjects.expand(object.getTitles(), TextualType.LEXICO, OwnerType.MIS).get().get()).isEqualTo("b");
    }


    @Test
    public void expand() {
        BasicTextualObject object = new BasicTextualObject();
        object.addTitle("a", OwnerType.BROADCASTER, TextualType.MAIN);
        object.addTitle("b", OwnerType.MIS, TextualType.MAIN);
        object.addTitle("c", OwnerType.BEELDENGELUID, TextualType.MAIN);

        object.addTitle("d", OwnerType.MIS, TextualType.SUB);

        SortedSet<BasicOwnedText> basicOwnedTexts = TextualObjects.expandTitles(object);

        assertThat(basicOwnedTexts.toString()).isEqualTo("[MAIN:BROADCASTER:a, MAIN:NPO:a, MAIN:MIS:b, MAIN:BEELDENGELUID:c, SUB:BROADCASTER:d, SUB:NPO:d, SUB:MIS:d, LEXICO:BROADCASTER:a, LEXICO:NPO:a]");
        basicOwnedTexts.forEach(bo -> log.info("{}", bo));



    }

    @Test
    public void expandMajor() {
        BasicTextualObject object = new BasicTextualObject();
        object.addTitle("a", OwnerType.BROADCASTER, TextualType.MAIN);
        object.addTitle("b", OwnerType.MIS, TextualType.MAIN);
        object.addTitle("c", OwnerType.BEELDENGELUID, TextualType.MAIN);

        object.addTitle("d", OwnerType.MIS, TextualType.SUB);

        SortedSet<BasicOwnedText> basicOwnedTexts = TextualObjects.expandTitlesMajorOwnerTypes(object);
        assertThat(basicOwnedTexts).hasSize(6);
        assertThat(basicOwnedTexts.toString()).isEqualTo("[MAIN:BROADCASTER:a, MAIN:NPO:a, SUB:BROADCASTER:d, SUB:NPO:d, LEXICO:BROADCASTER:a, LEXICO:NPO:a]");
        basicOwnedTexts.forEach(bo -> log.info("{}", bo));
    }


}
