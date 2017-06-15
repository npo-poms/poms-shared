package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.vpro.domain.media.Relation;
import nl.vpro.domain.media.RelationDefinition;
import nl.vpro.transfer.extjs.ExtRecord;
import nl.vpro.transfer.extjs.media.support.RelationDefinitionView;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "id",
        "key",
        "uriRef",
        "text"
        })
public class RelationView extends ExtRecord {
    private Long id;
    
    private RelationDefinitionView relationDefinitionView;
    
    private String uriRef;
    
    private String text;

    RelationView() {
    }

    private RelationView(Long id, RelationDefinitionView relationDefinitionView, String uriRef, String text) {
        this.id = id;
        this.relationDefinitionView = relationDefinitionView;
        this.uriRef = uriRef;
        this.text = text;
    }
    
    public static RelationView create(Relation fullRelation) {
        return new RelationView(
                fullRelation.getId(),
                RelationDefinitionView.create(fullRelation.getDefinition()),
                fullRelation.getUriRef(),
                fullRelation.getText()
        );
    }

    public Relation toRelation() {
        Relation relation = new Relation();
        relation.setId(id);
        updateTo(relation);
        return relation;
    }

    public void updateTo(Relation relation) {
        relation.setDefinition(relationDefinitionView.toRelationDefinition());
        relation.setUriRef(uriRef);
        relation.setText(text);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return relationDefinitionView.getKey();
    }

    public void setKey(String key) {
        relationDefinitionView = new RelationDefinitionView();
        relationDefinitionView.setKey(key);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUriRef() {
        return uriRef;
    }

    public void setUriRef(String uriRef) {
        this.uriRef = uriRef;
    }
}
