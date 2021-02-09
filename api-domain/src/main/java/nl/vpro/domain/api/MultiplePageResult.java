package nl.vpro.domain.api;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.constraint.DisplayablePredicate;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 3.3
 */
@XmlRootElement(name = "multiplePageResult")
@XmlType(name = "multiplePageResultType")
@XmlSeeAlso(MultiplePageEntry.class)
public class MultiplePageResult extends AbstractMultipleResult<Page> {

    public MultiplePageResult() {
        super(MultiplePageEntry::new);
    }

    public MultiplePageResult(List<String> ids, List<Page> pages, DisplayablePredicate<Page> predicate) {
        super(MultiplePageEntry::new, ids, pages, predicate);
    }
}
