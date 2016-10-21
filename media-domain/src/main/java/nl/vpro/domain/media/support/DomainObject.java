/*
 * Copyright (C) 2007/2008 All rights reserved
 * VPRO, The Netherlands
 */

package nl.vpro.domain.media.support;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.Xmlns;

/**
 * @author roekoe
 */
@MappedSuperclass
@XmlType(name = "domainObjectType", namespace = Xmlns.SHARED_NAMESPACE)
@XmlAccessorType(XmlAccessType.FIELD)
public interface DomainObject extends Identifiable<Long>, Serializable {

    DomainObject setId(Long id);

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
    boolean equals(Object object);


    /*
    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }

        if (this.getClass() != object.getClass()) {
            return false;
        }

        DomainObject that = (DomainObject) object;

        if (this.getId() != null && that.getId() != null) {
            return this.getId().equals(that.getId());
        }

        return this == that;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .toString();
    }*/
}
