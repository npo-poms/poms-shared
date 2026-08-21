package nl.vpro.media.odi;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.vpro.domain.media.*;
import nl.vpro.media.odi.handler.AVFileFormatLocationSorter;
import nl.vpro.media.odi.util.LocationResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 */

@ExtendWith({SpringExtension.class})
@ContextConfiguration(locations = "classpath:odiService-context.xml")
public class OdiServiceDefaultConfigTest {

    @Inject
    OdiServiceImpl odiService;

    @Test
    public void test() {
        Program program = MediaBuilder.program().locations(
            Location.builder()
                .programUrl("odi+http://odi.omroep.nl/video/h264_std/20060712_ziektekostenverzekering")
                .avFileFormat(AVFileFormat.HASP)
                .build(),
            Location.builder()
                .programUrl("http://video.omroep.nl/ntr/schooltv/beeldbank/video/20060712_ziektekostenverzekering.mp4")
                .avFileFormat(AVFileFormat.MP4)
                .build()

        ).build();
        assertThat(odiService.playMedia(program, null).getProgramUrl()).startsWith("${odi.baseUrl}/video/${odi.aplication}/h264_std/").endsWith("20060712_ziektekostenverzekering?type=http");

        //API-292
        List<LocationResult> mp4 = odiService.handleLocations(program, null, "mp4");
        Collections.sort(mp4, new AVFileFormatLocationSorter().getComparator("mp4"));
        assertThat(mp4).hasSize(2);
        assertThat(mp4.get(0).getAvFileFormat()).isEqualTo(AVFileFormat.MP4);
        assertThat(odiService.playMedia(program, null, "mp4").getProgramUrl()).isEqualTo("http://video.omroep.nl/ntr/schooltv/beeldbank/video/20060712_ziektekostenverzekering.mp4");

    }

