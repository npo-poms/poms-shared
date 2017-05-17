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
@XmlType(name="AvailableSubtitlesType")
public class AvailableSubtitles {

	private Locale language;

	@Enumerated(EnumType.STRING)
	private SubtitlesType type;

	public AvailableSubtitles() {

	}

	public AvailableSubtitles(Locale language, SubtitlesType type) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AvailableSubtitles that = (AvailableSubtitles) o;

        if (language != null ? !language.equals(that.language) : that.language != null) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = language != null ? language.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
