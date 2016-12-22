/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import javax.ws.rs.core.MediaType;

/**
 * See https://jira.vpro.nl/browse/API-
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class Constants {


    public static final String VTT = "text/vtt";
    public static final MediaType VTT_TYPE = new MediaType("text", "vtt");

    public static final String SRT = "text/srt";
    public static final MediaType SRT_TYPE = new MediaType("text", "srt");

    public static final String EBU = "text/ebu-tt";
    public static final MediaType EBU_TYPE = new MediaType("text", "ebu-tt");

    public static final int MAX_RESULTS = 240;

    public static final int DEFAULT_MAX_RESULTS = 10;

    public static final String DEFAULT_MAX_RESULTS_STRING = "" + DEFAULT_MAX_RESULTS;

    public static final int MAX_FACET_RESULTS = 24;


    public static final String OFFSET = "offset";
    public static final String ZERO = "0";
    public static final String MAX = "max";
    public static final String ASC = "asc";
    public static final String ORDER = "order";

    public static final String MAX_INTEGER = "" + Integer.MAX_VALUE;


    public static final String PROFILE = "profile";
    public static final String PROFILE_MESSAGE = "Limit the results only to this profile";


    public static final String PROPERTIES = "properties";
    public static final String PROPERTIES_NONE = "none";
    public static final String PROPERTIES_ALL = "all";
    public static final String PROPERTIES_MESSAGE = "Optimize media result for these returned properties";


}
