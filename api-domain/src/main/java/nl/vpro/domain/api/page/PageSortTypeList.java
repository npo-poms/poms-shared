package nl.vpro.domain.api.page;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

/**
* @author Michiel Meeuwissen
* @since 3.4
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageSortListType")
public class PageSortTypeList {

    private List<PageSortType> sort;

    public PageSortTypeList() {
    }

    public PageSortTypeList(List<PageSortType> sort) {
        this.sort = sort;
    }

    public List<PageSortType> getSort() {
        return sort;
    }

    public void setSort(List<PageSortType> sort) {
        this.sort = sort;
    }
}
