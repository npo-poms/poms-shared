package nl.vpro.domain.api.page;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.page.Page;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlRootElement(name = "pageResult")
@XmlType(name = "pageResultType")
public class PageResult extends Result<Page> {

    public PageResult() {
    }

    public PageResult(List<? extends Page> pages, Long offset, Integer max, Total total) {
        super(pages, offset, max, total);
    }

    public PageResult(Result<? extends Page> pages) {
        super(pages);
    }
}
