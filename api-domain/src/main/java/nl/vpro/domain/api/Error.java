/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.*;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@XmlType(name = "errorType", propOrder = {
    "message",
    "classes",
    "cause",
    "testResult"})
@XmlSeeAlso({AndPredicateTestResult.class, OrPredicateTestResult.class, NotPredicateTestResult.class})
@JsonPropertyOrder({"status", "message", "classes", "cause", "testResult"})
public class Error {

    @XmlAttribute
    @Getter
    private Integer status;

    @XmlElement
    @Getter

    private String message;

    @XmlElement
    @Getter
    private String cause;

    @XmlElement(name = "class")
    @JsonProperty("classes")
    @Getter
    private List<String> classes;

    @XmlElement
    @Getter

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
        this.classes = listOfClasses(t);
    }

    public Error(Response.Status status, Throwable t) {
        this.status = status.getStatusCode();
        this.message = t.getMessage();
        this.cause = ExceptionUtils.getStackTrace(t);
        this.classes = listOfClasses(t);
    }

    public Error(Integer status, String message, Throwable t) {
        this.status = status;
        this.message = message + " " + t.getMessage();
        this.cause = ExceptionUtils.getStackTrace(t);
        this.classes = listOfClasses(t);
    }

    public Error(Response.Status status, String message) {
        this.status = status.getStatusCode();
        this.message = message;
    }


    public void setTestResult(PredicateTestResult<?> testResult) {
        this.testResult = testResult;
    }

    void causeString(String string) {
        this.cause = string;
    }


    @Override
    public String toString() {
        return status + ":" + message;
    }

    private static List<String> listOfClasses(Throwable t) {
        List<String> result = new ArrayList<>();
        listOfClasses(t.getClass(), result);
        for (Class<?> c : ClassUtils.getAllInterfaces(t.getClass())) {
            result.add(c.getCanonicalName());
        }
        return result;

    }
    private static <T extends Throwable> void listOfClasses(Class<T> t, List<String> result) {
        if (t != null) {
            String cannonicalName = t.getCanonicalName();
            if (! result.contains(cannonicalName)) {
                result.add(cannonicalName);
                Class<?> superclass = t.getSuperclass();
                if (Throwable.class.isAssignableFrom(superclass)) {
                    listOfClasses((Class<T>) superclass, result);
                }

            }

        }

    }
}
