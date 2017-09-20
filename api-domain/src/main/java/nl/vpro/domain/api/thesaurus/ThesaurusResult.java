package nl.vpro.domain.api.thesaurus;

import lombok.NoArgsConstructor;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.media.gtaa.ThesaurusObject;

@XmlRootElement(name = "thesaurusItems")
@XmlType(name = "thesaurusItemsType")
@NoArgsConstructor
public class ThesaurusResult<T extends ThesaurusObject> extends Result<T> {

    public ThesaurusResult(List<T> list, Integer max) {
        super(list, 0L, max, null);
    }



}
