package nl.vpro.domain.api.thesaurus;

import lombok.NoArgsConstructor;

import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.gtaa.*;

@XmlRootElement(name = "thesaurusItems")
@XmlType(name = "thesaurusItemsType")
@NoArgsConstructor
@XmlSeeAlso({
    GTAAPerson.class,
    GTAATopic.class,
    GTAAGenre.class,
    GTAAGeographicName.class,
    GTAAMaker.class,
    GTAAName.class,
    GTAAGeographicName.class,
    GTAAClassification.class,
    GTAATopicBandG.class
})public class ThesaurusResult<T extends GTAAConcept> extends Result<T> {

    public ThesaurusResult(List<T> list, Integer max) {
        super(list, 0L, max, Total.MISSING);
    }



}
