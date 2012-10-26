package nl.vpro.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

/**
 * Wraps a {@link Logger} in an {@link OutputStream}, making logging available as an outputstream, which can be useful for things that accept outputstreams (e.g. external processes)
 * @author Michiel Meeuwissen
 */
public abstract class LoggerOutputStream extends OutputStream {


    public static LoggerOutputStream info(Logger log) {
        return info(log, false);
    }

    public static LoggerOutputStream info(Logger log, boolean skipEmptyLines) {
        return new LoggerOutputStream(log, skipEmptyLines) {
            @Override
            void log(String line) {
                log.info(line);
            }
        };
    }

    public static LoggerOutputStream error(Logger log) {
        return error(log, false);
    }
    public static LoggerOutputStream error(Logger log, boolean skipEmptyLines) {
        return new LoggerOutputStream(log, skipEmptyLines) {
            @Override
            void log(String line) {
                log.error(line);
            }
        };
    }


    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final Logger log;
    final boolean skipEmptyLines;
    int lastChar = -1;

    LoggerOutputStream(Logger log, boolean skipEmptyLines) {
        this.log = log;
        this.skipEmptyLines = skipEmptyLines;
    }
    abstract void log(String line);


    @Override
    public void write(int b) {
        switch(b) {
            case '\n':
                log();
                break;
            case '\r':
                if (lastChar != '\n') {
                    log();
                }
                break;
            default:
                buffer.write(b);
        }
        lastChar = b;
    }

    @Override
    public void close() throws IOException {
        super.close();
        log();
    }

    private void log() {
        String line = buffer.toString();
        if (!skipEmptyLines || !StringUtils.isBlank(line)) {
            log(line);
        }
        buffer.reset();
    }
}
