/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import nl.vpro.domain.media.Segment;

@XmlRootElement(name = "segment")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "programId",
    "programTitle",
    "programAVType",
    "programDuration",
    "start"
})
public class SegmentEditView extends MediaEditView {

    private Long programId;

    private String programTitle;

    private String programAVType;

    private String programDuration;

    private String start;

    private SegmentEditView() {
    }

    static SegmentEditView create(Segment fullSegment) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        SegmentEditView simpleSegment = new SegmentEditView();

        simpleSegment.programId = fullSegment.getParent().getId();
        simpleSegment.programTitle = fullSegment.getParent().getMainTitle();
        simpleSegment.programAVType = fullSegment.getParent().getAVType().name();

        if(fullSegment.getParent().getDurationAsDate() != null) {
            simpleSegment.programDuration = sdf.format(fullSegment.getParent().getDurationAsDate());
        }

        if(fullSegment.getStart() != null) {
            simpleSegment.start = sdf.format(fullSegment.getStart());
        }

        return simpleSegment;
    }

    public long getProgramId() {
        return programId;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public String getProgramAVType() {
        return programAVType;
    }

    public String getProgramDuration() {
        return programDuration;
    }

    public String getStart() {
        return start;
    }
}
