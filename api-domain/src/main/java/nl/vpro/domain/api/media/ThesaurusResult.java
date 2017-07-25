package nl.vpro.domain.api.media;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.media.gtaa.GTAAPerson;

@XmlRootElement(name = "thesaurusResult")
@XmlType(name = "thesaurusResultType")
public class ThesaurusResult extends Result<GTAAPerson>{

	public ThesaurusResult() {
		
	}
	
	public ThesaurusResult(List<GTAAPerson> asList, Long offset, Integer max, Long total) {
		super(asList, offset, max, total);
	}

	public ThesaurusResult(List<GTAAPerson> list, long offset, Integer max, int size) {
		super(list, offset, max, Long.valueOf(size));
	}

}
