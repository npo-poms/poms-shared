/*
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.upload;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.encoder.Job;
import nl.vpro.domain.encoder.JobResult;
import nl.vpro.transfer.extjs.ExtRecord;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "index",
    "jobId",
    "urn",
    "title",
    "profile",
    "location",
    "state",
    "progress"
})
public class JobResultView extends ExtRecord {
    private Integer index;

    private Long jobId;

    private String urn;

    private String mid;

    private String title;

    private String success;

    private String profile;

    private String state;

    private String broadcaster;

    private String message;

    private String location;

    private Float progress;

    private JobResultView() {
    }

    public static JobResultView create(int index, Job job, JobResult result) {
        JobResultView simpleJob = new JobResultView();

        simpleJob.index = index;
        simpleJob.jobId = result.getJobId();
        simpleJob.urn = job.getMediaId();
        simpleJob.mid = job.getMid();
        simpleJob.title = job.getTitle();
        simpleJob.success = "" + job.getState();
        if(result.getProfile() != null) {
            simpleJob.profile = result.getProfile().name();
        }
        simpleJob.state = "" + result.getState();
        simpleJob.broadcaster = job.getBroadcaster();
        simpleJob.message = result.getMessage();
        if(result.getLocation() != null) {
            simpleJob.location = result.getLocation().getProgramUrl().toString();
        }

/*
        if(job instanceof SaturnJob) {
            simpleJob.progress = ((SaturnJob)job).getProgress();
        }
*/

        return simpleJob;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public void setUrn(String urn) {
        this.urn = urn;
    }

    public String getUrn() {
        return urn;
    }

    public String getMid() {
        return mid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
