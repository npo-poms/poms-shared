package nl.vpro.domain;

import lombok.extern.slf4j.Slf4j;

import java.util.SortedSet;

import org.junit.Assert;
import org.junit.Test;

import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.support.Description;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Title;

import static nl.vpro.domain.TextualObjects.findOwnersForTextFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

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


    @Test
    public void testUpdateDescriptionsForOwner() {
        Description e1 = new Description("e1", OwnerType.BROADCASTER, TextualType.MAIN);
        Description e2 = new Description("e2", OwnerType.NEBO, TextualType.MAIN); // Should stay
        Description e3 = new Description("e3", OwnerType.BROADCASTER, TextualType.SHORT); // Has to be deleted
        Description n1 = new Description("n1", OwnerType.BROADCASTER, TextualType.MAIN); // Should replace e1
        Description n2 = new Description("n2", OwnerType.NEBO, TextualType.SHORT); // Should not be added
        Description n3 = new Description("n3", OwnerType.BROADCASTER, TextualType.EPISODE); // Has to be added

        Program existing = new Program();
        Program incoming = new Program();

        existing.addDescription(e1);
        existing.addDescription(e2);
        existing.addDescription(e3);
        incoming.addDescription(n1);
        incoming.addDescription(n2);
        incoming.addDescription(n3);

        TextualObjects.updateDescriptionsForOwner(incoming, existing, OwnerType.BROADCASTER);

        Assert.assertThat("Insertion or ordering of new descriptions failed", existing.getDescriptions().first().getDescription(), equalTo(n1.getDescription()));
        Assert.assertThat("Deletion or ordering of obsolete descriptions failed", existing.getDescriptions().last().getDescription(), equalTo(n3.getDescription()));
        Assert.assertThat("Number of descriptions does not match", existing.getDescriptions().size(), equalTo(3));
    }


}
