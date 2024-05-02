package nl.vpro.domain.subtitles;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

/**
 * WEBVTT support settings per cue. For now this simply wraps the WebVTT string.
 *
 * @author Michiel Meeuwissen
 * @since 5.10
 */
@Data
@XmlJavaTypeAdapter(CueSettings.Adapter.class)
@EqualsAndHashCode
public class CueSettings implements Serializable {

    /**
     *
     */
    String value;

    CueSettings(String settings) {
        this.value = settings;
    }

    public static CueSettings webvtt(String value) {
        return value == null ? null : new CueSettings(value);
    }

    public static CueSettings copy(CueSettings settings) {
        if (settings == null) {
            return null;
        }
        return new CueSettings(settings.value);
    }

    /**
     * The type of the value of the settings.
     * @return Currently always {@link SubtitlesFormat#WEBVTT}
     */
    public SubtitlesFormat getType() {
        return SubtitlesFormat.WEBVTT;
    }

    public static class Adapter extends XmlAdapter<String, CueSettings> {

        @Override
        public CueSettings unmarshal(String v) {
            if (v == null) {
                return null;
            }
            return new CueSettings(v);

        }

        @Override
        public String marshal(CueSettings v) {
            if (v == null || StringUtils.isEmpty(v.value)) {
                return null;
            }
            return v.value;

        }
    }
}
