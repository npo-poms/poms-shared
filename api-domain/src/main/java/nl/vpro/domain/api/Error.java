/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.Response;
import jakarta.xml.bind.annotation.*;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

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
    "violations",
    "testResult"
})
@JsonPropertyOrder({"status", "message", "classes", "cause", "violations", "testResult"})
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

    @XmlAnyElement(lax = true)
    @Getter
    @Setter
    List<?> violations;

    @Getter
    @Setter
    @XmlElement
    private PredicateTestResult testResult;


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
        this.classes = listOfClasses(t, false);
    }

    public Error(Response.Status status, Throwable t) {
        this(status, t, true, false);
    }

    public Error(Response.Status status, Throwable t, boolean withCause, boolean concise) {
        this.status = status.getStatusCode();
        this.message = t.getMessage();
        this.cause = withCause ? ExceptionUtils.getStackTrace(t) : null;
        this.classes = listOfClasses(t, concise);
    }

    public Error(Integer status, String message, Throwable t) {
        this.status = status;
        this.message = message + " " + t.getMessage();
        this.cause = ExceptionUtils.getStackTrace(t);
        this.classes = listOfClasses(t, false);
    }

    public Error(Response.Status status, String message) {
        this.status = status.getStatusCode();
        this.message = message;
    }

    void causeString(String string) {
        this.cause = string;
    }


    @Override
    public String toString() {
        return status + ":" + message;
    }

    private static List<String> listOfClasses(Throwable t, boolean concise) {
        List<String> result = new ArrayList<>();
        listOfClasses(t.getClass(), result, concise);
        for (Class<?> c : ClassUtils.getAllInterfaces(t.getClass())) {
            if (c.equals(Serializable.class)) {
                continue;
            }
            result.add(className(c, concise));
        }
        return result;

    }
    private static <T extends Throwable> void listOfClasses(Class<T> t, List<String> result, boolean concise) {
        if (t != null) {
            String name = className(t, concise);
            if (! result.contains(name)) {
                result.add(name);
                Class<?> superclass = t.getSuperclass();
                if ((superclass.equals(Exception.class) || superclass.equals(RuntimeException.class)) && concise) {
                    return;
                }
                if (Throwable.class.isAssignableFrom(superclass)) {
                    listOfClasses((Class<T>) superclass, result, concise);
                }

            }
        }
    }

    private static String className(Class<?> c, boolean concise) {
        if (concise) {
            return c.getSimpleName();
        } else {
            return c.getCanonicalName();
        }
    }
}
