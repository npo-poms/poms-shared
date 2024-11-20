package nl.vpro.domain.media.search;

import lombok.Getter;
import lombok.Setter;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.bind.MediaFormTextJson;

/**
 * @since 8.4
 */
@Setter
@Getter
@JsonSerialize(using = MediaFormTextJson.Serializer.class)
@JsonDeserialize(using = MediaFormTextJson.Deserializer.class)
@XmlAccessorType(XmlAccessType.NONE)
public class MediaFormText {

    @XmlAttribute
    BooleanOperator booleanOperator = null;

    @XmlAttribute
    Boolean exactMatching = null;

    @XmlAttribute
    Boolean implicitWildcard = null;

    @XmlValue
    private String text;


    public boolean needsAttributes() {
        return booleanOperator != null || implicitWildcard != null || exactMatching != null;
    }


    public MediaFormText() {

    }

    public MediaFormText(String text) {
        this.text = text;
    }

    public enum BooleanOperator {
        AND,
        OR
    }

    public boolean isEmpty() {
        return text == null || text.isEmpty();
    }



    public boolean isQuoted() {
        if (! isEmpty() && text.length() > 2) {
            var first = text.charAt(0);
            if (first == '\'' || first == '"') {
                return text.charAt(text.length() - 1) == first;
            }
        }
        return false;
    }

    public String getUnQuotedValue() {
        if (isQuoted()) {
            return text.substring(1, text.length() - 1);
        } else {
            return text;
        }
    }

}
