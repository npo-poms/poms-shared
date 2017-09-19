package nl.vpro.domain.page.update;

import lombok.Builder;

import java.time.Instant;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.AbstractChange;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@XmlRootElement
@XmlType(name = "changeType")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso(PageUpdate.class)
public class PageUpdateChange extends AbstractChange<PageUpdate> {

    public PageUpdateChange() {
        super();
    }

    @Builder
    public PageUpdateChange(Instant publishDate, String id, Boolean deleted, Boolean tail, PageUpdate object) {
        super(publishDate, id, deleted, tail, object);
    }

    @XmlElement
    public PageUpdate getObject() {
        return super.getObject();
    }
    public void setObject(PageUpdate p) {
        super.setObject(p);
    }
}
