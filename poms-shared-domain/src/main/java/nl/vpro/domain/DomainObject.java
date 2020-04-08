/*
 * Copyright (C) 2007/2008 All rights reserved
 * VPRO, The Netherlands
 */

package nl.vpro.domain;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;


/**
 * A domain object is an {@link Identifiable} which serves a as a base class for the domain entities in the POMS universe that have their own id.
 *
 * @author roekoe
 */
@MappedSuperclass
@ToString
@XmlTransient
public abstract class DomainObject implements Identifiable<Long>, Serializable {

    @Id
    @SequenceGenerator(name = "hibernate_sequences", sequenceName = "hibernate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequences")
    @XmlTransient // Don't remove!
    @Getter
    protected Long id;

    @Transient
    @Getter
    private boolean persisted;

    public DomainObject() {
    }

    protected DomainObject(Long id) {
        this.id = id;
    }
    /**
     * Under normal operation this should not be used!
     * <p/>
     * While testing it sometimes comes in handy to be able to set an Id to simulate
     * a persisted object.
     */
    public DomainObject setId(Long id) {
        this.id = id;
        return this;
    }

    /**
     * Checks for database identity or object identity if one side of the comparison can
     * not supply a database identity. It is advised to override this method with a more
     * accurate test which should not rely on database identity. You can rely on this
     * criterion when equality can not be deducted programmatic and a real and final
     * check is in need of human interaction. In essence this check then states that two
     * objects are supposed to be different if they can't supply the same database Id.
     *
     * @param object the object to compare with
     * @return true if both objects are equal
     */
    @Override
    public boolean equals(Object object) {
        if(object == null) {
            return false;
        }

        if(this.getClass() != object.getClass()) {
            return false;
        }

        DomainObject that = (DomainObject)object;

        if(this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }

        return this == that;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @PostLoad
    @PostPersist
    void setPersisted() {
        persisted = true;
    }
}
