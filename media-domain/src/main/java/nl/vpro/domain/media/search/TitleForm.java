package nl.vpro.domain.media.search;

import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "titleFormType")
public class TitleForm {

    @XmlValue
    final private String text;

    @XmlAttribute
    final private TextualType type;

    @XmlAttribute
    final private OwnerType owner;

    @XmlAttribute
    final private boolean tokenized;

    private TitleForm() {
        this(null, false);
    }

    public TitleForm(String title, boolean tokenized) {
        this(title, TextualType.MAIN, null, tokenized);
    }

    public TitleForm(String title,  TextualType type, OwnerType owner, boolean tokenized) {
        this.text = title;
        this.type = type;
        this.owner = owner;
        this.tokenized = tokenized;
    }

    public String getTitle() {
        return text;
    }

    public TextualType getType() {
        return type;
    }

    public OwnerType getOwner() {
        return owner;
    }

    public boolean isTokenized() {
        return tokenized;
    }

}
