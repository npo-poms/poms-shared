package nl.vpro.domain.api.page;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
@XmlEnum
@XmlType(name = "pageSortTypeEnum")
public enum PageSortField {
    sortDate, 
    lastModified, 
    lastPublished, 
    creationDate
}
