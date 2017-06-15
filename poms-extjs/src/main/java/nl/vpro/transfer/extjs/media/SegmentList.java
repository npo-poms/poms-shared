/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.SortedSet;

import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.Segment;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "segments")
public class SegmentList extends TransferList<SegmentView> {

    public SegmentList() {
    }

    public SegmentList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static SegmentList create(Program program) {
        SortedSet<Segment> fullList = program.getSegments();

        SegmentList simpleList = new SegmentList();
        simpleList.success = true;

        if(fullList == null) {
            return simpleList;
        }

        for(Segment segment : fullList) {
            simpleList.add(SegmentView.create(segment));
        }

        return simpleList;
    }
}
