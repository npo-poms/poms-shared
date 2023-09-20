/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.api.rs.subtitles;

import javax.ws.rs.core.MediaType;

/**

 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class Constants {

    private Constants() {
    }

    public static final String VTT_CHARSET = "UTF-8";
    public static final String VTT = "text/vtt";
    public static final String VTT_WITH_CHARSET = VTT + "; charset=" + VTT_CHARSET;

    public static final MediaType VTT_TYPE = new MediaType("text", "vtt").withCharset(VTT_CHARSET);

    public static final String SRT_CHARSET = "cp1252";
    public static final String SRT = "text/srt";
    public static final String SRT_WITH_CHARSET = SRT + "; charset=" + SRT_CHARSET;
    public static final MediaType SRT_TYPE = new MediaType("text", "srt").withCharset(SRT_CHARSET);

    public static final String TT888_CHARSET = "ISO6937";
    public static final String TT888 = "text/tt888";
    public static final String TT888_WITH_CHARSET = TT888 + "; charset=" + TT888_CHARSET;
    public static final MediaType TT888_TYPE = new MediaType("text", "tt888").withCharset(TT888_CHARSET);

    public static final String EBU = "application/ebu-stl";
    public static final MediaType EBU_TYPE = new MediaType("application", "ebu-stl");


    public static final String XML = "application/xml";
    public static final MediaType XML_TYPE = new MediaType("application", "xml");

}
