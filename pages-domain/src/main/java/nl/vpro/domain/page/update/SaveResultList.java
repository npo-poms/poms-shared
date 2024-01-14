package nl.vpro.domain.page.update;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import nl.vpro.domain.media.update.collections.XmlCollection;

@XmlSeeAlso(SaveResult.class)
@XmlRootElement(name = "saveResults")
public class SaveResultList extends XmlCollection<SaveResult> {

    public SaveResultList(){

    }

    public SaveResultList(List<SaveResult> list) {
        super(list);
    }


}
