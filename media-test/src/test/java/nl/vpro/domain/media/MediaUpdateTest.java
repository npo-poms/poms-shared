package nl.vpro.domain.media;

import org.junit.Test;

import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
public class MediaUpdateTest {

    @Test

    public void withEverything() throws Exception {

        // TODO. Still orienting, it may be that the xml is not yet absolutely correct.
        Program withEverything = MediaTestDataBuilder
            .program()
            .withEverything()
            .withFixedDates()
            .build();

        ProgramUpdate update = ProgramUpdate.create(withEverything, OwnerType.BROADCASTER);

        ProgramUpdate rounded = JAXBTestUtil.roundTripAndSimilar(update, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program type=\"BROADCAST\" avType=\"VIDEO\" embeddable=\"true\" mid=\"VPROWON_20001\" publishStart=\"1970-01-01T01:00:00+01:00\" publishStop=\"2500-01-01T00:00:00+01:00\" urn=\"urn:vpro:media:program:12\" xmlns=\"urn:vpro:media:update:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:media=\"urn:vpro:media:2009\">\n" +
            "    <broadcaster>BNN</broadcaster>\n" +
            "    <broadcaster>AVRO</broadcaster>\n" +
            "    <title type=\"MAIN\">Main title</title>\n" +
            "    <title type=\"SHORT\">Short title</title>\n" +
            "    <title type=\"SUB\">Episode title MIS</title>\n" +
            "    <description type=\"MAIN\">Main description</description>\n" +
            "    <description type=\"SHORT\">Short description</description>\n" +
            "    <description type=\"EPISODE\">Episode description MIS</description>\n" +
            "    <country>GB</country>\n" +
            "    <country>US</country>\n" +
            "    <language>nl</language>\n" +
            "    <language>fr</language>\n" +
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
            "    <duration>P0DT2H0M0.000S</duration>\n" +
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
            "            <duration>P0DT0H0M0.200S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"NED3\">\n" +
            "            <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "            <duration>P0DT0H0M0.050S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"HOLL\">\n" +
            "            <start>1970-01-09T01:00:00.350+01:00</start>\n" +
            "            <duration>P0DT0H0M0.250S</duration>\n" +
            "        </scheduleEvent>\n" +
            "        <scheduleEvent channel=\"CONS\">\n" +
            "            <start>1970-01-11T01:00:00.600+01:00</start>\n" +
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
            "            <title type=\"MAIN\">Main title</title>\n" +
            "            <title type=\"SHORT\">Short title</title>\n" +
            "            <title type=\"SUB\">Episode title MIS</title>\n" +
            "            <description type=\"MAIN\">Main description</description>\n" +
            "            <description type=\"SHORT\">Short description</description>\n" +
            "            <description type=\"EPISODE\">Episode description MIS</description>\n" +
            "            <country>GB</country>\n" +
            "            <country>US</country>\n" +
            "            <language>nl</language>\n" +
            "            <language>fr</language>\n" +
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
            "            <duration>P0DT0H1M40.000S</duration>\n" +
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
            "            <scheduleEvents>\n" +
            "                <scheduleEvent channel=\"NED3\">\n" +
            "                    <start>1970-01-01T01:00:00.100+01:00</start>\n" +
            "                    <duration>P0DT0H0M0.200S</duration>\n" +
            "                </scheduleEvent>\n" +
            "                <scheduleEvent channel=\"NED3\">\n" +
            "                    <start>1970-01-04T01:00:00.300+01:00</start>\n" +
            "                    <duration>P0DT0H0M0.050S</duration>\n" +
            "                </scheduleEvent>\n" +
            "                <scheduleEvent channel=\"HOLL\">\n" +
            "                    <start>1970-01-09T01:00:00.350+01:00</start>\n" +
            "                    <duration>P0DT0H0M0.250S</duration>\n" +
            "                </scheduleEvent>\n" +
            "                <scheduleEvent channel=\"CONS\">\n" +
            "                    <start>1970-01-11T01:00:00.600+01:00</start>\n" +
            "                    <duration>P0DT0H0M0.200S</duration>\n" +
            "                </scheduleEvent>\n" +
            "            </scheduleEvents>\n" +
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

        JAXBTestUtil.roundTripAndSimilar(rounded.fetch(OwnerType.BROADCASTER), "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<program type=\"BROADCAST\" avType=\"VIDEO\" embeddable=\"true\" mid=\"VPROWON_20001\" sortDate=\"1970-01-01T01:00:00+01:00\" workflow=\"FOR PUBLICATION\" publishStart=\"1970-01-01T01:00:00+01:00\" publishStop=\"2500-01-01T00:00:00+01:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
            "    <broadcaster id=\"BNN\">BNN</broadcaster>\n" +
            "    <broadcaster id=\"AVRO\">AVRO</broadcaster>\n" +
            "    <title owner=\"BROADCASTER\" type=\"MAIN\">Main title</title>\n" +
            "    <title owner=\"BROADCASTER\" type=\"SHORT\">Short title</title>\n" +
            "    <title owner=\"BROADCASTER\" type=\"SUB\">Episode title MIS</title>\n" +
            "    <description owner=\"BROADCASTER\" type=\"MAIN\">Main description</description>\n" +
            "    <description owner=\"BROADCASTER\" type=\"SHORT\">Short description</description>\n" +
            "    <description owner=\"BROADCASTER\" type=\"EPISODE\">Episode description MIS</description>\n" +
            "    <duration>P0DT2H0M0.000S</duration>\n" +
            "    <credits/>\n" +
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
            "    <scheduleEvents/>\n" +
            "    <relation broadcaster=\"AVRO\" type=\"THESAURUS\">synoniem</relation>\n" +
            "    <relation broadcaster=\"EO\" type=\"KOOR\">Ulfts Mannenkoor</relation>\n" +
            "    <relation broadcaster=\"VPRO\" type=\"ARTIST\">Marco Borsato</relation>\n" +
            "    <relation uriRef=\"http://www.bluenote.com/\" broadcaster=\"VPRO\" type=\"LABEL\">Blue Note</relation>\n" +
            "    <images>\n" +
            "        <shared:image owner=\"BROADCASTER\" type=\"PICTURE\" highlighted=\"false\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:image:2\">\n" +
            "            <shared:title>Eerste plaatje met credits</shared:title>\n" +
            "            <shared:credits>CREDITS</shared:credits>\n" +
            "            <shared:source>SOURCE</shared:source>\n" +
            "            <shared:license>PUBLIC_DOMAIN</shared:license>\n" +
            "        </shared:image>\n" +
            "        <shared:image owner=\"BROADCASTER\" type=\"PICTURE\" highlighted=\"false\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:image:3\">\n" +
            "            <shared:title>Tweede plaatje met credits</shared:title>\n" +
            "            <shared:credits>CREDITS</shared:credits>\n" +
            "            <shared:source>SOURCE</shared:source>\n" +
            "            <shared:license>PUBLIC_DOMAIN</shared:license>\n" +
            "        </shared:image>\n" +
            "    </images>\n" +
            "    <segments>\n" +
            "        <segment type=\"SEGMENT\" avType=\"VIDEO\" embeddable=\"true\" mid=\"VPROWON_12345_1\" sortDate=\"1970-01-01T01:00:00+01:00\" workflow=\"FOR PUBLICATION\" publishStart=\"1970-01-01T01:00:00+01:00\" publishStop=\"2500-01-01T00:00:00+01:00\">\n" +
            "            <broadcaster id=\"BNN\">BNN</broadcaster>\n" +
            "            <broadcaster id=\"AVRO\">AVRO</broadcaster>\n" +
            "            <title owner=\"BROADCASTER\" type=\"MAIN\">Main title</title>\n" +
            "            <title owner=\"BROADCASTER\" type=\"SHORT\">Short title</title>\n" +
            "            <title owner=\"BROADCASTER\" type=\"SUB\">Episode title MIS</title>\n" +
            "            <description owner=\"BROADCASTER\" type=\"MAIN\">Main description</description>\n" +
            "            <description owner=\"BROADCASTER\" type=\"SHORT\">Short description</description>\n" +
            "            <description owner=\"BROADCASTER\" type=\"EPISODE\">Episode description MIS</description>\n" +
            "            <duration>P0DT0H1M40.000S</duration>\n" +
            "            <credits/>\n" +
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
            "            <scheduleEvents/>\n" +
            "            <relation broadcaster=\"AVRO\" type=\"THESAURUS\">synoniem</relation>\n" +
            "            <relation broadcaster=\"EO\" type=\"KOOR\">Ulfts Mannenkoor</relation>\n" +
            "            <relation broadcaster=\"VPRO\" type=\"ARTIST\">Marco Borsato</relation>\n" +
            "            <relation uriRef=\"http://www.bluenote.com/\" broadcaster=\"VPRO\" type=\"LABEL\">Blue Note</relation>\n" +
            "            <images>\n" +
            "                <shared:image owner=\"BROADCASTER\" type=\"PICTURE\" highlighted=\"false\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:image:2\">\n" +
            "                    <shared:title>Eerste plaatje met credits</shared:title>\n" +
            "                    <shared:credits>CREDITS</shared:credits>\n" +
            "                    <shared:source>SOURCE</shared:source>\n" +
            "                    <shared:license>PUBLIC_DOMAIN</shared:license>\n" +
            "                </shared:image>\n" +
            "                <shared:image owner=\"BROADCASTER\" type=\"PICTURE\" highlighted=\"false\" workflow=\"FOR PUBLICATION\" urn=\"urn:vpro:media:image:3\">\n" +
            "                    <shared:title>Tweede plaatje met credits</shared:title>\n" +
            "                    <shared:credits>CREDITS</shared:credits>\n" +
            "                    <shared:source>SOURCE</shared:source>\n" +
            "                    <shared:license>PUBLIC_DOMAIN</shared:license>\n" +
            "                </shared:image>\n" +
            "            </images>\n" +
            "            <start>P0DT0H0M0.000S</start>\n" +
            "        </segment>\n" +
            "    </segments>\n" +
            "</program>\n");
    }
}
