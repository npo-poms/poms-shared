package nl.vpro.domain.api.thesaurus;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

import jakarta.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

import nl.vpro.domain.Change;
import nl.vpro.domain.gtaa.*;

@ToString
@EqualsAndHashCode(callSuper = true)
@XmlRootElement
@XmlSeeAlso({
    GTAAPerson.class,
    GTAATopic.class,
    GTAAGenre.class,
    GTAAGeographicName.class,
    GTAAMaker.class,
    GTAAName.class,
    GTAAGeographicName.class,
    GTAAClassification.class,
    GTAATopicBandG.class}
)
public class ThesaurusChange extends Change<GTAAConcept> {

    @XmlAttribute
    private Long sequence;

    @XmlAttribute
    private Long revision;

    @XmlAttribute
    private String mergedTo;

    public ThesaurusChange() {
    }

    @lombok.Builder
    public ThesaurusChange(Instant publishDate, String id, Boolean deleted, Boolean tail, GTAAConcept object) {
        super(publishDate, id, deleted, tail, false, object);

        // TODO
    }


    @Override
    @JsonIgnore
    @XmlAnyElement(lax = true)
    public GTAAConcept getObject() {
        return super.getObject();
    }

    @Override
    public void setObject(GTAAConcept p) {
        super.setObject(p);
    }



    @JsonProperty("object")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
    @JsonTypeIdResolver(GTAAConceptIdResolver.class)
    protected GTAAConcept getJsonObject() {
        return getObject();
    }

    protected void setJsonObject(GTAAConcept o) {
        setObject(o);
    }


}



