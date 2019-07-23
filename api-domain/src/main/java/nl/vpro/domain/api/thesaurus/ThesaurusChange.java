package nl.vpro.domain.api.thesaurus;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Instant;

import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
public class ThesaurusChange extends Change<ThesaurusObject> {

    @XmlAttribute
    private Long sequence;

    @XmlAttribute
    private Long revision;

    @XmlAttribute
    private String mergedTo;

    public ThesaurusChange() {
    }

    @Builder
    public ThesaurusChange(Instant publishDate, String id, Boolean deleted, Boolean tail, ThesaurusObject object) {
        super(publishDate, id, deleted, tail, object);

        // TODO
    }


    @Override
    @JsonIgnore
    @XmlAnyElement
    public ThesaurusObject getObject() {
        return super.getObject();
    }

    @Override
    public void setObject(ThesaurusObject p) {
        super.setObject(p);
    }



    @JsonProperty("object")
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
    protected ThesaurusObject getJsonObject() {
        return getObject();
    }

    protected void setJsonObject(ThesaurusObject o) {
        setObject(o);
    }


}



