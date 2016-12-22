package nl.vpro.domain.api.media;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
* @author Michiel Meeuwissen
* @since 3.3
*/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "mediaSortListType")
public class MediaSortTypeList {

    private List<MediaSortType> sort;

    public MediaSortTypeList() {
    }

    public MediaSortTypeList(List<MediaSortType> sort) {
        this.sort = sort;
    }

    public List<MediaSortType> getSort() {
        return sort;
    }

    public void setSort(List<MediaSortType> sort) {
        this.sort = sort;
    }
}
