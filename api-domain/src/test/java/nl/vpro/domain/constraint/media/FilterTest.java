package nl.vpro.domain.constraint.media;

import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.util.Locale;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.constraint.PredicateTestResult;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.logging.LoggerOutputStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@Slf4j
public class FilterTest {

    @BeforeEach
    public void setup() {
        Locale.setDefault(Locale.US);
    }

    @Test
    public void testApply() {
        Filter filter = new Filter();
        filter.setConstraint(MediaConstraints.alwaysFalse());

        PredicateTestResult r = filter.testWithReason(MediaTestDataBuilder.broadcast().build());

        assertThat(r.applies()).isFalse();
        assertThat(r.getDescription(Locale.US)).isEqualTo("Never matches");
        assertThat(r.getReason()).isEqualTo("AlwaysFalse");
    }

    @Test
    public void netinnl_ar() {
        Filter filter = JAXB.unmarshal(new StringReader(
            "    <media:filter xmlns:media=\"urn:vpro:api:constraint:media:2013\">\n" +
            "      <media:and>\n" +
            "        <media:or>\n" +
            "          <media:descendantOf>POMS_S_VPRO_3381073</media:descendantOf>\n" +
            "          <media:descendantOf>POMS_S_VPRO_1409087</media:descendantOf>\n" +
            "          <media:descendantOf>POMS_S_VPRO_1409088</media:descendantOf>\n" +
            "        </media:or>\n" +
            "        <media:or>\n" +
            "          <media:not>\n" +
            "            <media:isExclusive/>\n" +
            "          </media:not>\n" +
            "          <media:exclusive>NETINNL</media:exclusive>\n" +
            "        </media:or>\n" +
            "      </media:and>\n" +
            "    </media:filter>\n"), Filter.class);
        log.info("{}", filter);
        Program program = JAXB.unmarshal(new StringReader("<program xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" type=\"CLIP\" avType=\"VIDEO\" embeddable=\"true\" mid=\"POMS_KRO_3852926\" sortDate=\"2016-05-05T14:31:30.329+02:00\" creationDate=\"2016-05-05T14:31:30.329+02:00\" lastModified=\"2018-01-03T10:33:08.728+01:00\" publishDate=\"2018-01-03T10:33:30.680+01:00\" urn=\"urn:vpro:media:program:75670889\" workflow=\"PUBLISHED\">\n" +
            "<broadcaster id=\"KRO\">KRO</broadcaster>\n" +
            "<portal id=\"NETINNL\">NETINNL</portal>\n" +
            "<exclusive portalId=\"NETINNL\"/>\n" +
            "<title owner=\"BROADCASTER\" type=\"MAIN\">حب من...</title>\n" +
            "<title owner=\"BROADCASTER\" type=\"SUB\">هستر وداويت</title>\n" +
            "<description owner=\"BROADCASTER\" type=\"MAIN\">\n" +
            "هستر وداويت متأكدان أنهما يريدان أن يتشاركا حياتهما في كل شيء. لكن داويت لا يشعر بالارتياح في منطة آخترهوك. هل يمكن أن يشعر داويت بالسعادة في آخترهوك البيضاء؟\n" +
            "</description>\n" +
            "<genre id=\"3.0.1.7\">\n" +
            "<term>Informatief</term>\n" +
            "</genre>\n" +
            "<availableSubtitles language=\"ar\" type=\"TRANSLATION\"/>\n" +
            "<duration>P0DT0H46M3.890S</duration>\n" +
            "<credits/>\n" +
            "<descendantOf urnRef=\"urn:vpro:media:group:71834908\" midRef=\"POMS_S_VPRO_3381073\" type=\"PLAYLIST\"/>\n" +
            "<descendantOf urnRef=\"urn:vpro:media:group:72865615\" midRef=\"POMS_S_VPRO_3512033\" type=\"PLAYLIST\"/>\n" +
            "<descendantOf urnRef=\"urn:vpro:media:group:74841750\" midRef=\"POMS_S_NTR_3760375\" type=\"PLAYLIST\"/>\n" +
            "<memberOf added=\"2018-01-03T10:14:19.313+01:00\" highlighted=\"false\" midRef=\"POMS_S_VPRO_3381073\" index=\"12426\" type=\"PLAYLIST\" urnRef=\"urn:vpro:media:group:71834908\"/>\n" +
            "<memberOf added=\"2018-01-03T10:33:08.605+01:00\" highlighted=\"false\" midRef=\"POMS_S_VPRO_3512033\" index=\"6092\" type=\"PLAYLIST\" urnRef=\"urn:vpro:media:group:72865615\"/>\n" +
            "<memberOf added=\"2016-06-02T13:55:11.925+02:00\" highlighted=\"false\" midRef=\"POMS_S_NTR_3760375\" index=\"5\" type=\"PLAYLIST\" urnRef=\"urn:vpro:media:group:74841750\"/>\n" +
            "<locations>\n" +
            "<location owner=\"BROADCASTER\" creationDate=\"2016-05-05T14:31:30.292+02:00\" lastModified=\"2016-05-05T14:31:30.337+02:00\" urn=\"urn:vpro:media:location:75670895\" workflow=\"PUBLISHED\">\n" +
            "<programUrl>mid://netinnederland.nl/KN_1662067</programUrl>\n" +
            "<avAttributes>\n" +
            "<bitrate>10000</bitrate>\n" +
            "<avFileFormat>UNKNOWN</avFileFormat>\n" +
            "</avAttributes>\n" +
            "</location>\n" +
            "</locations>\n" +
            "<scheduleEvents/>\n" +
            "<relation broadcaster=\"VPRO\" type=\"TRANSLATION_SOURCE\" urn=\"urn:vpro:media:relation:75670897\">KN_1662067</relation>\n" +
            "<images>\n" +
            "<shared:image owner=\"BROADCASTER\" type=\"STILL\" highlighted=\"false\" creationDate=\"2016-05-05T14:31:30.314+02:00\" lastModified=\"2016-05-05T14:31:30.335+02:00\" urn=\"urn:vpro:media:image:75670891\" workflow=\"PUBLISHED\">\n" +
            "<shared:title>Liefs Uit..</shared:title>\n" +
            "<shared:description>\n" +
            "Dawit en Hester weten zeker dat ze hun leven samen willen delen, maar de Ethiopische Dawit voelt hij zich ongemakkelijk in de Achterhoek. Kan Dawit wel gelukkig zijn in de witte Achterhoek?\n" +
            "</shared:description>\n" +
            "<shared:imageUri>urn:vpro:image:519119</shared:imageUri>\n" +
            "</shared:image>\n" +
            "<shared:image owner=\"BROADCASTER\" type=\"STILL\" highlighted=\"false\" creationDate=\"2016-05-05T14:31:30.314+02:00\" lastModified=\"2016-05-05T14:31:30.336+02:00\" urn=\"urn:vpro:media:image:75670892\" workflow=\"PUBLISHED\">\n" +
            "<shared:title>Liefs Uit..</shared:title>\n" +
            "<shared:description>\n" +
            "Dawit en Hester weten zeker dat ze hun leven samen willen delen, maar de Ethiopische Dawit voelt hij zich ongemakkelijk in de Achterhoek. Kan Dawit wel gelukkig zijn in de witte Achterhoek?\n" +
            "</shared:description>\n" +
            "<shared:imageUri>urn:vpro:image:519120</shared:imageUri>\n" +
            "</shared:image>\n" +
            "<shared:image owner=\"BROADCASTER\" type=\"STILL\" highlighted=\"false\" creationDate=\"2016-05-05T14:31:30.314+02:00\" lastModified=\"2016-05-05T14:31:30.336+02:00\" urn=\"urn:vpro:media:image:75670893\" workflow=\"PUBLISHED\">\n" +
            "<shared:title>Liefs Uit..</shared:title>\n" +
            "<shared:description>\n" +
            "Dawit en Hester weten zeker dat ze hun leven samen willen delen, maar de Ethiopische Dawit voelt hij zich ongemakkelijk in de Achterhoek. Kan Dawit wel gelukkig zijn in de witte Achterhoek?\n" +
            "</shared:description>\n" +
            "<shared:imageUri>urn:vpro:image:519121</shared:imageUri>\n" +
            "</shared:image>\n" +
            "</images>\n" +
            "<segments/>\n" +
            "</program>"), Program.class);
        PredicateTestResult mediaObjectPredicateTestResult = filter.testWithReason(program);
        assertThat(mediaObjectPredicateTestResult.applies()).isTrue();
        JAXB.marshal(mediaObjectPredicateTestResult, LoggerOutputStream.info(log));

    }
}
