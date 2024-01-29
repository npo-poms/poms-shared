package nl.vpro.domain.media.search;

import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.media.RelationDefinition;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relationFormType", propOrder = {
        "text"
        })
public class RelationForm {

    @XmlAttribute
    final private String type;

    @XmlAttribute
    final private String broadcaster;

    @XmlAttribute
    final private String uriRef;

    @XmlValue
    final private String text;

    private RelationForm() {
        this(null, null, null, null);
    }

    public RelationForm(String type, String broadcaster, String uriRef, String text) {
        this.broadcaster = broadcaster;
        this.type = type;
        this.uriRef = uriRef;
        this.text = text;
    }

    public RelationForm(RelationDefinition def, String uriRef, String text) {
        this(def.getType(), def.getBroadcaster(), uriRef, text);
    }

    public String getType() {
        return type;
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public String getUriRef() {
        return uriRef;
    }

    public String getText() {
        return text;
    }


}
