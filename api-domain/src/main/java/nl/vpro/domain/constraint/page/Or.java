package nl.vpro.domain.constraint.page;

import nl.vpro.domain.constraint.AbstractOr;
import nl.vpro.domain.constraint.Constraint;
import nl.vpro.domain.page.Page;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class Or extends AbstractOr<Page> {

    protected Or() {
    }

    @SafeVarargs
    public Or(Constraint<Page>... constraints) {
        super(constraints);
    }

    public Or(List<Constraint<Page>> constraints) {
        super(constraints);
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
    public List<Constraint<Page>> getConstraints() {
        return constraints;
    }
}
