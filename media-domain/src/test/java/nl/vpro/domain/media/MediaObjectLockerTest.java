package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.util.locker.ObjectLockerAdmin;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since ...
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

        Assertions.assertThatThrownBy(() -> {
            MediaObjectLocker.withMidLock("mid_1", "test", new Runnable() {
                @Override
                public void run() {
                    MediaObjectLocker.withMidLock("mid_2", "test sub", new Runnable() {
                        @Override
                        public void run() {
                            result.add("bla");
                        }
                    });
                }
            });
        }).isInstanceOf(IllegalStateException.class);
        assertThat(result).isEmpty();

    }

    @Test
    public void strictlyOneUncertain() {
        ObjectLockerAdmin.JMX_INSTANCE.setStrictlyOne(true);
        final List<String> result = new ArrayList<>();

        MediaObjectLocker.withMidLock("mid_1", "test", new Runnable() {
            @Override
            public void run() {
                MediaObjectLocker.withCorrelationLock(MediaIdentifiable.Correlation.crid("crid_1"), "test sub", new Callable() {
                    @Override
                    public Object call() throws Exception {
                        result.add("bla");
                        return null;
                    }
                });
                }
        });
        assertThat(result).containsExactly("bla");
    }

}
