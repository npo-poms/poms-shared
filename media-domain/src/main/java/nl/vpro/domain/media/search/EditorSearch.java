package nl.vpro.domain.media.search;

import lombok.Data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlValue;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Data
@XmlAccessorType(XmlAccessType.NONE)
public class EditorSearch {


    @XmlAttribute
    private Boolean principalId;

    @XmlValue
    String text;

    public static EditorSearch name(String text) {
        if (text == null) {
            return null;
        }
        EditorSearch search = new EditorSearch();
        search.text = text;
        search.principalId = null;
        return search;
    }

    public static EditorSearch id(String text) {
        if (text == null) {
            return null;
        }
        EditorSearch search = new EditorSearch();
        search.text = text;
        search.principalId = true;
        return search;
    }

    public boolean isPrincipalId() {
        return principalId == Boolean.TRUE;
    }
}
