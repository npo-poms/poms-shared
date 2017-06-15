package nl.vpro.transfer.extjs.media.support;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;
import nl.vpro.domain.media.RelationDefinition;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "relationdefinitions")
public class RelationDefinitionsList extends TransferList<RelationDefinitionView> {
    
    private RelationDefinitionsList() {
    }
    
    public static RelationDefinitionsList create(List<RelationDefinition> relationDefinitions) {
        RelationDefinitionsList relationDefinitionsList = new RelationDefinitionsList();
        
        for (RelationDefinition relationDefinition : relationDefinitions) {
            relationDefinitionsList.add(RelationDefinitionView.create(relationDefinition));
        }
        
        relationDefinitionsList.success = true;
        relationDefinitionsList.results = relationDefinitions.size();
        return relationDefinitionsList;
    }
}