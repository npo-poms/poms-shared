package nl.vpro.domain.media;

import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.xml.bind.LocaleAdapter;

@Embeddable
@Cacheable
@XmlType(name="AvailableSubtitleType")
public class AvailableSubtitle {

	private Locale language;
	private String type;
	
	public AvailableSubtitle() {
		
	}
	
	public AvailableSubtitle(Locale language, String type) {
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
	public String getType() {
		return StringUtils.upperCase(type);
	}
	
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "AvailableSubtitles [language=" + language + ", type=" + type + "]";
	}
}
