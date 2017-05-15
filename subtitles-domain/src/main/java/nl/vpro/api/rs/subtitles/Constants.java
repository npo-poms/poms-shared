/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.subtitles;

import javax.ws.rs.core.MediaType;

/**

 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class Constants {


    public static final String VTT = "text/vtt";
    public static final MediaType VTT_TYPE = new MediaType("text", "vtt");

    public static final String SRT = "text/srt";
    public static final MediaType SRT_TYPE = new MediaType("text", "srt");

    public static final String TT888 = "text/tt88";
    public static final MediaType TT888_TYPE = new MediaType("text", "tt888");

    public static final String EBU = "application/ebu-stl";
    public static final MediaType EBU_TYPE = new MediaType("application", "ebu-stl");


}
