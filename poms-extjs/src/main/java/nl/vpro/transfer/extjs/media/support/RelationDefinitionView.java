package nl.vpro.transfer.extjs.media.support;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.vpro.domain.media.RelationDefinition;

@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
        "key",
        "displayText"
        })
public class RelationDefinitionView {
    
    private static final Pattern KEY_PATTERN = Pattern.compile("^\\[(.*)\\]-\\[(.*)\\]$");

    private String type;

    private String broadcaster;

    private String text;

    public RelationDefinitionView() {
    }

    public RelationDefinitionView(String type, String broadcaster, String text) {
        this.type = type;
        this.broadcaster = broadcaster;
        this.text = text;
    }

    public static RelationDefinitionView create(RelationDefinition fullRelationDefinition) {
        return new RelationDefinitionView(fullRelationDefinition.getType(), fullRelationDefinition.getBroadcaster(), fullRelationDefinition.getDisplayText());
    }

    public RelationDefinition toRelationDefinition() {
        return new RelationDefinition(type, broadcaster, text);
    }

    public String getKey() {
        StringBuffer sb = new StringBuffer()
            .append('[')
            .append(broadcaster)
            .append("]-[")
            .append(type)
            .append(']');

        return sb.toString();
    }

    public void setKey(String key) {
        Matcher matcher = KEY_PATTERN.matcher(key);
        if(!matcher.find()) {
            throw new IllegalArgumentException("Not a well formed key: " + key);
        }

        this.broadcaster = matcher.group(1);
        this.type = matcher.group(2);
    }

    public String getDisplayText() {
        return broadcaster + " :: " + text;
    }

    public void setDisplayText(String text) {
        // JAXB do nothing
    }
}