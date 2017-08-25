package nl.vpro.domain.api.media;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;

@XmlRootElement(name = "thesaurusUpdate")
@XmlType(name = "thesaurusUpdateType")
public class ThesaurusUpdates extends Result<Map<String, String>> {

    public ThesaurusUpdates() {
        
    }
    public ThesaurusUpdates(List<Map<String, String>> asList, long offset) {
        super(asList, offset, null, null);
    }

}
