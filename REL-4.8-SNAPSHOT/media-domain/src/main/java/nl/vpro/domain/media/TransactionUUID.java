package nl.vpro.domain.media;

import java.util.*;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michiel Meeuwissen
 * @since 3.4
 */
public class TransactionUUID {
    
    private static final Logger LOG = LoggerFactory.getLogger(TransactionUUID.class);
    
    private static List<TransactionUUIDConsumer> consumers = new ArrayList<>();  
    static {
        ServiceLoader.load(TransactionUUIDConsumer.class).iterator().forEachRemaining(p ->
            consumers.add(p)
        );
        if (! consumers.isEmpty()) {
            LOG.info("Using consumers {}", consumers);
        }
    }

    private static ThreadLocal<UUID> threadLocal = new ThreadLocal<UUID>() {
        @Override
        public UUID initialValue() {
            return UUID.randomUUID();
        }
    };

    public static UUID get() {
        UUID uuid = threadLocal.get();
        consume(uuid.toString());
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

    public static void reset() {
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
