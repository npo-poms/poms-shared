/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.errors;

import nl.vpro.transfer.extjs.TransferList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlRootElement;
import java.lang.reflect.UndeclaredThrowableException;

@XmlRootElement(name = "errors")
public class ErrorList extends TransferList<ErrorView> {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorList.class);


    public ErrorList(String message) {
        this.success = false;
        this.results = 0;
        this.message = message;
        LOG.warn(this.message);
    }

    public ErrorList(Throwable e) {
        this.success = false;
        this.results = 0;
        if (e instanceof UndeclaredThrowableException) {
            e = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
        }
        this.message = e.getClass().getName() + " " + e.getMessage();
        LOG.warn(this.message, e);

    }
}
