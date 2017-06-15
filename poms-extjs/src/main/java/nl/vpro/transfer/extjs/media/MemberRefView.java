/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.MemberRef;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.ExtRecord;
import nl.vpro.transfer.extjs.media.support.MediaTypeView;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "id",
    "referenceId",
    "mid",
    "workflow",
    "mediaType",
    "broadcasters",
    "title",
    "subtitle",
    "number",
    "added",
    "highlighted"
})
public class MemberRefView extends ExtRecord {

    private Long id;

    private Long referenceId;

    private String mid;

    private String workflow;

    private MediaTypeView mediaType;

    private String title;

    private String subtitle;

    private Integer number;

    private Date added;

    private Boolean highlighted;

    private List<String> broadcasters =  new ArrayList<>();

    private MemberRefView() {
    }

    public static MemberRefView create(MediaPermissionEvaluator permissionEvaluator, MemberRef memberRef) {
        MemberRefView result = new MemberRefView();
        MediaObject owner = memberRef.getOwner();

        result.setId(memberRef.getId());
        result.setMid(memberRef.getMidRef());
        result.setWorkflow(owner.getWorkflow().name());
        result.setMediaType(MediaTypeView.create(permissionEvaluator, MediaType.getMediaType(owner)));
        result.setReferenceId(owner.getId());
        result.setTitle(owner.getMainTitle());
        result.setSubtitle(owner.getSubTitle());
        result.setNumber(memberRef.getNumber());
        result.setAdded(memberRef.getAdded());
        result.setHighlighted(memberRef.isHighlighted());

        for(Broadcaster broadcaster : owner.getBroadcasters()) {
            result.addBroadcasters(broadcaster.getDisplayName());
        }

        return result;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public MediaTypeView getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaTypeView mediaType) {
        this.mediaType = mediaType;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    public List<String> getBroadcasters() {
        return broadcasters;
    }

    private void addBroadcasters(String broadcaster) {
        this.broadcasters.add(broadcaster);
    }

    public void setBroadcasters(List<String> broadcasters) {
        this.broadcasters = broadcasters;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public Integer getNumber() {
        if(number == null || number < 1) {
            return null;
        }
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getAdded() {
        return added;
    }

    public void setAdded(Date added) {
        this.added = added;
    }

    public Boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }
}
