package nl.vpro.domain.media;

import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.xml.bind.LocaleAdapter;

@Embeddable
@Cacheable
@XmlType(name="AvailableSubtitleType")
public class AvailableSubtitle {

	private Locale language;

	@Enumerated(EnumType.STRING)
	private SubtitlesType type;

	public AvailableSubtitle() {

	}

	public AvailableSubtitle(Locale language, SubtitlesType type) {
		this.language = language;
		this.type = type;

	}

	@XmlJavaTypeAdapter(LocaleAdapter.class)
	@XmlAttribute
	public Locale getLanguage() {
		return language;
	}

	public void setLanguage(Locale language) {
		this.language = language;
	}

	@XmlAttribute
	public SubtitlesType getType() {
		return type;
	}

	public void setType(SubtitlesType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "AvailableSubtitles [language=" + language + ", type=" + type + "]";
	}
}
