package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import nl.vpro.util.Version;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * Created a program 'with everything' then makes an 'update' object of it. Checks the XML, and marshall it back to an update object.
 *
 * Then take that, make a simple mediaobject of it and check _its_ XML.
 *
 *
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class MediaUpdateTest {

    static ProgramUpdate rounded;

    static {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }

    @Test
    public void withEverything1() throws Exception {

        Program withEverything = MediaTestDataBuilder
            .program()
            .withEverything()
            .withFixedDates()
            .build();

        withEverything.getVersion();
        ProgramUpdate update = ProgramUpdate.create(withEverything, OwnerType.BROADCASTER);
        update.setVersion(Version.of(5, 11));
        log.info("{}", update.getVersion());

        rounded = JAXBTestUtil.roundTripAndValidateAndSimilar(update,
                getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"),
                    "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<program type=\"BROADCAST\" avType=\"VIDEO\" embeddable=\"true\" mid=\"VPROWON_20001\" publishStart=\"1970-01-01T01:00:00+01:00\" publishStop=\"2500-01-01T00:00:00+01:00\" urn=\"urn:vpro:media:program:12\" version=\"5.11\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
                    "    <broadcaster>BNN</broadcaster>\n" +
                    "    <broadcaster>AVRO</broadcaster>\n" +
                    "    <portal>3VOOR12_GRONINGEN</portal>\n" +
                    "    <portal>STERREN24</portal>\n" +
                    "    <exclusive>STERREN24</exclusive>\n" +
                    "    <exclusive start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\">3VOOR12_GRONINGEN</exclusive>\n" +
                    "    <region platform=\"INTERNETVOD\">NL</region>\n" +
                    "    <region start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\" platform=\"INTERNETVOD\">BENELUX</region>\n" +
                    "    <region start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\" platform=\"TVVOD\">NL</region>\n" +
                    "    <title type=\"MAIN\">Main title</title>\n" +
                    "    <title type=\"SHORT\">Short title</title>\n" +
                    "    <title type=\"SUB\">Episode title MIS</title>\n" +
                    "    <description type=\"MAIN\">Main description</description>\n" +
                    "    <description type=\"SHORT\">Short description</description>\n" +
                    "    <description type=\"EPISODE\">Episode description MIS</description>\n" +
                    "    <tag>tag1</tag>\n" +
                    "    <tag>tag2</tag>\n" +
                    "    <tag>tag3</tag>\n" +
                    "    <country>GB</country>\n" +
                    "    <country>US</country>\n" +
                    "    <language>nl</language>\n" +
                    "    <language>fr</language>\n" +
                    "    <genre>3.0.1.7.21</genre>\n" +
                    "    <genre>3.0.1.8.25</genre>\n" +
                    "    <intentions>\n" +
                    "        <intention>ACTIVATING</intention>\n" +
                    "        <intention>INFORM_INDEPTH</intention>\n" +
                    "    </intentions>\n" +
                    "    <targetGroups>\n" +
                    "        <targetGroup>ADULTS</targetGroup>\n" +
                    "    </targetGroups>\n" +
                    "    <avAttributes>\n" +
                    "        <bitrate>1000000</bitrate>\n" +
                    "        <avFileFormat>M4V</avFileFormat>\n" +
                    "        <videoAttributes width=\"640\" height=\"320\">\n" +
                    "            <coding>VCODEC</coding>\n" +
                    "        </videoAttributes>\n" +
                    "        <audioAttributes>\n" +
                    "            <channels>2</channels>\n" +
                    "            <coding>ACODEC</coding>\n" +
                    "        </audioAttributes>\n" +
                    "    </avAttributes>\n" +
                    "    <releaseYear>2004</releaseYear>\n" +
                    "    <duration>P0DT2H0M0.000S</duration>\n" +
                    "    <credits>\n" +
                    "        <person role=\"DIRECTOR\">\n" +
                    "            <givenName>Bregtje</givenName>\n" +
                    "            <familyName>van der Haak</familyName>\n" +
                    "        </person>\n" +
                    "        <person role=\"PRESENTER\">\n" +
                    "            <givenName>Hans</givenName>\n" +
                    "            <familyName>Goedkoop</familyName>\n" +
                    "        </person>\n" +
                    "        <person role=\"PRESENTER\">\n" +
                    "            <givenName>Meta</givenName>\n" +
                    "            <familyName>de Vries</familyName>\n" +
                    "        </person>\n" +
                    "        <person role=\"ACTOR\">\n" +
                    "            <givenName>Claire</givenName>\n" +
                    "            <familyName>Holt</familyName>\n" +
                    "        </person>\n" +
                    "    </credits>\n" +
                    "    <memberOf position=\"1\" highlighted=\"false\">VPROWON_20003</memberOf>\n" +
                    "    <ageRating>12</ageRating>\n" +
                    "    <contentRating>ANGST</contentRating>\n" +
                    "    <contentRating>DRUGS_EN_ALCOHOL</contentRating>\n" +
                    "    <email>info@npo.nl</email>\n" +
                    "    <email>programma@avro.nl</email>\n" +
                    "    <website>http://www.omroep.nl/programma/journaal</website>\n" +
                    "    <website>http://tegenlicht.vpro.nl/afleveringen/222555</website>\n" +
                    "    <twitterref>#vpro</twitterref>\n" +
                    "    <twitterref>@twitter</twitterref>\n" +
                    "    <prediction>INTERNETVOD</prediction>\n" +
                    "    <prediction>TVVOD</prediction>\n" +
                    "    <locations>\n" +
                    "        <location urn=\"urn:vpro:media:location:6\">\n" +
                    "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>\n" +
                    "            <avAttributes>\n" +
                    "                <avFileFormat>MP4</avFileFormat>\n" +
                    "            </avAttributes>\n" +
                    "            <offset>P0DT0H13M0.000S</offset>\n" +
                    "            <duration>P0DT0H10M0.000S</duration>\n" +
                    "        </location>\n" +
                    "        <location urn=\"urn:vpro:media:location:7\">\n" +
                    "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf</programUrl>\n" +
                    "            <avAttributes>\n" +
                    "                <avFileFormat>WM</avFileFormat>\n" +
                    "            </avAttributes>\n" +
                    "        </location>\n" +
                    "        <location urn=\"urn:vpro:media:location:8\">\n" +
                    "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf</programUrl>\n" +
                    "            <avAttributes>\n" +
                    "                <avFileFormat>WM</avFileFormat>\n" +
                    "            </avAttributes>\n" +
                    "            <duration>P0DT0H30M33.000S</duration>\n" +
                    "        </location>\n" +
                    "        <location urn=\"urn:vpro:media:location:11\">\n" +
                    "            <programUrl>http://www.vpro.nl/location/1</programUrl>\n" +
                    "            <avAttributes>\n" +
                    "                <avFileFormat>UNKNOWN</avFileFormat>\n" +
                    "            </avAttributes>\n" +
                    "        </location>\n" +
                    "    </locations>\n" +
                    "    <scheduleEvents>\n" +
                    "        <scheduleEvent channel=\"NED3\">\n" +
                    "            <start>1970-01-01T01:00:00.100+01:00</start>\n" +
                    "            <guideDay>1969-12-31</guideDay>\n" +
                    "            <duration>P0DT0H0M0.200S</duration>\n" +
                    "        </scheduleEvent>\n" +
                    "        <scheduleEvent channel=\"NED3\">\n" +
                    "            <start>1970-01-04T01:00:00.300+01:00</start>\n" +
                    "            <guideDay>1970-01-03</guideDay>\n" +
                    "            <duration>P0DT0H0M0.050S</duration>\n" +
                    "        </scheduleEvent>\n" +
                    "        <scheduleEvent channel=\"HOLL\">\n" +
                    "            <start>1970-01-09T01:00:00.350+01:00</start>\n" +
                    "            <guideDay>1970-01-08</guideDay>\n" +
                    "            <duration>P0DT0H0M0.250S</duration>\n" +
                    "        </scheduleEvent>\n" +
                    "        <scheduleEvent channel=\"CONS\">\n" +
                    "            <start>1970-01-11T01:00:00.600+01:00</start>\n" +
                    "            <guideDay>1970-01-10</guideDay>\n" +
                    "            <duration>P0DT0H0M0.200S</duration>\n" +
                    "        </scheduleEvent>\n" +
                    "    </scheduleEvents>\n" +
                    "    <relation type=\"THESAURUS\" broadcaster=\"AVRO\">synoniem</relation>\n" +
                    "    <relation type=\"KOOR\" broadcaster=\"EO\">Ulfts Mannenkoor</relation>\n" +
                    "    <relation type=\"ARTIST\" broadcaster=\"VPRO\">Marco Borsato</relation>\n" +
                    "    <relation type=\"LABEL\" broadcaster=\"VPRO\" uriRef=\"http://www.bluenote.com/\">Blue Note</relation>\n" +
                    "    <images>\n" +
                    "        <image type=\"PICTURE\" urn=\"urn:vpro:media:image:2\" highlighted=\"false\">\n" +
                    "            <title>Eerste plaatje met credits</title>\n" +
                    "            <source>SOURCE</source>\n" +
                    "            <license>PUBLIC_DOMAIN</license>\n" +
                    "            <credits>CREDITS</credits>\n" +
                    "            <urn>urn:vpro:image:11234</urn>\n" +
                    "        </image>\n" +
                    "        <image type=\"PICTURE\" urn=\"urn:vpro:media:image:3\" highlighted=\"false\">\n" +
                    "            <title>Tweede plaatje met credits</title>\n" +
                    "            <source>SOURCE</source>\n" +
                    "            <license>PUBLIC_DOMAIN</license>\n" +
                    "            <credits>CREDITS</credits>\n" +
                    "            <urn>urn:vpro:image:15678</urn>\n" +
                    "        </image>\n" +
                    "    </images>\n" +
                    "    <episodeOf position=\"1\" highlighted=\"false\">VPROWON_30002</episodeOf>\n" +
                    "    <segments>\n" +
                    "        <segment midRef=\"VPROWON_20001\" avType=\"VIDEO\" embeddable=\"true\" mid=\"VPROWON_12345_1\" publishStart=\"1970-01-01T01:00:00+01:00\" publishStop=\"2500-01-01T00:00:00+01:00\" urn=\"urn:vpro:media:segment:12\">\n" +
                    "            <broadcaster>BNN</broadcaster>\n" +
                    "            <broadcaster>AVRO</broadcaster>\n" +
                    "            <portal>3VOOR12_GRONINGEN</portal>\n" +
                    "            <portal>STERREN24</portal>\n" +
                    "            <exclusive>STERREN24</exclusive>\n" +
                    "            <exclusive start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\">3VOOR12_GRONINGEN</exclusive>\n" +
                    "            <region platform=\"INTERNETVOD\">NL</region>\n" +
                    "            <region start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\" platform=\"INTERNETVOD\">BENELUX</region>\n" +
                    "            <region start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\" platform=\"TVVOD\">NL</region>\n" +
                    "            <title type=\"MAIN\">Main title</title>\n" +
                    "            <title type=\"SHORT\">Short title</title>\n" +
                    "            <title type=\"SUB\">Episode title MIS</title>\n" +
                    "            <description type=\"MAIN\">Main description</description>\n" +
                    "            <description type=\"SHORT\">Short description</description>\n" +
                    "            <description type=\"EPISODE\">Episode description MIS</description>\n" +
                    "            <tag>tag1</tag>\n" +
                    "            <tag>tag2</tag>\n" +
                    "            <tag>tag3</tag>\n" +
                    "            <country>GB</country>\n" +
                    "            <country>US</country>\n" +
                    "            <language>nl</language>\n" +
                    "            <language>fr</language>\n" +
                    "            <genre>3.0.1.7.21</genre>\n" +
                    "            <genre>3.0.1.8.25</genre>\n" +
                    "            <intentions>\n" +
                    "                <intention>ACTIVATING</intention>\n" +
                    "                <intention>INFORM_INDEPTH</intention>\n" +
                    "            </intentions>\n" +
                    "            <targetGroups>\n" +
                    "                <targetGroup>ADULTS</targetGroup>\n" +
                    "            </targetGroups>\n" +
                    "            <avAttributes>\n" +
                    "                <bitrate>1000000</bitrate>\n" +
                    "                <avFileFormat>M4V</avFileFormat>\n" +
                    "                <videoAttributes width=\"640\" height=\"320\">\n" +
                    "                    <coding>VCODEC</coding>\n" +
                    "                </videoAttributes>\n" +
                    "                <audioAttributes>\n" +
                    "                    <channels>2</channels>\n" +
                    "                    <coding>ACODEC</coding>\n" +
                    "                </audioAttributes>\n" +
                    "            </avAttributes>\n" +
                    "            <releaseYear>2004</releaseYear>\n" +
                    "            <duration>P0DT0H1M40.000S</duration>\n" +
                    "            <credits>\n" +
                    "                <person role=\"DIRECTOR\">\n" +
                    "                    <givenName>Bregtje</givenName>\n" +
                    "                    <familyName>van der Haak</familyName>\n" +
                    "                </person>\n" +
                    "                <person role=\"PRESENTER\">\n" +
                    "                    <givenName>Hans</givenName>\n" +
                    "                    <familyName>Goedkoop</familyName>\n" +
                    "                </person>\n" +
                    "                <person role=\"PRESENTER\">\n" +
                    "                    <givenName>Meta</givenName>\n" +
                    "                    <familyName>de Vries</familyName>\n" +
                    "                </person>\n" +
                    "                <person role=\"ACTOR\">\n" +
                    "                    <givenName>Claire</givenName>\n" +
                    "                    <familyName>Holt</familyName>\n" +
                    "                </person>\n" +
                    "            </credits>\n" +
                    "            <memberOf position=\"1\" highlighted=\"false\">VPROWON_20008</memberOf>\n" +
                    "            <ageRating>12</ageRating>\n" +
                    "            <contentRating>ANGST</contentRating>\n" +
                    "            <contentRating>DRUGS_EN_ALCOHOL</contentRating>\n" +
                    "            <email>info@npo.nl</email>\n" +
                    "            <email>programma@avro.nl</email>\n" +
                    "            <website>http://www.omroep.nl/programma/journaal</website>\n" +
                    "            <website>http://tegenlicht.vpro.nl/afleveringen/222555</website>\n" +
                    "            <twitterref>#vpro</twitterref>\n" +
                    "            <twitterref>@twitter</twitterref>\n" +
                    "            <prediction>INTERNETVOD</prediction>\n" +
                    "            <locations>\n" +
                    "                <location urn=\"urn:vpro:media:location:6\">\n" +
                    "                    <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>\n" +
                    "                    <avAttributes>\n" +
                    "                        <avFileFormat>MP4</avFileFormat>\n" +
                    "                    </avAttributes>\n" +
                    "                    <offset>P0DT0H13M0.000S</offset>\n" +
                    "                    <duration>P0DT0H10M0.000S</duration>\n" +
                    "                </location>\n" +
                    "                <location urn=\"urn:vpro:media:location:7\">\n" +
                    "                    <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf</programUrl>\n" +
                    "                    <avAttributes>\n" +
                    "                        <avFileFormat>WM</avFileFormat>\n" +
                    "                    </avAttributes>\n" +
                    "                </location>\n" +
                    "                <location urn=\"urn:vpro:media:location:8\">\n" +
                    "                    <programUrl>http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf</programUrl>\n" +
                    "                    <avAttributes>\n" +
                    "                        <avFileFormat>WM</avFileFormat>\n" +
                    "                    </avAttributes>\n" +
                    "                    <duration>P0DT0H30M33.000S</duration>\n" +
                    "                </location>\n" +
                    "                <location urn=\"urn:vpro:media:location:11\">\n" +
                    "                    <programUrl>http://www.vpro.nl/location/1</programUrl>\n" +
                    "                    <avAttributes>\n" +
                    "                        <avFileFormat>UNKNOWN</avFileFormat>\n" +
                    "                    </avAttributes>\n" +
                    "                </location>\n" +
                    "            </locations>\n" +
                    "            <relation type=\"THESAURUS\" broadcaster=\"AVRO\">synoniem</relation>\n" +
                    "            <relation type=\"KOOR\" broadcaster=\"EO\">Ulfts Mannenkoor</relation>\n" +
                    "            <relation type=\"ARTIST\" broadcaster=\"VPRO\">Marco Borsato</relation>\n" +
                    "            <relation type=\"LABEL\" broadcaster=\"VPRO\" uriRef=\"http://www.bluenote.com/\">Blue Note</relation>\n" +
                    "            <images>\n" +
                    "                <image type=\"PICTURE\" urn=\"urn:vpro:media:image:2\" highlighted=\"false\">\n" +
                    "                    <title>Eerste plaatje met credits</title>\n" +
                    "                    <source>SOURCE</source>\n" +
                    "                    <license>PUBLIC_DOMAIN</license>\n" +
                    "                    <credits>CREDITS</credits>\n" +
                    "                    <urn>urn:vpro:image:11234</urn>\n" +
                    "                </image>\n" +
                    "                <image type=\"PICTURE\" urn=\"urn:vpro:media:image:3\" highlighted=\"false\">\n" +
                    "                    <title>Tweede plaatje met credits</title>\n" +
                    "                    <source>SOURCE</source>\n" +
                    "                    <license>PUBLIC_DOMAIN</license>\n" +
                    "                    <credits>CREDITS</credits>\n" +
                    "                    <urn>urn:vpro:image:15678</urn>\n" +
                    "                </image>\n" +
                    "            </images>\n" +
                    "            <start>P0DT0H0M0.000S</start>\n" +
                    "        </segment>\n" +
                    "    </segments>\n" +
                    "</program>\n");


    }

    @Test
    public void withEverything2() throws Exception {
        MediaObject fetched = rounded.fetch(OwnerType.BROADCASTER);
        JAXBTestUtil.roundTripAndSimilar(fetched, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program type=\"BROADCAST\" avType=\"VIDEO\" embeddable=\"true\" mid=\"VPROWON_20001\" sortDate=\"1970-01-11T01:00:00.600+01:00\" workflow=\"FOR PUBLICATION\" publishStart=\"1970-01-01T01:00:00+01:00\" publishStop=\"2500-01-01T00:00:00+01:00\" urn=\"urn:vpro:media:program:12\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <broadcaster id=\"BNN\">BNN</broadcaster>\n" +
            "    <broadcaster id=\"AVRO\">AVRO</broadcaster>\n" +
            "    <portal id=\"3VOOR12_GRONINGEN\"/>\n" +
            "    <portal id=\"STERREN24\"/>\n" +
            "    <exclusive portalId=\"STERREN24\"/>\n" +
            "    <exclusive portalId=\"3VOOR12_GRONINGEN\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
            "    <region regionId=\"NL\" platform=\"INTERNETVOD\"/>\n" +
            "    <region regionId=\"BENELUX\" platform=\"INTERNETVOD\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
            "    <region regionId=\"NL\" platform=\"TVVOD\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
            "    <title owner=\"BROADCASTER\" type=\"MAIN\">Main title</title>\n" +
            "    <title owner=\"BROADCASTER\" type=\"SHORT\">Short title</title>\n" +
            "    <title owner=\"BROADCASTER\" type=\"SUB\">Episode title MIS</title>\n" +
            "    <description owner=\"BROADCASTER\" type=\"MAIN\">Main description</description>\n" +
            "    <description owner=\"BROADCASTER\" type=\"SHORT\">Short description</description>\n" +
            "    <description owner=\"BROADCASTER\" type=\"EPISODE\">Episode description MIS</description>\n" +
            "    <genre id=\"3.0.1.7.21\">\n" +
            "        <term>Informatief</term>\n" +
            "        <term>Nieuws/actualiteiten</term>\n" +
            "    </genre>\n" +
            "    <genre id=\"3.0.1.8.25\">\n" +
            "        <term>Documentaire</term>\n" +
            "        <term>Natuur</term>\n" +
            "    </genre>\n" +
            "    <tag>tag1</tag>\n" +
            "    <tag>tag2</tag>\n" +
            "    <tag>tag3</tag>\n" +
            "    <intentions owner=\"BROADCASTER\">\n" +
            "        <intention>ACTIVATING</intention>\n" +
            "        <intention>INFORM_INDEPTH</intention>\n" +
            "    </intentions>\n" +
            "    <targetGroups owner=\"BROADCASTER\">\n" +
            "        <targetGroup>ADULTS</targetGroup>\n" +
            "    </targetGroups>\n" +
            "    <country code=\"GB\">Verenigd Koninkrijk</country>\n" +
            "    <country code=\"US\">Verenigde Staten</country>\n" +
            "    <language code=\"nl\">Nederlands</language>\n" +
            "    <language code=\"fr\">Frans</language>\n" +
            "    <avAttributes>\n" +
            "        <bitrate>1000000</bitrate>\n" +
            "        <avFileFormat>M4V</avFileFormat>\n" +
            "        <videoAttributes width=\"640\" heigth=\"320\">\n" +
            "            <videoCoding>VCODEC</videoCoding>\n" +
            "        </videoAttributes>\n" +
            "        <audioAttributes>\n" +
            "            <numberOfChannels>2</numberOfChannels>\n" +
            "            <audioCoding>ACODEC</audioCoding>\n" +
            "        </audioAttributes>\n" +
            "    </avAttributes>\n" +
            "    <releaseYear>2004</releaseYear>\n" +
            "    <duration>P0DT2H0M0.000S</duration>\n" +
            "    <credits>\n" +
            "        <person role=\"DIRECTOR\">\n" +
            "            <givenName>Bregtje</givenName>\n" +
            "            <familyName>van der Haak</familyName>\n" +
            "        </person>\n" +
            "        <person role=\"PRESENTER\">\n" +
            "            <givenName>Hans</givenName>\n" +
            "            <familyName>Goedkoop</familyName>\n" +
            "        </person>\n" +
            "        <person role=\"PRESENTER\">\n" +
            "            <givenName>Meta</givenName>\n" +
            "            <familyName>de Vries</familyName>\n" +
            "        </person>\n" +
            "        <person role=\"ACTOR\">\n" +
            "            <givenName>Claire</givenName>\n" +
            "            <familyName>Holt</familyName>\n" +
            "        </person>\n" +
            "    </credits>\n" +
            "    <descendantOf midRef=\"VPROWON_20003\"/>\n" +
            "    <memberOf highlighted=\"false\" midRef=\"VPROWON_20003\" index=\"1\"/>\n" +
            "    <ageRating>12</ageRating>\n" +
            "    <contentRating>ANGST</contentRating>\n" +
            "    <contentRating>DRUGS_EN_ALCOHOL</contentRating>\n" +
            "    <email>info@npo.nl</email>\n" +
            "    <email>programma@avro.nl</email>\n" +
            "    <website>http://www.omroep.nl/programma/journaal</website>\n" +
            "    <website>http://tegenlicht.vpro.nl/afleveringen/222555</website>\n" +
            "    <twitter type=\"HASHTAG\">#vpro</twitter>\n" +
            "    <twitter type=\"ACCOUNT\">@twitter</twitter>\n" +
            "    <prediction state=\"ANNOUNCED\">INTERNETVOD</prediction>\n" +
            "    <prediction state=\"ANNOUNCED\">TVVOD</prediction>\n" +
            "    <locations>\n" +
            "        <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:location:6\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>MP4</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "            <offset>P0DT0H13M0.000S</offset>\n" +
            "            <duration>P0DT0H10M0.000S</duration>\n" +
            "        </location>\n" +
            "        <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:location:7\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>WM</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "        </location>\n" +
            "        <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:location:8\">\n" +
            "            <programUrl>http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>WM</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "            <duration>P0DT0H30M33.000S</duration>\n" +
            "        </location>\n" +
            "        <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:location:11\">\n" +
            "            <programUrl>http://www.vpro.nl/location/1</programUrl>\n" +
            "            <avAttributes>\n" +
            "                <avFileFormat>UNKNOWN</avFileFormat>\n" +
            "            </avAttributes>\n" +
            "        </location>\n" +
            "    </locations>\n" +
            "    <relation broadcaster=\"AVRO\" type=\"THESAURUS\">synoniem</relation>\n" +
            "    <relation broadcaster=\"EO\" type=\"KOOR\">Ulfts Mannenkoor</relation>\n" +
            "    <relation broadcaster=\"VPRO\" type=\"ARTIST\">Marco Borsato</relation>\n" +
            "    <relation uriRef=\"http://www.bluenote.com/\" broadcaster=\"VPRO\" type=\"LABEL\">Blue Note</relation>\n" +
            "    <images>\n" +
            "        <shared:image owner=\"BROADCASTER\" type=\"PICTURE\" highlighted=\"false\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:image:2\">\n" +
            "            <shared:title>Eerste plaatje met credits</shared:title>\n" +
            "            <shared:imageUri>urn:vpro:image:11234</shared:imageUri>\n" +
            "            <shared:credits>CREDITS</shared:credits>\n" +
            "            <shared:source>SOURCE</shared:source>\n" +
            "            <shared:license>PUBLIC_DOMAIN</shared:license>\n" +
            "        </shared:image>\n" +
            "        <shared:image owner=\"BROADCASTER\" type=\"PICTURE\" highlighted=\"false\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:image:3\">\n" +
            "            <shared:title>Tweede plaatje met credits</shared:title>\n" +
            "            <shared:imageUri>urn:vpro:image:15678</shared:imageUri>\n" +
            "            <shared:credits>CREDITS</shared:credits>\n" +
            "            <shared:source>SOURCE</shared:source>\n" +
            "            <shared:license>PUBLIC_DOMAIN</shared:license>\n" +
            "        </shared:image>\n" +
            "    </images>\n" +
              "    <scheduleEvents>\n" +
            "        <scheduleEvent channel=\"NED3\" midRef=\"VPROWON_20001\" urnRef=\"urn:vpro:media:program:12\">\n" +
            "            <guideDay>1969-12-31+01:00</guideDay>\n" +
            "            <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "            <duration>P0DT0H0M0.200S</duration>\n" +
            "            <poProgID>VPROWON_20001</poProgID>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"NED3\" midRef=\"VPROWON_20001\" urnRef=\"urn:vpro:media:program:12\">\n" +
            "            <guideDay>1970-01-03+01:00</guideDay>\n" +
            "            <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "            <duration>P0DT0H0M0.050S</duration>\n" +
            "            <poProgID>VPROWON_20001</poProgID>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"HOLL\" midRef=\"VPROWON_20001\" urnRef=\"urn:vpro:media:program:12\">\n" +
            "            <guideDay>1970-01-08+01:00</guideDay>\n" +
            "            <start>1970-01-09T01:00:00.350+01:00</start>\n" +
            "            <duration>P0DT0H0M0.250S</duration>\n" +
            "            <poProgID>VPROWON_20001</poProgID>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"CONS\" midRef=\"VPROWON_20001\" urnRef=\"urn:vpro:media:program:12\">\n" +
            "            <guideDay>1970-01-10+01:00</guideDay>\n" +
            "            <start>1970-01-11T01:00:00.600+01:00</start>\n" +
            "            <duration>P0DT0H0M0.200S</duration>\n" +
            "            <poProgID>VPROWON_20001</poProgID>\n" +
            "        </scheduleEvent>\n" +
            "    </scheduleEvents>\n" +
            "    <segments>\n" +
            "        <segment midRef=\"VPROWON_20001\" type=\"SEGMENT\" urnRef=\"urn:vpro:media:program:12\" avType=\"VIDEO\" embeddable=\"true\" mid=\"VPROWON_12345_1\" sortDate=\"1970-01-11T01:00:00.600+01:00\" workflow=\"FOR PUBLICATION\" publishStart=\"1970-01-01T01:00:00+01:00\" publishStop=\"2500-01-01T00:00:00+01:00\" urn=\"urn:vpro:media:segment:12\">\n" +
            "            <broadcaster id=\"BNN\">BNN</broadcaster>\n" +
            "            <broadcaster id=\"AVRO\">AVRO</broadcaster>\n" +
            "            <portal id=\"3VOOR12_GRONINGEN\"/>\n" +
            "            <portal id=\"STERREN24\"/>\n" +
            "            <exclusive portalId=\"STERREN24\"/>\n" +
            "            <exclusive portalId=\"3VOOR12_GRONINGEN\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
            "            <region regionId=\"NL\" platform=\"INTERNETVOD\"/>\n" +
            "            <region regionId=\"BENELUX\" platform=\"INTERNETVOD\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
            "            <region regionId=\"NL\" platform=\"TVVOD\" start=\"1970-01-01T01:00:00+01:00\" stop=\"1970-01-01T01:01:40+01:00\"/>\n" +
            "            <title owner=\"BROADCASTER\" type=\"MAIN\">Main title</title>\n" +
            "            <title owner=\"BROADCASTER\" type=\"SHORT\">Short title</title>\n" +
            "            <title owner=\"BROADCASTER\" type=\"SUB\">Episode title MIS</title>\n" +
            "            <description owner=\"BROADCASTER\" type=\"MAIN\">Main description</description>\n" +
            "            <description owner=\"BROADCASTER\" type=\"SHORT\">Short description</description>\n" +
            "            <description owner=\"BROADCASTER\" type=\"EPISODE\">Episode description MIS</description>\n" +
            "            <genre id=\"3.0.1.7.21\">\n" +
            "                <term>Informatief</term>\n" +
            "                <term>Nieuws/actualiteiten</term>\n" +
            "            </genre>\n" +
            "            <genre id=\"3.0.1.8.25\">\n" +
            "                <term>Documentaire</term>\n" +
            "                <term>Natuur</term>\n" +
            "            </genre>\n" +
            "            <tag>tag1</tag>\n" +
            "            <tag>tag2</tag>\n" +
            "            <tag>tag3</tag>\n" +
            "            <intentions owner=\"BROADCASTER\">\n" +
            "                <intention>ACTIVATING</intention>\n" +
            "                <intention>INFORM_INDEPTH</intention>\n" +
            "            </intentions>\n" +
            "            <targetGroups owner=\"BROADCASTER\">" +
            "                <targetGroup>ADULTS</targetGroup>\n" +
            "            </targetGroups>\n" +
            "            <country code=\"GB\">Verenigd Koninkrijk</country>\n" +
            "            <country code=\"US\">Verenigde Staten</country>\n" +
            "            <language code=\"nl\">Nederlands</language>\n" +
            "            <language code=\"fr\">Frans</language>\n" +
            "            <avAttributes>\n" +
            "                <bitrate>1000000</bitrate>\n" +
            "                <avFileFormat>M4V</avFileFormat>\n" +
            "                <videoAttributes width=\"640\" heigth=\"320\">\n" +
            "                    <videoCoding>VCODEC</videoCoding>\n" +
            "                </videoAttributes>\n" +
            "                <audioAttributes>\n" +
            "                    <numberOfChannels>2</numberOfChannels>\n" +
            "                    <audioCoding>ACODEC</audioCoding>\n" +
            "                </audioAttributes>\n" +
            "            </avAttributes>\n" +
            "            <releaseYear>2004</releaseYear>\n" +
            "            <duration>P0DT0H1M40.000S</duration>\n" +
            "            <credits>\n" +
            "                <person role=\"DIRECTOR\">\n" +
            "                    <givenName>Bregtje</givenName>\n" +
            "                    <familyName>van der Haak</familyName>\n" +
            "                </person>\n" +
            "                <person role=\"PRESENTER\">\n" +
            "                    <givenName>Hans</givenName>\n" +
            "                    <familyName>Goedkoop</familyName>\n" +
            "                </person>\n" +
            "                <person role=\"PRESENTER\">\n" +
            "                    <givenName>Meta</givenName>\n" +
            "                    <familyName>de Vries</familyName>\n" +
            "                </person>\n" +
            "                <person role=\"ACTOR\">\n" +
            "                    <givenName>Claire</givenName>\n" +
            "                    <familyName>Holt</familyName>\n" +
            "                </person>\n" +
            "            </credits>\n" +
            "            <descendantOf urnRef=\"urn:vpro:media:program:12\" midRef=\"VPROWON_20001\" type=\"BROADCAST\"/>\n" +
            "            <descendantOf midRef=\"VPROWON_20008\"/>\n" +
            "            <memberOf highlighted=\"false\" midRef=\"VPROWON_20008\" index=\"1\"/>\n" +
            "            <ageRating>12</ageRating>\n" +
            "            <contentRating>ANGST</contentRating>\n" +
            "            <contentRating>DRUGS_EN_ALCOHOL</contentRating>\n" +
            "            <email>info@npo.nl</email>\n" +
            "            <email>programma@avro.nl</email>\n" +
            "            <website>http://www.omroep.nl/programma/journaal</website>\n" +
            "            <website>http://tegenlicht.vpro.nl/afleveringen/222555</website>\n" +
            "            <twitter type=\"HASHTAG\">#vpro</twitter>\n" +
            "            <twitter type=\"ACCOUNT\">@twitter</twitter>\n" +
            "            <prediction state=\"ANNOUNCED\">INTERNETVOD</prediction>\n" +
            "            <locations>\n" +
            "                <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:location:6\">\n" +
            "                    <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1132492/bb.20090317.m4v</programUrl>\n" +
            "                    <avAttributes>\n" +
            "                        <avFileFormat>MP4</avFileFormat>\n" +
            "                    </avAttributes>\n" +
            "                    <offset>P0DT0H13M0.000S</offset>\n" +
            "                    <duration>P0DT0H10M0.000S</duration>\n" +
            "                </location>\n" +
            "                <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:location:7\">\n" +
            "                    <programUrl>http://cgi.omroep.nl/legacy/nebo?/ceres/1/vpro/rest/2009/VPRO_1135479/sb.20091106.asf</programUrl>\n" +
            "                    <avAttributes>\n" +
            "                        <avFileFormat>WM</avFileFormat>\n" +
            "                    </avAttributes>\n" +
            "                </location>\n" +
            "                <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:location:8\">\n" +
            "                    <programUrl>http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf</programUrl>\n" +
            "                    <avAttributes>\n" +
            "                        <avFileFormat>WM</avFileFormat>\n" +
            "                    </avAttributes>\n" +
            "                    <duration>P0DT0H30M33.000S</duration>\n" +
            "                </location>\n" +
            "                <location owner=\"BROADCASTER\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:location:11\">\n" +
            "                    <programUrl>http://www.vpro.nl/location/1</programUrl>\n" +
            "                    <avAttributes>\n" +
            "                        <avFileFormat>UNKNOWN</avFileFormat>\n" +
            "                    </avAttributes>\n" +
            "                </location>\n" +
            "            </locations>\n" +
            "            <relation broadcaster=\"AVRO\" type=\"THESAURUS\">synoniem</relation>\n" +
            "            <relation broadcaster=\"EO\" type=\"KOOR\">Ulfts Mannenkoor</relation>\n" +
            "            <relation broadcaster=\"VPRO\" type=\"ARTIST\">Marco Borsato</relation>\n" +
            "            <relation uriRef=\"http://www.bluenote.com/\" broadcaster=\"VPRO\" type=\"LABEL\">Blue Note</relation>\n" +
            "            <images>\n" +
            "                <shared:image owner=\"BROADCASTER\" type=\"PICTURE\" highlighted=\"false\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:image:2\">\n" +
            "                    <shared:title>Eerste plaatje met credits</shared:title>\n" +
            "                    <shared:imageUri>urn:vpro:image:11234</shared:imageUri>\n" +
            "                    <shared:credits>CREDITS</shared:credits>\n" +
            "                    <shared:source>SOURCE</shared:source>\n" +
            "                    <shared:license>PUBLIC_DOMAIN</shared:license>\n" +
            "                </shared:image>\n" +
            "                <shared:image owner=\"BROADCASTER\" type=\"PICTURE\" highlighted=\"false\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:image:3\">\n" +
            "                    <shared:title>Tweede plaatje met credits</shared:title>\n" +
            "                    <shared:imageUri>urn:vpro:image:15678</shared:imageUri>\n" +
            "                    <shared:credits>CREDITS</shared:credits>\n" +
            "                    <shared:source>SOURCE</shared:source>\n" +
            "                    <shared:license>PUBLIC_DOMAIN</shared:license>\n" +
            "                </shared:image>\n" +
            "            </images>\n" +
            "            <start>P0DT0H0M0.000S</start>\n" +
            "        </segment>\n" +
            "    </segments>\n" +
            "</program>");
    }



    @Test
    public void generateCompleteProgramUpdateXMLandVerifyAgainstXSD() throws Exception {
        StringWriter writer = new StringWriter();

        Program program = MediaTestDataBuilder.program().withEverything().build();
        ProgramUpdate programUpdate = ProgramUpdate.create(program);
        //JAXB.marshal(programUpdate, System.out); //log
        JAXB.marshal(programUpdate, writer);

        StreamSource xml = new StreamSource(new StringReader(writer.toString()));

        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        log.info("{}", getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd"));
        Schema schema = schemaFactory.newSchema(
                getClass().getResource("/nl/vpro/domain/media/update/vproMediaUpdate.xsd")
        );
        Validator validator = schema.newValidator();
        validator.validate(xml);
    }

    @Test
    public void testWithoutIntentions() {

        String withoutIntentions = "<program xmlns=\"urn:vpro:media:update:2009\" xmlns:media=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" embeddable=\"true\">\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "</program>";

        ProgramUpdate update = JAXB.unmarshal(new StringReader(withoutIntentions), ProgramUpdate.class);

        assertThat(update.getIntentions()).isNull();


    }

    @Test
    public void testWithIntentions() {

        String withoutIntentions = "<program xmlns=\"urn:vpro:media:update:2009\" xmlns:media=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" embeddable=\"true\">\n" +
            "    <locations/>\n" +
            "    <scheduleEvents/>\n" +
            "    <images/>\n" +
            "    <segments/>\n" +
            "    <intentions />\n" +
            "</program>";

        ProgramUpdate update = JAXB.unmarshal(new StringReader(withoutIntentions), ProgramUpdate.class);

        assertThat(update.getIntentions()).isEmpty();


    }


    @Test
    public void testWithIntentionsOfOtherOwner() {
        ProgramUpdate update  = ProgramUpdate.create(
            MediaBuilder.program().intentions(Intentions.builder().owner(OwnerType.MIS).value(IntentionType.INFORM_INDEPTH).build()).build(),
            OwnerType.BROADCASTER
        );

        assertThat(update.getIntentions()).containsExactly(IntentionType.INFORM_INDEPTH);


    }

    @Test
    public void testWithIntentionsOfOwner() {
        ProgramUpdate update  = ProgramUpdate.create(
            MediaBuilder.program().intentions(
                Intentions.builder()
                    .owner(OwnerType.MIS).value(IntentionType.INFORM_INDEPTH).build(),
                 Intentions.builder()
                    .owner(OwnerType.BROADCASTER).value(IntentionType.ENTERTAINMENT_INFORMATIVE).build()

            ).build(),
            OwnerType.MIS
        );

        assertThat(update.getIntentions()).containsExactly(IntentionType.INFORM_INDEPTH);


    }
}
