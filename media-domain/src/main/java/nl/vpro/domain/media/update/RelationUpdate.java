/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;


import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.media.Relation;
import nl.vpro.domain.media.RelationDefinition;


/**
 * @see nl.vpro.domain.media.update
 * @see Relation
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "relationUpdateType",
    propOrder = {
        "text"
        })
public class RelationUpdate implements Comparable<RelationUpdate> {

    @XmlAttribute(required = true)
    private String type;

    @XmlAttribute(required = true)
    private String broadcaster;

    @XmlAttribute
    private String uriRef;

    @XmlAttribute
    private String urn;

    @XmlValue
    private String text;

    public static RelationUpdate uri(RelationDefinition type, String uri) {
        return new RelationUpdate(type.getType(), type.getBroadcaster(), uri, null);
    }

    public static RelationUpdate text(RelationDefinition type, String text) {
        return new RelationUpdate(type.getType(), type.getBroadcaster(), null, text);
    }


    private RelationUpdate() {
    }

    public RelationUpdate(String type, String broadcaster) {
        this.type = type;
        this.broadcaster = broadcaster;
    }

    public RelationUpdate(String type, String broadcaster, String uriRef, String text) {
        this(type, broadcaster);
        this.uriRef = uriRef;
        this.text = text;
    }

    RelationUpdate(String type, String broadcaster, String uriRef, String text, String urn) {
        this(type, broadcaster);
        this.uriRef = uriRef;
        this.text = text;
        this.urn = urn;
    }

    public RelationUpdate(Relation relation) {
        this(relation.getType(), relation.getBroadcaster(), relation.getUriRef(), relation.getText(), relation.getUrn());
    }

    public Relation toRelation() {
        Relation relation = new Relation(
            new RelationDefinition(type, broadcaster),
            uriRef,
            text
        );
        relation.setUrn(urn);
        return relation;
    }

    public String getBroadcaster() {
        return broadcaster;
    }

    public void setBroadcaster(String broadcaster) {
        this.broadcaster = broadcaster;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUriRef() {
        return uriRef;
    }

    public void setUriRef(String uriRef) {
        this.uriRef = uriRef;
    }

    /**
     * The URN is the internal id of POMS.
     */
    public String getUrn() {
        return urn;
    }

    @Override
    public String toString() {
        return "RelationUpdate:" + broadcaster + ":" + type + ":" + (uriRef != null ? uriRef + ":" : "") + (text != null ? text : "");
    }

    @Override
    public int compareTo(@NonNull RelationUpdate relationUpdate) {
        if(broadcaster != null && relationUpdate.broadcaster != null && broadcaster.compareTo(relationUpdate.broadcaster) != 0) {
            return broadcaster.compareTo(relationUpdate.broadcaster);
        }

        if(type != null && relationUpdate.type != null && type.compareTo(relationUpdate.type) != 0) {
            return type.compareTo(relationUpdate.type);
        }

        if(uriRef != null && relationUpdate.uriRef != null && uriRef.compareTo(relationUpdate.uriRef) != 0) {
            return uriRef.compareTo(relationUpdate.uriRef);
        }


        if (urn != null && relationUpdate.urn != null) {
            if (urn.equals(relationUpdate.urn)) {
                return 0;
            }
        }

        if(text != null && relationUpdate.text != null && text.compareTo(relationUpdate.text) != 0) {
            return text.compareTo(relationUpdate.text);
        }

        return hashCode() - relationUpdate.hashCode();
    }
}
