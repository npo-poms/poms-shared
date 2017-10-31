package nl.vpro.domain.media;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "audioAttributesType", propOrder = {
        "numberOfChannels",
        "audioCoding",
        "language"
        })
public class AudioAttributes implements Serializable {
    private static final long serialVersionUID = -1734599033747834638L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @XmlTransient
    private Long id;

    @XmlElement
    @Size(min = 1, max = 255)
    protected String audioCoding;

    @XmlElement
    protected Integer numberOfChannels;

    @XmlElement
    protected String language;

    public AudioAttributes() {
    }

    public AudioAttributes(AudioAttributes source) {
        this.audioCoding = source.audioCoding;
        this.numberOfChannels = source.numberOfChannels;
        this.language = source.language;
    }

    public static AudioAttributes copy(AudioAttributes source) {
        if(source == null) {
            return null;
        }
        return new AudioAttributes(source);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AudioAttributes(String audioCoding, Integer numberOfChannels) {
        this.audioCoding = audioCoding;
        this.numberOfChannels = numberOfChannels;
    }

    @lombok.Builder
    private AudioAttributes(String audioCoding, Integer numberOfChannels, Locale language) {
        this.audioCoding = audioCoding;
        this.numberOfChannels = numberOfChannels;
        this.language = language;
    }

    @Deprecated
    public AudioAttributes(String audioCoding, Integer numberOfChannels, String language) {
        this(audioCoding, numberOfChannels, new Locale(language));
    }

    public static AudioAttributes update(AudioAttributes from, AudioAttributes to) {
        if(from != null) {
            if(to == null) {
                to = new AudioAttributes();
            }

            to.setAudioCoding(from.getAudioCoding());
            to.setLanguage(from.getLanguage());
            to.setNumberOfChannels(from.getNumberOfChannels());

        } else {
            to = null;
        }

        return to;
    }

    public String getAudioCoding() {
        return audioCoding;
    }

    public void setAudioCoding(String coding) {
        this.audioCoding = coding;
    }

    public Integer getNumberOfChannels() {
        return numberOfChannels;
    }

    public void setNumberOfChannels(Integer numberOfChannels) {
        this.numberOfChannels = numberOfChannels;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("audioCoding", audioCoding)
            .append("numberOfChannels", numberOfChannels)
            .append("language", language)
            .toString();
    }
}
