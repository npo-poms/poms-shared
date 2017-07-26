package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class BasicTextualUpdate extends AbstractTextualObjectUpdate<BasicTypedText, BasicTypedText, BasicTextualUpdate> {

    public BasicTextualUpdate() {
        super(BasicTypedText::new, BasicTypedText::new);
    }
}
