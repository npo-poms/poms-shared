/*
 * Copyright (C) 2015 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update.action;

import lombok.Data;
import lombok.ToString;

import jakarta.xml.bind.annotation.*;

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
     * What the 'from' and 'to' properties of the actions are referring to.
     * @since 7.10.1
     */
    @XmlType(name = "moveActionTypeType")
    public enum Type {
        /**
         * A reference to a mediaobject. MID, crid, or id.
         */
        REFERENCE,
        /**
         * The number of the memberref
         * TODO: as yet supported
         */
        NUMBER,

        /**
         * The (offset 0) index in the corresponding list.
         */
        INDEX
    }

    @XmlAttribute(name = "type")
    Type type = null;


    public Type getEffectiveType() {
        return type == null ? Type.INDEX : type;
    }

    /**
     * The {@link #getType()} of the object to move from
     */
    @XmlElement(required = true)
    private String from;


    /**
     * The {@link #getType()} of the object to move from
     */
    @XmlElement(required = true)
    private String to;


}
