package nl.vpro.domain.media;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.support.OwnerType;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
class StandaloneMemberRefTest {


    @Test
    public void roundTripMemberRef(){

        MemberRef ref = MemberRef.builder()
            .midRef("parent")
            .added(Instant.EPOCH)
            .highlighted(false)
            .number(3)
            .owner(OwnerType.BROADCASTER)
            .build();

        MemberRef rounded = StandaloneMemberRef.memberRef("child", ref).toMemberRef();

        assertThat(rounded.getMidRef()).isEqualTo(ref.getMidRef());
        assertThat(rounded.getAdded()).isEqualTo(ref.getAdded());
        assertThat(rounded.isHighlighted()).isEqualTo(ref.isHighlighted());
        assertThat(rounded.getNumber()).isEqualTo(ref.getNumber());
        assertThat(rounded.getOwner()).isEqualTo(ref.getOwner());

        // assertThat(rounded).isEqualTo(ref); equals is a bit odd.


    }

}
