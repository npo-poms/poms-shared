/*
 * Copyright (C) 2007/2008 All rights reserved
 * VPRO Omroepvereniging, The Netherlands
 * Creation date 18 sep 2008
 */

package nl.vpro.domain.image.support;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

/**
 * @author roekoe
 */
@SuppressWarnings("serial")
@MappedSuperclass
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(name = "domainType", namespace = Xmlns.IMAGE_NAMESPACE)
public abstract class AbstractDomainObject<T extends AbstractDomainObject> implements DomainObject<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    protected AbstractDomainObject() {
    }

    protected AbstractDomainObject(Long id) {
        this.id = id;
    }

    @Override
    @XmlTransient
    public Long getId() {
        return id;
    }

    /**
     * Under normal operation this should not be used!
     * <p/>
     * While testing it sometimes comes in handy to be able to set an Id to simulate
     * a persisted object.
     *
     * @param id
     */
    @Override
    public T setId(Long id) {
        this.id = id;
        return (T)this;
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

        if(!(object instanceof AbstractDomainObject)) {
            return false;
        }

        DomainObject domainObject = (DomainObject)object;

        if(this.getId() != null && domainObject.getId() != null) {
            return this.getId().equals(domainObject.getId());
        }

        return this == domainObject;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
