package nl.vpro.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.logging.LoggerOutputStream;

/**
 * Wrapper around ProcessorBuilder
 * It makes calling that somewhat simpler and also implements an interface, for easier mocking in test cases.
 *
 * @author Michiel Meeuwissen
 * @since 1.6
 */
public class CommandExecutorImpl implements CommandExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(CommandExecutorImpl.class);

    private final String binary;


    private long processTimeout = -1l;
    private static final Timer PROCESS_MONITOR = new Timer(true); // create as daemon so that it shuts down at program exit


    public CommandExecutorImpl(String c) {
        binary = c;
    }


    @Override
    public int execute(String... args) {
        return execute(LoggerOutputStream.info(getLogger()), null,  args);
    }

    @Override
    public int execute(final OutputStream out, OutputStream errors, String... args) {
        if (errors == null) {
            errors = LoggerOutputStream.error(getLogger(), true);
        }
        final List<String> command = new ArrayList<String>();
        command.add(binary);
        ProcessBuilder pb = new ProcessBuilder(command);
        Process p;
        try {
            Collections.addAll(command, args);
            LOG.info("Executing " + command);
            p = pb.start();
            final ProcessTimeoutHandle handle;
            if (processTimeout > 0l) {
                handle = startProcessTimeoutMonitor(p, "" + command, processTimeout * 1000);
            } else {
                handle = null;
            }
            Copier copier       = out != null ? copyThread(p.getInputStream(), out) : null;
            Copier errorCopier  = copyThread(p.getErrorStream(), errors);
            p.waitFor();
            if (copier != null) {
                copier.waitFor();
            }
            errorCopier.waitFor();
            int result = p.exitValue();
            if (result != 0) {
                LOG.error("Error {} occurred while calling {}  (see log)", result, command);
            }
            if (out != null) {
                out.flush();
            }
            errors.close();
            if (handle != null) {
                handle.cancel();
            }
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void setProcessTimeout(long processTimeout) {
        this.processTimeout = processTimeout;
    }

    Logger getLogger() {
        String[] split = binary.split("[\\/\\.\\\\]+");
        StringBuilder category = new StringBuilder(CommandExecutorImpl.class.getName());
        for (int i = split.length -1; i >=0; i-- ) {
            if (split[i].length() > 0) {
                category.append('.').append(split[i]);
            }
        }
        return LoggerFactory.getLogger(category.toString());
    }


    protected Copier copyThread(InputStream in, OutputStream out) {
        Copier copier = new Copier(in, out);
        copyExecutor.execute(copier);
        return copier;
    }

    private static final ThreadPoolExecutor copyExecutor =
        new ThreadPoolExecutor(2, 2000, 60, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>(),
            ThreadPools.createThreadFactory(
                CommandExecutorImpl.class.getName() + "-Copier",
                false,
                Thread.NORM_PRIORITY));


    private  static class Copier implements Runnable {
        private boolean ready;
        private long count = 0;
        private final InputStream in;
        private final OutputStream out;

        public Copier(InputStream i, OutputStream o) {
            in = i;
            out = o;
        }

        @Override
        public void run() {
            try {
                count = IOUtils.copy(in, out);
            } catch (Throwable t) {
                LOG.error("Connector " + toString() + ": " + t.getClass() + " " + t.getMessage());
            }
            synchronized (this) {
                ready = true;
                notifyAll();
            }
        }

        public void waitFor() throws InterruptedException {
            synchronized (this) {
                while (!ready) wait();
            }
        }


        public long getCount() {
            return count;
        }

    }

    private static class ProcessTimeoutHandle {
        private final ProcessTimeoutTask task;

        protected ProcessTimeoutHandle(ProcessTimeoutTask task) {
            this.task = task;
        }

        public void cancel() {
            task.cancel();
            PROCESS_MONITOR.purge();
        }

    }

    private static class ProcessTimeoutTask extends TimerTask {
        private final Process monitoredProcess;
        private final String command;

        protected ProcessTimeoutTask(Process monitoredProcess, String command) {
            this.monitoredProcess = monitoredProcess;
            this.command = command;
        }

        @Override
        public void run() {
            try {
                // already terminated? If not, it throws IllegalThreadStateException, otherwise it will
                // return forked process' exit value, which we aren't interested in
                monitoredProcess.exitValue();
            } catch (IllegalThreadStateException itse) {
                // wasn't terminated, kill it
                LOG.warn("The process {} took too long, killing it.", command);
                monitoredProcess.destroy();
            }
        }
    }


    private static ProcessTimeoutHandle startProcessTimeoutMonitor(Process process, String command, long timeout) {
        ProcessTimeoutTask task = new ProcessTimeoutTask(process, command); // task fires after timeout and kills the process
        PROCESS_MONITOR.schedule(task, timeout); // schedule the task to fire

        return new ProcessTimeoutHandle(task); // wrap the task so we can cancel when process finishes before timeout occurs
    }

    @Override
    public String toString() {
        return binary;
    }



}
