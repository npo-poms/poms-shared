/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.media.Group;

@XmlRootElement(name = "group")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "episodes"
        })
public class GroupEditView extends MediaEditView {

    private long episodes;

    @XmlAttribute
    private boolean isOrdered;


    private GroupEditView() {
    }

    static GroupEditView create(Group fullGroup, long episodeCount) {
        GroupEditView simpleGroup = new GroupEditView();

        simpleGroup.episodes = episodeCount;
        simpleGroup.isOrdered = fullGroup.isOrdered();

        return simpleGroup;
    }

    public long getEpisodes() {
        return episodes;
    }

    public boolean isOrdered() {
        return isOrdered;
    }

    public void setOrdered(boolean ordered) {
        isOrdered = ordered;
    }

}
