package nl.vpro.domain.api.media;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;

@XmlRootElement(name = "thesaurusItems")
@XmlType(name = "thesaurusItemsType")
public class ThesaurusResult extends Result<Map<String, String>> {

    public ThesaurusResult() {
    }

    public ThesaurusResult(List<Map<String, String>> asList, Long offset, Integer max) {
        super(asList, offset, max, null);
    }
    
    public ThesaurusResult(List<Map<String, String>> asList, Integer max) {
        super(asList, 0l, max, null);
    }

}
