package nl.vpro.domain.media;

import java.util.Locale;

import javax.xml.bind.JAXB;

import org.junit.Test;

import nl.vpro.domain.subtitles.SubtitlesType;

public class AvailableSubtitleTest {

    @Test
    public void test() {
        JAXB.marshal(new AvailableSubtitles(Locale.ENGLISH, SubtitlesType.TRANSLATION), System.out);
    }

}
