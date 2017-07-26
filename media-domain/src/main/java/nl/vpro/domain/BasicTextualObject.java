package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class BasicTextualObject extends AbstractTextualObject<BasicOwnedText, BasicOwnedText, BasicTextualObject> {

    public BasicTextualObject() {
        super(BasicOwnedText::new, BasicOwnedText::new);
    }
}
