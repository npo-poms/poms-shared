package nl.vpro.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public CommandExecutorImpl(String c) {
        binary = c;
    }


    @Override
    public int execute(String... args) {
        return execute(LoggerOutputStream.info(getLogger()), LoggerOutputStream.error(getLogger()),  args);
    }

    @Override
    public int execute(OutputStream out, OutputStream errors, String... args) {
        final List<String> command = new ArrayList<String>();
        command.add(binary);
        ProcessBuilder pb = new ProcessBuilder(command);
        Process p;
        try {
            Collections.addAll(command, args);
            LOG.info("Executing " + command);
            p = pb.start();
            Copier copier       = copyThread(p.getInputStream(), out);
            Copier errorCopier  = copyThread(p.getErrorStream(), errors);
            p.waitFor();
            copier.waitFor();
            errorCopier.waitFor();
            int result = p.exitValue();
            if (result != 0) {
                LOG.error("Error occurred while calling " + command + " (see log)");
            }
            out.flush();
            errors.close();
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(CommandExecutorImpl.class.getName() +
            ("." + binary).replaceAll("[\\/\\.\\\\]+", "."));
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



}
