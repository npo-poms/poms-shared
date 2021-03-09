package nl.vpro.domain.media;

import java.time.Instant;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static nl.vpro.domain.media.MediaBuilder.program;
import static nl.vpro.domain.media.MediaBuilder.segment;
import static nl.vpro.domain.media.support.Workflow.PARENT_REVOKED;
import static nl.vpro.domain.media.support.Workflow.PUBLISHED;

public class TrackableObjectTest {

    @Test
    public void workflowParentRevokedIsPublishableWhenParentIsNull() {
        final Segment parentNull = segment().workflow(PARENT_REVOKED).build();

        Assertions.assertThat(parentNull.isPublishable(Instant.now())).isTrue();
    }

    @Test
    public void workflowParentRevokedIsPublishableWhenParentIsSo() {
        final Segment parentPublished = segment().workflow(PARENT_REVOKED).parent(
            program().workflow(PUBLISHED).build()
        ).build();

        Assertions.assertThat(parentPublished.isPublishable(Instant.now())).isTrue();
    }

    @Test
    public void workflowParentRevokedIsNotPublishableWhenParentIsNotPublishable() {
        final Instant now = Instant.now();
        final Segment parentToRevoke = segment().workflow(PARENT_REVOKED).parent(
            program().workflow(PUBLISHED).publishStop(now.minusSeconds(60)).build()
        ).build();

        Assertions.assertThat(parentToRevoke.isPublishable(now)).isFalse();
    }
}
