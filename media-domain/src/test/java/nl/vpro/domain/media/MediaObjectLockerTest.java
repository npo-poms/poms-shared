package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.util.locker.ObjectLockerAdmin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Michiel Meeuwissen
 */
@Slf4j
class MediaObjectLockerTest {

    @BeforeEach
    public void setup() {
        ObjectLockerAdmin.JMX_INSTANCE.setMaxLockAcquireTime(Duration.ofSeconds(10).toString());
        ObjectLockerAdmin.JMX_INSTANCE.setStrictlyOne(false);
    }

    @Test
    public void strictlyOne() {
        ObjectLockerAdmin.JMX_INSTANCE.setStrictlyOne(true);
        final List<String> result = new ArrayList<>();

        assertThatThrownBy(() -> MediaObjectLocker.withMidLock("mid_1", "test", () ->
            MediaObjectLocker.withMidLock("mid_2", "test sub", () -> {
                result.add("bla");
            }))).isInstanceOf(IllegalStateException.class);
        assertThat(result).isEmpty();

    }

    @Test
    public void strictlyOneUncertain() {
        ObjectLockerAdmin.JMX_INSTANCE.setStrictlyOne(true);
        final List<String> result = new ArrayList<>();

        MediaObjectLocker.withMidLock("mid_1", "test", new Runnable() {
            @Override
            public void run() {
                MediaObjectLocker.withCorrelationLock(MediaIdentifiable.Correlation.crid("crid_1"), "test sub", new Callable<Object>() {
                    @Override
                    public Object call() {
                        result.add("bla");
                        return null;
                    }
                });
                }
        });
        assertThat(result).containsExactly("bla");
    }

}
