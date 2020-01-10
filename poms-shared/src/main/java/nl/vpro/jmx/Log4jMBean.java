/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.jmx;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @deprecated moved to vpro-shared
 */
@ManagedResource(
        objectName="nl.vpro:name=logging",
        description="Logging configuration.",
        log=true,
        logFile="jmx.log"
)
@Deprecated
public class Log4jMBean {

    @ManagedOperation(description="Get current level for category")
    public String getLevel(String category) {
        return LogManager.getLogger(category).getLevel().toString();
    }

    @ManagedOperation(description="Level trace")
    public void trace(String category) {
        LogManager.getLogger(category).setLevel(Level.TRACE);
    }

    @ManagedOperation(description="Level debug")
    public void debug(String category) {
        LogManager.getLogger(category).setLevel(Level.DEBUG);
    }

    @ManagedOperation(description="Level info")
    public void info(String category) {
        LogManager.getLogger(category).setLevel(Level.INFO);
    }

    @ManagedOperation(description="Level warn")
    public void warn(String category) {
        LogManager.getLogger(category).setLevel(Level.WARN);
    }

    @ManagedOperation(description="Level error")
    public void error(String category) {
        LogManager.getLogger(category).setLevel(Level.ERROR);
    }

    @ManagedOperation(description="Level fatal")
    public void fatal(String category) {
        LogManager.getLogger(category).setLevel(Level.FATAL);
    }
}
