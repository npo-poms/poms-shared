package nl.vpro.domain.media;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.vpro.logging.simple.*;

import static nl.vpro.domain.media.MediaObjectLocker.withMidLock;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author Michiel Meeuwissen
 * @since ...
 */
@Slf4j
public class MediaObjectLockerTest {

    @Test
    public void test() throws InterruptedException {
        StringBuilderSimpleLogger sb = new StringBuilderSimpleLogger();
        SimpleLogger logger = new ChainedSimpleLogger(sb, Slf4jSimpleLogger.of(log));
        Thread thread1 = new Thread(new Job(1, logger));
        log.info("bla");
        Thread thread2 = new Thread(new Thread(new Job(2, sb)));
        log.info("now starting");
        thread2.start();
        Thread.sleep(550);
        thread1.start();

        thread1.join();
        thread2.join();
        assertThat(sb.getStringBuilder().toString()).isEqualTo(
            "INFO 2:0\n" +
                "INFO 2:1\n" +
                "INFO 2:2\n" +
                "INFO 2:3\n" +
                "INFO 2:4\n" +
                "INFO 2:5\n" +
                "INFO 1:0\n" +
                "INFO 2:6\n" +
                "INFO 1:1\n" +
                "INFO 2:7\n" +
                "INFO 1:2\n" +
                "INFO 2:8\n" +
                "INFO 1:3\n" +
                "INFO 2:9\n" +
                "INFO 1:4\n" +
                "INFO 1:5\n" +
                "INFO 1:6\n" +
                "INFO 1:7\n" +
                "INFO 1:8\n" +
                "INFO 1:9"
        );


        assertThat(MediaObjectLocker.HOLDS.get()).isEmpty();
    }

    @Test
    public void lock() {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        lock.lock();
        log.info("{}", lock.getHoldCount());
    }

    @Test
    @Disabled
    public void wildTesting() throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        for (int i = 0 ; i < 100; i++) {
            Thread t = new Thread(new Job2("" + i, Slf4jSimpleLogger.of(log), 0));
            t.start();
            threads.add(t);
        }
        for (Thread t : threads) {
            t.join();
        }

    }

    @SneakyThrows
    private static void sleep(long duration) {
        Thread.sleep(duration);
    }

    static final Random random = new Random();

    @SneakyThrows
    private static void randomSleep() {
        sleep(random.nextInt(500));
    }

    private static class Job implements Runnable {
        final int number;
        final SimpleLogger logger;

        private Job(int number, SimpleLogger logger) {
            this.number = number;
            this.logger = logger;
        }

        @Override
        @SneakyThrows
        public void run() {
            for (int i = 0; i < 10; i++) {
                logger.info(number + ":" + i);
                Thread.sleep(100);
            }

        }
    }

    private static final String[] mids = {"mid1", "mid2", "mid3", "mid4"};

    private static class Job2 implements Runnable {
        final String number;
        final SimpleLogger logger;
        final Runnable sleep;
        final Supplier<String> mid;
        final int depth;

        private Job2(
            String number,
            SimpleLogger logger,
            int depth) {
            this.number = number;
            this.logger = logger;
            this.sleep = MediaObjectLockerTest::randomSleep;;
            this.mid =  () -> mids[random.nextInt(mids.length)];
            this.depth = depth;
        }

        @Override
        public void run() {
            log.info("start test" + number);
            for (int i = 0; i < 100; i++) {
                final int j = i;
                final String m = mid.get();
                final String m2 = mid.get();
                withMidLock(m, "test" + number, new Runnable() {
                    @Override
                    @SneakyThrows
                    public void run() {
                        if (random.nextInt(10) == 0) {
                            logger.info(number + ":ex");

                            throw new RuntimeException();
                        }
                        logger.info(m + ":" + number + ":" + j);
                        sleep.run();
                        if (depth < 3) {
                            withMidLock(m2, "test.sub+" + number, new Job2("sub." + number, logger, depth + 1));
                        }
                    }

                });
            };
        }
    }

}
