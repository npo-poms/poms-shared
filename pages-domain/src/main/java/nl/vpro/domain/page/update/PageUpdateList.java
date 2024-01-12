package nl.vpro.domain.page.update;

import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import nl.vpro.domain.media.update.collections.XmlCollection;

@XmlSeeAlso(PageUpdate.class)
@XmlRootElement(name = "pages")
public class PageUpdateList extends XmlCollection<PageUpdate> {

    public PageUpdateList(){

    }

    public PageUpdateList(List<PageUpdate> list) {
        super(list);
    }

    public static PageUpdateList of(List<PageUpdateBuilder> list) {
        return new PageUpdateList(list.stream().map(PageUpdateBuilder::build).toList());
    }

    public static PageUpdateList of(PageUpdateBuilder... list) {
        return of(Arrays.asList(list));
    }
}
