package nl.vpro.domain.subtitles;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.*;

import java.time.Duration;
import java.util.Locale;

import javax.xml.XMLConstants;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.PolyNull;

import nl.vpro.xml.bind.LocaleAdapter;

import static nl.vpro.i18n.Locales.DUTCH;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@XmlRootElement(name = "standaloneCue")
@ToString(includeFieldNames = false, callSuper = true, of = {"language"})
@EqualsAndHashCode(callSuper = true)
@Getter
public class StandaloneCue extends Cue {

    @XmlAttribute(name = "lang", namespace = XMLConstants.XML_NS_URI)
    @XmlJavaTypeAdapter(LocaleAdapter.class)
    @Schema(implementation = String.class, type = "string")
    private Locale language;

    @XmlAttribute
    private SubtitlesType type = SubtitlesType.CAPTION;

    public static StandaloneCue translation(Cue cue, Locale locale) {
        return new StandaloneCue(cue, locale, SubtitlesType.TRANSLATION);
    }

    public static StandaloneCue tt888(Cue cue) {
        return new StandaloneCue(cue, DUTCH, SubtitlesType.CAPTION);
    }

    public static @PolyNull StandaloneCue of(@PolyNull Cue cue, SubtitlesId subtitles) {
        return of(cue, subtitles.getLanguage(), subtitles.getType());
    }

    public static @PolyNull StandaloneCue of(@PolyNull Cue cue, Locale language, SubtitlesType type) {
        if (cue == null) {
            return null;
        }
        return new StandaloneCue(cue, language, type);
    }

    protected StandaloneCue() {
    }

    public StandaloneCue(@NonNull Cue cue, Locale language, SubtitlesType type) {
        super(cue);
        this.language = language;
        this.type = type;
    }

    @lombok.Builder(builderClassName = "Builder", builderMethodName = "standaloneBuilder")
    StandaloneCue(
        String parent,
        int sequence,
        Duration start,
        Duration end,
        String content,
        Locale language,
        SubtitlesType type) {
        super(Cue.builder().parent(parent).sequence(sequence).start(start).end(end).content(content).build());
        this.language = language;
        this.type = type;
    }


    @XmlTransient
    public String getId() {
        return getParent() + "\t" + getType() + "\t" + getLanguage().toLanguageTag() + "\t" + getSequence();
        //return getParent() + "_" + getType() + "_" + getLocale() + "_" + getSequence();
    }

    public static CueId parseId(String id) {
        String[] split = id.split("\t", 4);
        return new CueId(SubtitlesId.builder()
            .mid(split[0])
            .type(SubtitlesType.valueOf(split[1]))
            .language(Locale.forLanguageTag(split[2]))
            .build(),
            Integer.parseInt(split[3]));
    }

    @XmlTransient
    public SubtitlesId getSubtitlesId() {
        return SubtitlesId.builder().mid(parent).language(language).type(type).build();
    }

}
