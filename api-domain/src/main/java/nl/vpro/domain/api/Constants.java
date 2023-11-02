/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class Constants {

    private Constants() {

    }


    public static final int MAX_RESULTS = 500;

    public static final int DEFAULT_MAX_RESULTS = 10;

    public static final String DEFAULT_MAX_RESULTS_STRING = "" + DEFAULT_MAX_RESULTS;

    public static final int MAX_FACET_RESULTS = 24;


    public static final String OFFSET = "offset";
    public static final String OFFSET_MESSAGE = "An offset is supported. Default is 0. It cannot be very big, so it cannot be used to iterate over all results. Use iterate call (backed by a cursor) for that";
    public static final String ZERO = "0";
    public static final String MAX = "max";
    public static final String ASC = "asc";
    public static final String ORDER = "order";
    public static final String DELETED = "deleted";


    public static final String MAX_INTEGER = "" + Integer.MAX_VALUE;


    public static final String PROFILE = "profile";
    public static final String PROFILE_MESSAGE = "Limit the results only to this profile";


    public static final String PROPERTIES = "properties";
    public static final String PROPERTIES_NONE = "none";
    public static final String PROPERTIES_ALL = "all";
    public static final String PROPERTIES_MESSAGE = "Optimize media result for these returned properties";


}
