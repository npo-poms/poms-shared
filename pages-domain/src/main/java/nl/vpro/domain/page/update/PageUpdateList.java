package nl.vpro.domain.page.update;

import java.util.*;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

@XmlRootElement(name = "pages")
public class PageUpdateList implements Iterable<PageUpdate> {

    @XmlElement(name = "page")
    private Collection<@Valid @NonNull PageUpdate> list;


    public PageUpdateList(){

    }

    public PageUpdateList(List<PageUpdate> list) {
        this.list = list;
    }

    public static PageUpdateList of(List<PageUpdateBuilder> list) {
        return new PageUpdateList(list.stream().map(PageUpdateBuilder::build).toList());
    }

    public static PageUpdateList of(PageUpdateBuilder... list) {
        return of(Arrays.asList(list));
    }

    @Override
    @NonNull
    public Iterator<PageUpdate> iterator() {
        return list.iterator();
    }
}
