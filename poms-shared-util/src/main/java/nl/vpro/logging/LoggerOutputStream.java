package nl.vpro.logging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;

/**
 * Wraps a {@link Logger} in an {@link OutputStream}, making logging available as an outputstream, which can be useful for things that accept outputstreams (e.g. external processes)
 * @author Michiel Meeuwissen
 */
public abstract class LoggerOutputStream extends OutputStream {


    public static LoggerOutputStream info(java.util.logging.Logger log) {
        return info(log, false);
    }

    public static LoggerOutputStream info(Logger log) {
        return info(log, false);
    }

    public static LoggerOutputStream info(final Logger log, boolean skipEmptyLines) {
        return new LoggerOutputStream(skipEmptyLines) {
            @Override
            void log(String line) {
                log.info(line);
            }
        };
    }

    public static LoggerOutputStream info(final java.util.logging.Logger log, boolean skipEmptyLines) {
        return new LoggerOutputStream(skipEmptyLines) {
            @Override
            void log(String line) {
                log.info(line);
            }
        };
    }

    public static LoggerOutputStream error(Logger log) {
        return error(log, false);
    }

    public static LoggerOutputStream error(java.util.logging.Logger log) {
        return error(log, false);
    }


    public static LoggerOutputStream error(final Logger log, boolean skipEmptyLines) {
        return new LoggerOutputStream(skipEmptyLines) {
            @Override
            void log(String line) {
                log.error(line);
            }
        };
    }

    public static LoggerOutputStream error(final java.util.logging.Logger log, boolean skipEmptyLines) {
        return new LoggerOutputStream(skipEmptyLines) {
            @Override
            void log(String line) {
                log.severe(line);
            }
        };
    }

    public static LoggerOutputStream warn(Logger log) {
        return warn(log, false, null);
    }

    public static LoggerOutputStream warn(java.util.logging.Logger log) {
        return warn(log, false);
    }

    public static LoggerOutputStream warn(final Logger log, boolean skipEmptyLines) {
        return warn(log, skipEmptyLines, null);
    }

    public static LoggerOutputStream warn(final Logger log, boolean skipEmptyLines, final Integer max) {
        return new LoggerOutputStream(skipEmptyLines) {
            private int count = 0;
            @Override
            void log(String line) {
                if (max != null) {
                    count++;
                    if (count >  max) {
                        if (count == max + 1) {
                            log.warn("...");
                        }
                        return;
                    }
                }
                log.warn(line);
            }
        };
    }

    public static LoggerOutputStream warn(final java.util.logging.Logger log, boolean skipEmptyLines) {
        return new LoggerOutputStream(skipEmptyLines) {
            @Override
            void log(String line) {
                log.warning(line);
            }
        };
    }

	public static LoggerOutputStream debug(Logger log) {
		return debug(log, false);
	}

    public static LoggerOutputStream debug(java.util.logging.Logger log) {
        return debug(log, false);
    }



    public static LoggerOutputStream debug(final Logger log, boolean skipEmptyLines) {
        return new LoggerOutputStream(skipEmptyLines) {
            @Override
            void log(String line) {
                log.debug(line);
            }
        };
    }

    public static LoggerOutputStream debug(final java.util.logging.Logger log, boolean skipEmptyLines) {
        return new LoggerOutputStream(skipEmptyLines) {
            @Override
            void log(String line) {
                log.fine(line);
            }
        };
    }


    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    final boolean skipEmptyLines;
    int lastChar = -1;

    LoggerOutputStream(boolean skipEmptyLines) {
        this.skipEmptyLines = skipEmptyLines;
    }

    abstract void log(String line);


    @Override
    public void write(int b) {
        switch(b) {
            case '\n':
                log(skipEmptyLines);
                break;
            case '\r':
                if (lastChar != '\n') {
                    log(skipEmptyLines);
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
        log(true);
    }

    private void log(boolean skipEmpty) {
        String line = buffer.toString();
        if (!skipEmpty || ! (line == null || line.length() == 0)) {
            log(line);
        }
        buffer.reset();
    }
}
