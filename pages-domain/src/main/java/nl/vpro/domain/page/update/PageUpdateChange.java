package nl.vpro.domain.page.update;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.VersionedChange;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@XmlRootElement
@XmlType(name = "changeType")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso(PageUpdate.class)
public class PageUpdateChange extends VersionedChange<PageUpdate> implements Serializable {

    @Serial
    private static final long serialVersionUID = -3105890330183928614L;

    public PageUpdateChange() {
        super();
    }

    @lombok.Builder(builderClassName = "Builder")
    public PageUpdateChange(
        Instant publishDate,
        String id,
        Boolean deleted,
        Boolean tail,
        Integer version,
        PageUpdate object) {
        super(publishDate, id, deleted, tail, version, object);
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
    protected void setObject(PageUpdate p) {
        super.setObject(p);
    }
}
