package nl.vpro.domain.gtaa;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public abstract class AbstractGTAANewConcept implements GTAANewConcept {

    @Getter
    @Setter
    @XmlElement(name = "scopeNote")
    @JsonProperty("scopeNotes")
    List<String> scopeNotes;


    public String getGtaaUri() {
        return null;
    }
}
