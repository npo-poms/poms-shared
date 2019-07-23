package nl.vpro.domain.gtaa;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.*;

import nl.vpro.openarchives.oai.Label;

/**
 * @since 5.11
 */
@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public class AbstractSimpleValueThesaurusItem extends AbstractThesaurusItem {

    public AbstractSimpleValueThesaurusItem(String id, List<Label> notes, String value, String redirectedFrom, Status status, Instant lastModified) {
        super(id, notes, value, redirectedFrom, status, lastModified);
    }

    public AbstractSimpleValueThesaurusItem() {
    }

    @Override
    @XmlElement
    public String getValue() {
         return super.getValue();
    }

}
