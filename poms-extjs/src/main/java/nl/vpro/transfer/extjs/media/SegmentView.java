/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Segment;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.transfer.extjs.ExtRecord;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "id",
    "mid",
    "workflow",
    "title",
    "description",
    "start",
    "duration",
    "stop",
    "images"
})
public class SegmentView extends ExtRecord {
    public static final String TIME_FORMAT = "HH:mm:ss.SSS";

    private Long id;

    private String mid;

    private String title;

    private String description;

    private String start;

    private String duration;

    private String stop; // Stop time is not stored in the database, it only exists for ExtJS convenience

    private List<ImageView> images = new ArrayList<ImageView>();

    private Workflow workflow;


    private SegmentView() {
    }

    private SegmentView(Long id, String mid, Workflow workflow, String title, String description,
                        String start, String duration, String stop, List<ImageView> images) {
        this.id = id;
        this.mid = mid;
        this.workflow = workflow;
        this.title = title;
        this.description = description;
        this.start = start;
        this.duration = duration;
        this.stop = stop;
        this.images = images;
    }

    public static SegmentView create(Segment fullSegment) {
        Long id = fullSegment.getId();
        String title = fullSegment.getMainTitle();
        String description = fullSegment.getMainDescription();

        Date start = fullSegment.getStart();
        Date duration = (fullSegment.getDurationAsDate() != null) ? fullSegment.getDurationAsDate() : new Date(0);
        Date stop = new Date(start.getTime() + duration.getTime());

        List<ImageView> images = new ArrayList<ImageView>();
        int index = 0;
        for(Image image : fullSegment.getImages()) {
            images.add(ImageView.create(image, index));
            index++;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        return new SegmentView(id, fullSegment.getMid(), fullSegment.getWorkflow(), title, description, sdf.format(start), sdf.format(duration), sdf.format(stop), images);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public Date getStartDateValue() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            return sdf.parse(start);
        } catch(ParseException e) {
            throw new RuntimeException("Error parsing date string.",
                e.getCause());
        }
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Date getDurationDateValue() {
        SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            return sdf.parse(duration);
        } catch(ParseException e) {
            throw new RuntimeException("Error parsing date string.",
                e.getCause());
        }
    }

    public String getStop() {
        return stop;
    }

    public void setStop() {
        // Stop time is not stored in the database, it only exists for ExtJS convenience
    }

    public List<ImageView> getImages() {
        return images;
    }

    public void setImages(List<ImageView> images) {
        this.images = images;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }
}
