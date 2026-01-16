package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class FacetResultItem {

    @Setter
    @Getter
    protected long count;

    private Boolean selected = null;

    protected FacetResultItem() {
    }

    protected FacetResultItem(long count) {
        this.count = count;
    }

    public abstract String getValue();

    public abstract void setValue(String name);


    public boolean isSelected() {
        return selected != null && selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected == Boolean.FALSE ? null : selected;
    }
}
