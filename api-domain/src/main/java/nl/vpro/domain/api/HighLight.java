package nl.vpro.domain.api;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.*;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "highlightType")
public class HighLight {


    @XmlAttribute
    private String term;

    private List<String> body;

    public HighLight() {

    }

    public HighLight(String term, String... body) {
        this.term = term;
        this.body = Arrays.asList(body);

    }


    public String getTerm() {
        return term;
    }

    public List<String> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return term + ":" + body;
    }
}
