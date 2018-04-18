package nl.vpro.domain;

import java.time.Instant;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class EmbargosTest {
    @Test
    public void copyIfMoreRestricted() {
        ReadonlyEmbargo from = Embargos.readyOnly(null, null);
        Embargo to = Embargos.of(Instant.ofEpochMilli(100), null);

        Embargos.copyIfMoreRestricted(from, to);
        assertThat(to.getPublishStartInstant().toEpochMilli()).isEqualTo(100);
        assertThat(to.getPublishStopInstant()).isNull();
    }

    @Test
    public void copyIfMoreRestricted2() {
        Embargo to = Embargos.of(Instant.ofEpochMilli(100), null);
        ReadonlyEmbargo from = Embargos.readyOnly(Instant.ofEpochMilli(50), null);
        Embargos.copyIfMoreRestricted(from, to);
        assertThat(to.getPublishStartInstant().toEpochMilli()).isEqualTo(100);
        assertThat(to.getPublishStopInstant()).isNull();


    }


    @Test
    public void copyIfMoreRestricted3() {
        Embargo to = Embargos.of(Instant.ofEpochMilli(100), null);
        ReadonlyEmbargo from = Embargos.readyOnly(Instant.ofEpochMilli(150), null);
        Embargos.copyIfMoreRestricted(from, to);
        assertThat(to.getPublishStartInstant().toEpochMilli()).isEqualTo(150);
        assertThat(to.getPublishStopInstant()).isNull();


    }


    @Test
    public void copyIfMoreRestricted4() {
        Embargo to = Embargos.of(Instant.ofEpochMilli(100), null);
        ReadonlyEmbargo from = Embargos.readyOnly(Instant.ofEpochMilli(150), Instant.ofEpochMilli(500));
        Embargos.copyIfMoreRestricted(from, to);
        assertThat(to.getPublishStartInstant().toEpochMilli()).isEqualTo(150);
        assertThat(to.getPublishStopInstant().toEpochMilli()).isEqualTo(500);


    }


    @Test
    public void copyIfMoreRestricted5() {
        Embargo to = Embargos.of(Instant.ofEpochMilli(100), Instant.ofEpochMilli(600));
        ReadonlyEmbargo from = Embargos.readyOnly(Instant.ofEpochMilli(150), Instant.ofEpochMilli(500));
        Embargos.copyIfMoreRestricted(from, to);
        assertThat(to.getPublishStartInstant().toEpochMilli()).isEqualTo(150);
        assertThat(to.getPublishStopInstant().toEpochMilli()).isEqualTo(500);


    }


    @Test
    public void copyIfLessRestricted() {
        ReadonlyEmbargo from = Embargos.readyOnly(Instant.ofEpochMilli(100), Instant.ofEpochMilli(10000));
        Embargo to = Embargos.of(null, Instant.ofEpochMilli(200));

        Embargos.copyIfLessRestricted(from, to);
        assertThat(to.getPublishStartInstant()).isNull();
        assertThat(to.getPublishStopInstant().toEpochMilli()).isEqualTo(10000);
    }

}
