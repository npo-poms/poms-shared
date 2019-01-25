package nl.vpro.domain.subtitles;

import lombok.Data;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.StringUtils;

/**
 * WEBVTT support settings per cue.
 * @author Michiel Meeuwissen
 * @since 5.10
 */
@Data
@XmlJavaTypeAdapter(CueSettings.Adapter.class)
public class CueSettings {

    /**
     *
     */
    String value;

    CueSettings(String settings) {
        this.value = settings;
    }

    public static CueSettings webvtt(String value) {
        return new CueSettings(value);
    }

    public static CueSettings copy(CueSettings settings) {
        if (settings == null) {
            return null;
        }
        return new CueSettings(settings.value);
    }
    public SubtitlesFormat getType() {
        return SubtitlesFormat.WEBVTT;
    }

    public static class Adapter extends XmlAdapter<String, CueSettings> {

        @Override
        public CueSettings unmarshal(String v) throws Exception {
            if (v == null) {
                return null;
            }
            return new CueSettings(v);

        }

        @Override
        public String marshal(CueSettings v) throws Exception {
            if (v == null || StringUtils.isEmpty(v.value)) {
                return null;
            }
            return v.value;

        }
    }
}
