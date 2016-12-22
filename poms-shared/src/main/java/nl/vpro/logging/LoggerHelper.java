/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.logging;

import org.slf4j.Logger;
import org.slf4j.ext.LoggerWrapper;

/**
 * A logger wrapper that add an time stamp to every trace message.
 */

public final class LoggerHelper extends LoggerWrapper {


    public LoggerHelper(Logger logger) {
        super(logger, logger.getName());
    }

    public static void trace(Logger logger, String message, Object... args) {
        if (logger.isTraceEnabled()) {
            logger.trace(String.format("%1$tT.%1$tL - %2$s", System.currentTimeMillis(), message), args);
        }
    }

    @Override
    public void trace(String message, Object... args) {
        LoggerHelper.trace(logger, message, args);
    }

    @Override
    public void trace(String message, Object arg) {
        LoggerHelper.trace(logger, message, arg);
    }
}
