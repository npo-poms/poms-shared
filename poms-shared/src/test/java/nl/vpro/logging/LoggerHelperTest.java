/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.logging;

import java.io.StringWriter;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.fest.assertions.Assertions.assertThat;

public class LoggerHelperTest {
    private static final Logger log = LoggerFactory.getLogger(LoggerHelperTest.class);

    @Test
    public void trace() throws Exception {
        final StringWriter writer = new StringWriter();

        org.apache.log4j.Logger rootLogger = LogManager.getRootLogger();
        rootLogger.removeAllAppenders();
        rootLogger.addAppender(new WriterAppender(new SimpleLayout(), writer));
        rootLogger.setLevel(Level.TRACE);

        LoggerHelper helper = new LoggerHelper(log);
        helper.trace("message {}", "argument");

        assertThat("123:bbb - ").matches("^[0-9]{3}:[b]{3} \\- $");
        assertThat(writer.toString()).matches("^TRACE \\- [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3} \\- message argument\\n$");

        LoggerHelper.trace(log, "message {}", "argument");
        assertThat(writer.toString()).matches("^TRACE \\- [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3} \\- message argument\\nTRACE \\- [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3} \\- message argument\\n$");
    }
}
