package nl.vpro.domain.media.support;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.12
 */
public class SubtitlesWorkflowTest {

    @Test
    @Ignore("Fails")
    public void mediaWorkFlow() {
        assertThat(
            Arrays.stream(SubtitlesWorkflow.values())
                .filter(sw -> sw.getMedia() == SubtitlesWorkflow.MediaSub.PUBLISHED)
        ).containsExactlyElementsOf(
            SubtitlesWorkflow.PUBLISHED_WORKFLOW
        );
    }


}
