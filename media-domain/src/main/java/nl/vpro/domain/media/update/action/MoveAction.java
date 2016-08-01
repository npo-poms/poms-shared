/**
 * Copyright (C) 2015 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update.action;

import javax.xml.bind.annotation.*;

/**
 * @author Roelof Jan Koekoek
 * @since 3.8
 */
@XmlRootElement(name = "move")
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "moveActionType",
    propOrder = {
        "from",
        "to"
    })
public class MoveAction {

    @XmlElement(required = true)
    private Integer from;

    @XmlElement(required = true)
    private Integer to;

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getTo() {
        return to;
    }

    public void setTo(Integer to) {
        this.to = to;
    }


}
