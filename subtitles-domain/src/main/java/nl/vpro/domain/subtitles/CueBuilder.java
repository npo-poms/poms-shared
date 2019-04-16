package nl.vpro.domain.subtitles;

import java.time.Duration;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 * @deprecated Use {@link Cue#builder()}
 */
@Deprecated
public class CueBuilder {


    public static CueBuilder forMid(String mid) {
        CueBuilder builder = new CueBuilder();
        builder.cue = new Cue(mid, 0, null, null, null, null, null, null);
        return builder;
    }


    private Cue cue;

    public CueBuilder mid(String mid) {
        cue.parent = mid;
        return this;
    }

    public CueBuilder start(Duration duration) {
        cue.start = duration;
        return this;
    }

    public CueBuilder end(Duration duration) {
        cue.end = duration;
        return this;
    }

    public CueBuilder sequence(int seq) {
        cue.sequence = seq;
        return this;
    }

    public CueBuilder content(String content) {
        cue.content = content;
        return this;
    }

    public Cue build() {
        return cue;
    }



}
