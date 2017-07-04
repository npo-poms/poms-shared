package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@Slf4j
public class TransactionUUID {

    private static List<TransactionUUIDConsumer> consumers = new ArrayList<>();
    static {
        ServiceLoader.load(TransactionUUIDConsumer.class).iterator().forEachRemaining(p ->
            consumers.add(p)
        );
        if (! consumers.isEmpty()) {
            log.info("Using consumers {}", consumers);
        }
    }

    private static ThreadLocal<UUID> threadLocal = ThreadLocal.withInitial(() -> null);

    public static UUID get() {
        UUID uuid = threadLocal.get();

        return uuid;
    }

    public static UUID set(UUID uuid) {
        threadLocal.set(uuid);
        consume(uuid.toString());
        return uuid;
    }

    private static void consume(String uuid){
        for (TransactionUUIDConsumer consumer : consumers) {
            consumer.accept(uuid);
        }
    }
    private static void consume() {
        UUID uuid = get();
        if (uuid != null) {
            consume(uuid.toString());
        }
    }


    public static void newUUIDIfEmpty() {
        if (threadLocal.get() == null) {
            threadLocal.set(UUID.randomUUID());
            log.info("Logging on transaction uuid {}", threadLocal.get());
            consume();
        }
    }

    public static void clear() {
        threadLocal.remove();
    }

    public static List<TransactionUUIDConsumer> getConsumers() {
        return Collections.unmodifiableList(consumers);
    }

    /**
     * @author Michiel Meeuwissen
     * @since 1.8
     */
    public interface TransactionUUIDConsumer extends Consumer<String> {
    }
}
