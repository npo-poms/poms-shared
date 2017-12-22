package nl.vpro.domain.page.update;

import java.time.Instant;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.Change;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@XmlRootElement
@XmlType(name = "changeType")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso(PageUpdate.class)
public class PageUpdateChange extends Change<PageUpdate> {

    public PageUpdateChange() {
        super();
    }

    @lombok.Builder(builderClassName = "Builder")
    public PageUpdateChange(Instant publishDate, String id, Boolean deleted, Boolean tail, PageUpdate object) {
        super(publishDate, id, deleted, tail, object);
    }


    public static PageUpdateChange tail(Instant publishDate) {
        return PageUpdateChange.builder()
            .tail(true)
            .publishDate(publishDate)
            .build();
    }

    @Override
    @XmlElement
    public PageUpdate getObject() {
        return super.getObject();
    }
    @Override
    public void setObject(PageUpdate p) {
        super.setObject(p);
    }
}
