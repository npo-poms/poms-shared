package nl.vpro.domain.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class FacetResultItem {

    protected long count;

    private Boolean selected = null;

    protected FacetResultItem() {
    }

    protected FacetResultItem(long count) {
        this.count = count;
    }

    public abstract String getValue();

    public abstract void setValue(String name);

    public long getCount() {
        return count;
    }

    public void setCount(long l) {
        this.count = l;
    }


    public boolean isSelected() {
        return selected != null && selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected == Boolean.FALSE ? null : selected;
    }
}
