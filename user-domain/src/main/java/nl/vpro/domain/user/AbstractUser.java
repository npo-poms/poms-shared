/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.*;

import nl.vpro.domain.Identifiable;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AbstractUser implements Serializable, Identifiable<String>, User  {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "principalid")
    @Getter
    protected String principalId;

    @Getter
    @Setter
    protected String givenName;

    @Getter
    @Setter
    protected String familyName;

    @Column(nullable = false)
    @Getter
    @Setter
    protected String displayName;

    @Column(nullable = false)
    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])",
        flags = {Pattern.Flag.CASE_INSENSITIVE}
    )

    @Getter
    @Setter
    protected String email;

    /**
     * The number of times this user was reported as 'logged in' since the 5.12 release.
     */
    @Column
    @Getter
    protected Instant lastLogin;

    @Column(name = "creationDate", nullable = false)
    @Getter
    protected Instant creationInstant;

    @Column
    @Getter
    @Min(value = 0)
    protected Integer loginCount = 0;

    protected AbstractUser() {
    }


    protected AbstractUser(AbstractUser user) {
        this.principalId = user.principalId;
        this.givenName = user.givenName;
        this.familyName = user.familyName;
        this.displayName = user.displayName;
        this.email = user.email;
        this.lastLogin = user.lastLogin;
        this.loginCount = user.loginCount;
        this.creationInstant = user.creationInstant;
    }

    public AbstractUser(String principalId, String displayName, String email) {
        if (principalId == null) {
            principalId = email;
        }
        this.principalId = principalId == null  ? null : principalId.toLowerCase();
        if (displayName == null) {
            displayName = this.principalId;
        }
        this.displayName = displayName;
        this.email = email;
    }


    @Override
    @XmlAttribute
    public String getId() {
        return getPrincipalId();
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId.toLowerCase();
    }


    @Override
    public void setLastLogin(Instant lastLogin) {
        this.lastLogin = lastLogin;
        this.loginCount++;
    }

    @PrePersist
    public void preperist() {
        if (creationInstant == null) {
            this.creationInstant = Instant.now();
        }

    }
}
