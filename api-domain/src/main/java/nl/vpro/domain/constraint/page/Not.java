package nl.vpro.domain.constraint.page;

import nl.vpro.domain.constraint.AbstractNot;
import nl.vpro.domain.constraint.Constraint;
import nl.vpro.domain.page.Page;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class Not extends AbstractNot<Page> {

    protected Not() {
    }

    public Not(Constraint<Page> constraint) {
        super(constraint);
    }

    @Override
    @XmlElements({
            @XmlElement(name = "and", type = And.class),
            @XmlElement(name = "or", type = Or.class),
            @XmlElement(name = "not", type = Not.class),
            @XmlElement(name = "broadcaster", type = BroadcasterConstraint.class),
            @XmlElement(name = "type", type = PageTypeConstraint.class),
            @XmlElement(name = "portal", type = PortalConstraint.class),
            @XmlElement(name = "section", type = SectionConstraint.class),
            @XmlElement(name = "genre", type = GenreConstraint.class),
    })
    public Constraint<Page> getConstraint() {
        return constraint;
    }


    @Override
    public void setConstraint(Constraint<Page> constraint) {
        this.constraint = constraint;
    }
}
