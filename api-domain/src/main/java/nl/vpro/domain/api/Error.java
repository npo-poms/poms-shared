/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.constraint.AndPredicateTestResult;
import nl.vpro.domain.constraint.NotPredicateTestResult;
import nl.vpro.domain.constraint.OrPredicateTestResult;
import nl.vpro.domain.constraint.PredicateTestResult;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "error")
@XmlType(name = "errorType", propOrder = {"message", "cause", "testResult"})
@XmlSeeAlso({AndPredicateTestResult.class, OrPredicateTestResult.class, NotPredicateTestResult.class})
@JsonPropertyOrder({"status", "message", "cause", "testResult"})
public class Error {

    @XmlAttribute
    private Integer status;

    @XmlElement
    private String message;

    @XmlElement
    private String cause;

    @XmlElement
    private PredicateTestResult<?> testResult;

    public Error() {
    }

    public Error(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Error(Integer status, Throwable t) {
        this.status = status;
        this.message = t.getMessage();
        this.cause = ExceptionUtils.getStackTrace(t);
    }

    public Error(Response.Status status, Throwable t) {
        this.status = status.getStatusCode();
        this.message = t.getMessage();
        this.cause = ExceptionUtils.getStackTrace(t);
    }

    public Error(Integer status, String message, Throwable t) {
        this.status = status;
        this.message = message + " " + t.getMessage();
        this.cause = ExceptionUtils.getStackTrace(t);
    }

    public Error(Response.Status status, String message) {
        this.status = status.getStatusCode();
        this.message = message;
    }


    public Integer getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCause() {
        return cause;
    }

    public PredicateTestResult<?> getTestResult() {
        return testResult;
    }

    public void setTestResult(PredicateTestResult<?> testResult) {
        this.testResult = testResult;
    }

    @Override
    public String toString() {
        return status + ":" + message;
    }
}
