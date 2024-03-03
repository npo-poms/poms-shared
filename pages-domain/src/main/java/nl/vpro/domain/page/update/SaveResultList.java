package nl.vpro.domain.page.update;

import java.util.*;

import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * @since 8.0
 */
@XmlSeeAlso(SaveResult.class)
@XmlRootElement(name = "saveResults")
public class SaveResultList implements Iterable<SaveResult> {

    @XmlElement(name = "saveResult")
    private Collection<@Valid @NonNull SaveResult> list;


    public SaveResultList(){

    }

    public SaveResultList(List<SaveResult> list) {
        this.list = list;
    }


    @Override
    @NonNull
    public Iterator<SaveResult> iterator() {
        return list.iterator();
    }


}