    @Test
    public void API_293() {
        String programXml =
            """
                <program xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" type="BROADCAST" avType="VIDEO" embeddable="true" hasSubtitles="true" mid="VPWON_1225089" sortDate="2014-12-24T18:23:00+01:00" creationDate="2014-11-19T22:44:52.724+01:00" lastModified="2015-06-10T09:35:24.822+02:00" publishDate="2016-04-08T13:05:49.696+02:00" urn="urn:vpro:media:program:47769966" workflow="PUBLISHED">
                <broadcaster id="NTR">NTR</broadcaster>
                <title owner="MIS" type="MAIN">Het Klokhuis</title>
                <title owner="WHATS_ON" type="MAIN">Europa</title>
                <title owner="MIS" type="SUB">Europa</title>
                <title owner="WHATS_ON" type="ORIGINAL">Europa</title>
                <description owner="MIS" type="MAIN">
                Laatste aflevering uit de serie Het Klokhuis maakt geschiedenis. Lisa vertelt de geschiedenis van Europa. Na de oorlog begonnen zes landen samen te werken in de Europese Gemeenschap voor Kolen en Staal. Inmiddels is de groep uitgegroeid tot 27 landen en heet het de Europese Gemeenschap. Waar is dat eigenlijk goed voor?
                </description>
                <description owner="WHATS_ON" type="MAIN">
                Laatste aflevering uit de serie .Het Klokhuis maakt geschiedenis.Lisa vertelt de geschiedenis van Europa. Na de oorlog begonnen 6 landen samen te werken in de Europese Gemeenschap voor Kolen en Staal. Inmiddels is de groep uitgegroeid tot 27 landen en heet het de Europese Gemeenschap. Waar is dat eigenlijk goed voor?
                </description>
                <description owner="MIS" type="SHORT">
                Laatste aflevering uit de serie Het Klokhuis maakt geschiedenis. Lisa vertelt de geschiedenis van Europa.
                </description>
                <description owner="WHATS_ON" type="SHORT">
                Laatste aflevering uit de serie.Het Klokhuis maakt geschiedenis.Lisa vertelt de geschiedenis van Europa.
                </description>
                <description owner="MIS" type="KICKER">
                Laatste aflevering uit de serie Het Klokhuis maakt geschiedenis.
                </description>
                <genre id="3.0.1.1.7">
                <term>Jeugd</term>
                <term>Informatief</term>
                </genre>
                <country code="NL">Nederland</country>
                <language code="nl">Nederlands</language>
                <avAttributes>
                <videoAttributes>
                <color>BLACK AND WHITE AND COLOR</color>
                <aspectRatio>16:9</aspectRatio>
                </videoAttributes>
                <audioAttributes>
                <numberOfChannels>2</numberOfChannels>
                </audioAttributes>
                </avAttributes>
                <releaseYear>2012</releaseYear>
                <duration authorized="true">PT15M14.980S</duration>
                <credits/>
                <descendantOf urnRef="urn:vpro:media:group:3731098" midRef="POMS_S_NTR_059960" type="SERIES"/>
                <descendantOf urnRef="urn:vpro:media:group:41634600" midRef="VPWON_1225086" type="SEASON"/>
                <descendantOf urnRef="urn:vpro:media:group:44769728" midRef="POMS_S_NTR_624076" type="COLLECTION"/>
                <descendantOf urnRef="urn:vpro:media:group:70885599" midRef="VPWON_1256937" type="SERIES"/>
                <website>www.hetklokhuis.nl</website>
                <prediction state="REALIZED">INTERNETVOD</prediction>
                <prediction state="REALIZED" publishStart="2014-12-24T18:40:52+01:00" publishStop="2014-12-24T18:40:52+01:00">PLUSVOD</prediction>
                <locations>
                <location owner="CERES" platform="INTERNETVOD" creationDate="2014-12-24T19:06:40.459+01:00" lastModified="2014-12-24T19:06:40.616+01:00" urn="urn:vpro:media:location:49491099" workflow="PUBLISHED">
                <programUrl>odip+http://odi.omroep.nl/video/adaptive/VPWON_1225089</programUrl>
                <avAttributes>
                <avFileFormat>HASP</avFileFormat>
                </avAttributes>
                </location>
                <location owner="CERES" platform="INTERNETVOD" creationDate="2014-12-24T18:56:30.332+01:00" lastModified="2014-12-24T18:56:30.569+01:00" urn="urn:vpro:media:location:49490903" workflow="PUBLISHED">
                <programUrl>odi+http://odi.omroep.nl/video/h264_sb/VPWON_1225089</programUrl>
                <avAttributes>
                <bitrate>200000</bitrate>
                <avFileFormat>H264</avFileFormat>
                </avAttributes>
                </location>
                <location owner="CERES" platform="INTERNETVOD" creationDate="2014-12-24T18:56:31.086+01:00" lastModified="2014-12-24T18:56:31.439+01:00" urn="urn:vpro:media:location:49490906" workflow="PUBLISHED">
                <programUrl>
                odi+http://odi.omroep.nl/video/h264_bb/VPWON_1225089
                </programUrl>
                <avAttributes>
                <bitrate>500000</bitrate>
                <avFileFormat>H264</avFileFormat>
                </avAttributes>
                </location>
                <location owner="CERES" platform="INTERNETVOD" creationDate="2014-12-24T18:56:29.720+01:00" lastModified="2014-12-24T18:56:30.062+01:00" urn="urn:vpro:media:location:49490900" workflow="PUBLISHED">
                <programUrl>
                odi+http://odi.omroep.nl/video/h264_std/VPWON_1225089
                </programUrl>
                <avAttributes>
                <bitrate>1000000</bitrate>
                <avFileFormat>H264</avFileFormat>
                </avAttributes>
                </location>
                <location owner="CERES" platform="PLUSVOD" creationDate="2014-12-24T18:51:14.926+01:00" lastModified="2014-12-24T18:51:15.126+01:00" publishStart="2014-12-24T18:40:52+01:00" publishStop="2014-12-24T18:40:52+01:00" urn="urn:vpro:media:location:49490763" workflow="FOR PUBLICATION">
                <programUrl>sub+http://npoplus.nl/video/mpeg2_hr/VPWON_1225089</programUrl>
                <avAttributes>
                <bitrate>3500000</bitrate>
                <avFileFormat>MPEG2</avFileFormat>
                </avAttributes>
                </location>
                </locations>
                <scheduleEvents>
                <scheduleEvent channel="NED3" imi="" midRef="VPWON_1225089" net="ZAPP" urnRef="urn:vpro:media:program:47769966">
                <textSubtitles>Teletekst ondertitels</textSubtitles>
                <guideDay>2014-12-24+01:00</guideDay>
                <start>2014-12-24T18:23:00+01:00</start>
                <offset>PT2M37.000S</offset>
                <duration>PT15M0.000S</duration>
                <poProgID>VPWON_1225089</poProgID>
                </scheduleEvent>
                <scheduleEvent channel="BVNT" imi="" midRef="VPWON_1225089" urnRef="urn:vpro:media:program:47769966">
                <repeat isRerun="true">21-03-2013</repeat>
                <textSubtitles>Teletekst ondertitels</textSubtitles>
                <guideDay>2014-12-31+01:00</guideDay>
                <start>2014-12-31T16:20:00+01:00</start>
                <duration>PT15M0.000S</duration>
                <poProgID>VPWON_1225089</poProgID>
                </scheduleEvent>
                <scheduleEvent channel="BVNT" imi="" midRef="VPWON_1225089" urnRef="urn:vpro:media:program:47769966">
                <repeat isRerun="true">21-03-2013</repeat>
                <textSubtitles>Teletekst ondertitels</textSubtitles>
                <guideDay>2014-12-31+01:00</guideDay>
                <start>2015-01-01T04:20:00+01:00</start>
                <duration>PT15M0.000S</duration>
                <poProgID>VPWON_1225089</poProgID>
                </scheduleEvent>
                </scheduleEvents>
                <images>
                <shared:image owner="NEBO" type="STILL" highlighted="false" creationDate="2014-12-24T19:00:17.184+01:00" lastModified="2014-12-24T19:00:19.631+01:00" urn="urn:vpro:media:image:49490975" workflow="PUBLISHED">
                <shared:title>Het Klokhuis</shared:title>
                <shared:description>
                Laatste aflevering uit de serie Het Klokhuis maakt geschiedenis. Lisa vertelt de geschiedenis van Europa.
                </shared:description>
                <shared:imageUri>urn:vpro:image:554954</shared:imageUri>
                </shared:image>
                <shared:image owner="NEBO" type="STILL" highlighted="false" creationDate="2014-12-24T19:00:18.818+01:00" lastModified="2014-12-24T19:00:19.632+01:00" urn="urn:vpro:media:image:49490976" workflow="PUBLISHED">
                <shared:title>Het Klokhuis</shared:title>
                <shared:description>
                Laatste aflevering uit de serie Het Klokhuis maakt geschiedenis. Lisa vertelt de geschiedenis van Europa.
                </shared:description>
                <shared:imageUri>urn:vpro:image:554955</shared:imageUri>
                </shared:image>
                <shared:image owner="NEBO" type="STILL" highlighted="false" creationDate="2014-12-24T19:00:19.577+01:00" lastModified="2014-12-24T19:00:19.633+01:00" urn="urn:vpro:media:image:49490977" workflow="PUBLISHED">
                <shared:title>Het Klokhuis</shared:title>
                <shared:description>
                Laatste aflevering uit de serie Het Klokhuis maakt geschiedenis. Lisa vertelt de geschiedenis van Europa.
                </shared:description>
                <shared:imageUri>urn:vpro:image:554956</shared:imageUri>
                </shared:image>
                </images>
                <episodeOf added="2014-11-30T06:49:19.451+01:00" highlighted="false" midRef="VPWON_1225086" index="3395" type="SEASON" urnRef="urn:vpro:media:group:41634600"/>
                <segments/>
                </program>""";
        Program program = JAXB.unmarshal(new StringReader(programXml), Program.class);
        assertThat(program.getLocations()).hasSize(5);

        assertThat(odiService.playMedia(program, null).getProgramUrl()).startsWith("${odi.baseUrl}/video/");

        assertThat(odiService.playMedia(program, null, "mp4").getProgramUrl()).startsWith("${odi.baseUrl}/video/");

        List<LocationResult> result = odiService.handleLocations(program, null, "mp4", "h264", "hasp");
        assertThat(odiService.playMedia(program, null, "mp4", "h264", "hasp").getProgramUrl()).startsWith("${odi.baseUrl}/video/");

    }

