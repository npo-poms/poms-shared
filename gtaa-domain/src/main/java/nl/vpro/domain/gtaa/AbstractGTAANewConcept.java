package nl.vpro.domain.gtaa;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonProperty;

@XmlTransient
@XmlAccessorType(XmlAccessType.NONE)
public abstract sealed class AbstractGTAANewConcept implements GTAANewConcept
    permits GTAANewGenericConcept, GTAANewPerson {

    @Getter
    @Setter
    @XmlElement(name = "scopeNote")
    @JsonProperty("scopeNotes")
    List<String> scopeNotes;


    public String getGtaaUri() {
        return null;
    }
}
