/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update.action;

import lombok.Data;
import lombok.ToString;

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
@ToString
@Data
public class MoveAction {

    /**
     * The id of the object to move from
     */
    @XmlElement(required = true)
    private Long from;


    /**
     * The id of the object to move to
     */
    @XmlElement(required = true)
    private Long to;


}
