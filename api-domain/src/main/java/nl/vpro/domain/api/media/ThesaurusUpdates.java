package nl.vpro.domain.api.media;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.api.media.ThesaurusUpdates.ThesaurusUpdate;
import nl.vpro.domain.media.gtaa.GTAAPerson;

@XmlRootElement(name = "thesaurusResult")
@XmlType(name = "thesaurusResultType")
public class ThesaurusUpdates extends Result<ThesaurusUpdate> {

	public ThesaurusUpdates(List<ThesaurusUpdate> asList, long l, Integer max, long m) {
		super(asList, l, max, m);
	}

	public static class ThesaurusUpdate {
		@XmlAttribute
		private GTAAPerson person;
		@XmlAttribute
		private Instant updatedAt;

		public ThesaurusUpdate(GTAAPerson person, Instant updatedAt) {
			this.person = person;
			this.updatedAt = updatedAt;
		}
	}
}
