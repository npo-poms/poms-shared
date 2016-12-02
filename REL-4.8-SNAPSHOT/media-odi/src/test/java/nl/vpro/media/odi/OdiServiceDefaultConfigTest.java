package nl.vpro.media.odi;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaBuilder;
import nl.vpro.domain.media.Program;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.9
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:odiService-context.xml")
public class OdiServiceDefaultConfigTest {

    @Inject
    OdiService odiService;

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
        assertThat(odiService.playMedia(program, null, "mp4").getProgramUrl()).isEqualTo("http://video.omroep.nl/ntr/schooltv/beeldbank/video/20060712_ziektekostenverzekering.mp4");

    }
}
