package nl.vpro.domain.constraint.page;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.constraint.AbstractFilter;
import nl.vpro.domain.constraint.Constraint;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Filter extends AbstractFilter<Page> {


    @lombok.Builder
    public Filter(Constraint<Page> constraint) {
        super(constraint);
    }

    public Filter() {
    }



    @JsonSerialize(using = AbstractFilter.Serializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonDeserialize(using = AbstractFilter.DeSerializer.class)
    @Override
    public Constraint<Page> getConstraint() {
        return super.getConstraint();
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
    public void setConstraint(Constraint<Page> constraint) {
        super.setConstraint(constraint);
    }

}
