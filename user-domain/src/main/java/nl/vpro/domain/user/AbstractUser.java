/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import nl.vpro.domain.Identifiable;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractUser implements Serializable, Identifiable<String>, User {
    private static final long serialVersionUID = 1L;

    @Id
    protected String principalId;

    protected String givenName;

    protected String familyName;

    @Column(nullable = false)
    protected String displayName;

    @Column(nullable = false)
    protected String email;

    @Column
    protected Instant lastLogin;

    protected AbstractUser() {
    }

    public AbstractUser(String principalId, String displayName, String email) {
        this.principalId = principalId.toLowerCase();
        this.displayName = displayName;
        this.email = email;
    }

    public AbstractUser(String principalId, String displayName, String email, String givenName, String familyName, Instant lastLogin) {
        this(principalId, displayName, email);
        this.givenName = givenName;
        this.familyName = familyName;
        this.lastLogin = lastLogin;
    }

    @Override
    @XmlAttribute
    public String getId() {
        return getPrincipalId();
    }

    @Override
    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId.toLowerCase();
    }

    @Override
    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    @Override
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Instant getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("User");
        sb.append("{principalId='").append(principalId).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
