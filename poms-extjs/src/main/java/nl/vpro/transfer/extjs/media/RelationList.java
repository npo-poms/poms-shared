package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.SortedSet;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Relation;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "relations")
public class RelationList extends TransferList<RelationView> {
    
    public RelationList() {
    }
    
    public RelationList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public static RelationList create(MediaObject media) {
        SortedSet<Relation> fullList = media.getRelations();

        RelationList simpleList = new RelationList();
        simpleList.success = true;
        
        if(fullList == null) {
            return simpleList;
        }

        for(Relation relation : fullList) {
            simpleList.add(RelationView.create(relation));
        }

        return simpleList;
    }
}