    @Test
    public void API_293_POW_02988195() {
        String programXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><program xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" type=\"BROADCAST\" avType=\"VIDEO\" embeddable=\"true\" hasSubtitles=\"true\" mid=\"POW_02988195\" sortDate=\"2016-11-30T11:00:00+01:00\" creationDate=\"2016-10-13T14:52:12.172+02:00\" lastModified=\"2016-11-30T13:00:20.694+01:00\" publishDate=\"2016-11-30T13:02:17.743+01:00\" urn=\"urn:vpro:media:program:83009519\" workflow=\"PUBLISHED\"><crid>crid://npo/programmagegevens/1268510256668</crid><broadcaster id=\"NOS\">NOS</broadcaster><title owner=\"MIS\" type=\"MAIN\">NOS Journaal</title><title owner=\"MIS\" type=\"SUB\">NOS Journaal</title><description owner=\"MIS\" type=\"SHORT\">Met het laatste nieuws, gebeurtenissen van nationaal en internationaal belang en de weersverwachting voor vandaag.</description><description owner=\"MIS\" type=\"KICKER\">Met het laatste nieuws, gebeurtenissen van nationaal en internationaal belang en de weersverwachting voor vandaag.</description><genre id=\"3.0.1.7.21\"><term>Informatief</term><term>Nieuws/actualiteiten</term></genre><country code=\"NL\">Nederland</country><language code=\"nl\">Nederlands</language><releaseYear>2016</releaseYear><duration authorized=\"true\">PT8M46.990S</duration><credits/><descendantOf urnRef=\"urn:vpro:media:group:3730932\" midRef=\"POMS_S_NOS_059925\" type=\"SERIES\"/><descendantOf urnRef=\"urn:vpro:media:group:66537768\" midRef=\"16Jnl1100\" type=\"SEASON\"/><descendantOf urnRef=\"urn:vpro:media:group:70890284\" midRef=\"NOSjnl1100\" type=\"SERIES\"/><descendantOf urnRef=\"urn:vpro:media:group:79149439\" midRef=\"POMS_S_NOS_4413283\" type=\"SEASON\"/><website>NOS.nl</website><twitter type=\"ACCOUNT\">@NOS</twitter><twitter type=\"HASHTAG\">#NOS</twitter><prediction state=\"REALIZED\">INTERNETVOD</prediction><prediction state=\"REALIZED\" publishStart=\"2016-11-30T11:09:28+01:00\" publishStop=\"2016-12-10T11:09:28+01:00\">TVVOD</prediction><prediction state=\"REALIZED\" publishStart=\"2016-11-30T11:09:28+01:00\" publishStop=\"2017-11-30T11:09:28+01:00\">PLUSVOD</prediction><locations><location owner=\"PLUTO\" platform=\"INTERNETVOD\" creationDate=\"2016-11-30T11:16:07.454+01:00\" lastModified=\"2016-11-30T11:16:07.482+01:00\" urn=\"urn:vpro:media:location:84355899\" workflow=\"PUBLISHED\"><programUrl>odip+http://odi.omroep.nl/video/adaptive/POW_02988195</programUrl><avAttributes><avFileFormat>HASP</avFileFormat></avAttributes></location><location owner=\"PLUTO\" platform=\"INTERNETVOD\" creationDate=\"2016-11-30T11:15:07.289+01:00\" lastModified=\"2016-11-30T11:15:07.305+01:00\" urn=\"urn:vpro:media:location:84355881\" workflow=\"PUBLISHED\"><programUrl>odi+http://odi.omroep.nl/video/h264_sb/POW_02988195</programUrl><avAttributes><bitrate>200000</bitrate><avFileFormat>H264</avFileFormat></avAttributes></location><location owner=\"PLUTO\" platform=\"INTERNETVOD\" creationDate=\"2016-11-30T11:15:06.960+01:00\" lastModified=\"2016-11-30T11:15:06.965+01:00\" urn=\"urn:vpro:media:location:84355878\" workflow=\"PUBLISHED\"><programUrl>odi+http://odi.omroep.nl/video/h264_bb/POW_02988195</programUrl><avAttributes><bitrate>500000</bitrate><avFileFormat>H264</avFileFormat></avAttributes></location><location owner=\"PLUTO\" platform=\"INTERNETVOD\" creationDate=\"2016-11-30T11:16:07.821+01:00\" lastModified=\"2016-11-30T11:16:07.865+01:00\" urn=\"urn:vpro:media:location:84355901\" workflow=\"PUBLISHED\"><programUrl>odi+http://odi.omroep.nl/video/h264_std/POW_02988195</programUrl><avAttributes><bitrate>1000000</bitrate><avFileFormat>H264</avFileFormat></avAttributes></location><location owner=\"PROJECTM\" platform=\"TVVOD\" creationDate=\"2016-11-30T11:11:05.646+01:00\" lastModified=\"2016-11-30T11:11:05.673+01:00\" publishStart=\"2016-11-30T11:09:28+01:00\" publishStop=\"2016-12-10T11:09:28+01:00\" urn=\"urn:vpro:media:location:84355543\" workflow=\"PUBLISHED\"><programUrl>sub+http://tvvod.omroep.nl/video/H264_2500/POW_02988195</programUrl><avAttributes><bitrate>2500000</bitrate><avFileFormat>H264</avFileFormat></avAttributes></location><location owner=\"PROJECTM\" platform=\"PLUSVOD\" creationDate=\"2016-11-30T11:13:05.895+01:00\" lastModified=\"2016-11-30T11:13:05.898+01:00\" publishStart=\"2016-11-30T11:09:28+01:00\" publishStop=\"2017-11-30T11:09:28+01:00\" urn=\"urn:vpro:media:location:84355649\" workflow=\"PUBLISHED\"><programUrl>sub+http://npo.npoplus.nl/video/MXF/POW_02988195</programUrl><avAttributes><bitrate>3500000</bitrate><avFileFormat>MPEG2</avFileFormat></avAttributes></location><location owner=\"PROJECTM\" platform=\"TVVOD\" creationDate=\"2016-11-30T11:11:05.286+01:00\" lastModified=\"2016-11-30T11:11:05.291+01:00\" publishStart=\"2016-11-30T11:09:28+01:00\" publishStop=\"2016-12-10T11:09:28+01:00\" urn=\"urn:vpro:media:location:84355540\" workflow=\"PUBLISHED\"><programUrl>sub+http://tvvod.omroep.nl/video/MPEG2_3500/POW_02988195</programUrl><avAttributes><bitrate>3500000</bitrate><avFileFormat>MPEG2</avFileFormat></avAttributes></location></locations><scheduleEvents><scheduleEvent channel=\"NED1\" imi=\"\" midRef=\"POW_02988195\" urnRef=\"urn:vpro:media:program:83009519\"><textSubtitles>Teletekst ondertitels</textSubtitles><guideDay>2016-11-30+01:00</guideDay><start>2016-11-30T11:00:00+01:00</start><duration>PT10M30.000S</duration><poProgID>POW_02988195</poProgID></scheduleEvent></scheduleEvents><images><shared:image owner=\"NEBO\" type=\"STILL\" highlighted=\"false\" creationDate=\"2016-11-30T12:00:06.704+01:00\" lastModified=\"2016-11-30T12:00:07.542+01:00\" urn=\"urn:vpro:media:image:84357316\" workflow=\"PUBLISHED\"><shared:title>NOS Journaal</shared:title><shared:description>Met het laatste nieuws, gebeurtenissen van nationaal en internationaal belang en de weersverwachting voor vandaag.</shared:description><shared:imageUri>urn:vpro:image:835750</shared:imageUri><shared:offset>PT1M0.000S</shared:offset><shared:height>406</shared:height><shared:width>720</shared:width></shared:image><shared:image owner=\"NEBO\" type=\"STILL\" highlighted=\"false\" creationDate=\"2016-11-30T12:00:07.078+01:00\" lastModified=\"2016-11-30T12:00:07.542+01:00\" urn=\"urn:vpro:media:image:84357317\" workflow=\"PUBLISHED\"><shared:title>NOS Journaal</shared:title><shared:description>Met het laatste nieuws, gebeurtenissen van nationaal en internationaal belang en de weersverwachting voor vandaag.</shared:description><shared:imageUri>urn:vpro:image:835751</shared:imageUri><shared:offset>PT2M0.000S</shared:offset><shared:height>406</shared:height><shared:width>720</shared:width></shared:image><shared:image owner=\"NEBO\" type=\"STILL\" highlighted=\"false\" creationDate=\"2016-11-30T12:00:07.504+01:00\" lastModified=\"2016-11-30T12:00:07.542+01:00\" urn=\"urn:vpro:media:image:84357318\" workflow=\"PUBLISHED\"><shared:title>NOS Journaal</shared:title><shared:description>Met het laatste nieuws, gebeurtenissen van nationaal en internationaal belang en de weersverwachting voor vandaag.</shared:description><shared:imageUri>urn:vpro:image:835752</shared:imageUri><shared:offset>PT3M0.000S</shared:offset><shared:height>406</shared:height><shared:width>720</shared:width></shared:image></images><episodeOf added=\"2016-10-13T14:52:12.307+02:00\" highlighted=\"false\" midRef=\"16Jnl1100\" index=\"237\" type=\"SEASON\" urnRef=\"urn:vpro:media:group:66537768\"/><segments/></program>";

        Program program = JAXB.unmarshal(new StringReader(programXml), Program.class);

        {
            LocationResult result = odiService.playMedia(program, null, "mp4");
            assertThat(result.getProgramUrl()).startsWith("${odi.baseUrl}/video/");
            assertThat(result.getUrn()).isEqualTo("urn:vpro:media:location:84355901");

        }
        {
            LocationResult result = odiService.playMedia(program, null, "h264", "mp4", "hasp");
            assertThat(result.getUrn()).isEqualTo("urn:vpro:media:location:84355901");
            assertThat(result.getProgramUrl()).startsWith("${odi.baseUrl}/video/");
        }


    }

}
