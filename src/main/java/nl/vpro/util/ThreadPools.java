package nl.vpro.util;

import java.util.concurrent.ThreadFactory;

/**
 * Utilities related to ThreadPools
 * @author Michiel Meeuwissen
 * @since 1.5
 */
public final class ThreadPools {

    private ThreadPools() {
        // this class has no intances
    }

    private static ThreadGroup THREAD_GROUP = new ThreadGroup(ThreadPools.class.getName());


    public static ThreadFactory createThreadFactory(final String namePrefix, final boolean daemon, final int priority) {
        return new ThreadFactory() {
            long counter = 1;
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(THREAD_GROUP, r);
                thread.setDaemon(daemon);
                thread.setPriority(priority);
                thread.setName(namePrefix +
                    (namePrefix.endsWith("-") ? "" : "-") +
                    (counter++)
                );
                return thread;
            }
        };
    }

}
