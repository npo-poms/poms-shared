package nl.vpro.domain.api.media;

import java.time.Instant;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.api.media.ThesaurusUpdates.ThesaurusUpdate;
import nl.vpro.domain.media.gtaa.GTAAPerson;
import nl.vpro.xml.bind.InstantXmlAdapter;

@XmlRootElement(name = "thesaurusResult")
@XmlType(name = "thesaurusResultType")
public class ThesaurusUpdates extends Result<ThesaurusUpdate> {

	public ThesaurusUpdates(List<ThesaurusUpdate> asList, long offset) {
		super(asList, offset, null, null);
	}

	public static class ThesaurusUpdate {
		@XmlAttribute
		private GTAAPerson person;
		@XmlAttribute
		@XmlJavaTypeAdapter(InstantXmlAdapter.class)
		private Instant updatedAt;

		public ThesaurusUpdate(GTAAPerson person, Instant updatedAt) {
			this.person = person;
			this.updatedAt = updatedAt;
		}
	}
}
