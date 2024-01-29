package nl.vpro.domain.api.page;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

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
