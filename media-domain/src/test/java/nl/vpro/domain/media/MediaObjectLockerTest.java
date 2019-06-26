package nl.vpro.domain.media;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

import nl.vpro.logging.simple.ChainedSimpleLogger;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.Slf4jSimpleLogger;
import nl.vpro.logging.simple.StringBuilderSimpleLogger;

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
        Thread thread1 = new Thread(new Runnable() {
            @Override

            public void run() {
                log.info("start test1");
                withMidLock("mid0", "test1", new Runnable() {
                    @Override
                    @SneakyThrows
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            final int fi = i;
                            withMidLock("mid" + (i % 2), "test1.sub", new Runnable() {
                                @SneakyThrows
                                @Override
                                public void run() {
                                    logger.info("1:" + fi);
                                    Thread.sleep(100);;

                                }
                            });
                        }
                    }
                });

            }
        });
        log.info("bla");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("start test2");
                withMidLock("mid0", "test2", new Runnable() {
                    @Override
                    @SneakyThrows
                    public void run() {
                        for (int i = 0; i < 10; i++) {
                            logger.info("2:"+i);
                            Thread.sleep(100);;
                        }

                    }
                });
            }
        });
        log.info("now starting");
        thread2.start();
        Thread.sleep(500);
        thread1.start();

        thread1.join();
        thread2.join();
        assertThat(sb.getStringBuilder().toString()).isEqualTo("INFO 2:0\n" +
            "INFO 2:1\n" +
            "INFO 2:2\n" +
            "INFO 2:3\n" +
            "INFO 2:4\n" +
            "INFO 2:5\n" +
            "INFO 2:6\n" +
            "INFO 2:7\n" +
            "INFO 2:8\n" +
            "INFO 2:9\n" +
            "INFO 1:0\n" +
            "INFO 1:1\n" +
            "INFO 1:2\n" +
            "INFO 1:3\n" +
            "INFO 1:4\n" +
            "INFO 1:5\n" +
            "INFO 1:6\n" +
            "INFO 1:7\n" +
            "INFO 1:8\n" +
            "INFO 1:9");


        assertThat(MediaObjectLocker.HOLDS.get()).isEmpty();
    }

    @Test
    public void lock() {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        lock.lock();
        log.info("{}", lock.getHoldCount());
    }

}
