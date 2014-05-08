package nl.vpro.util;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* @author Michiel Meeuwissen
* @since 3.1
*/
public class Copier implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Copier.class);

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
