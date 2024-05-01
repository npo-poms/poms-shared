/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.Identifiable;

@Getter
@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
@Slf4j
public abstract class AbstractUser implements Serializable, Identifiable<String>, User  {
    @Serial
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "principalid")
    @NonNull
    protected String principalId;

    @Setter
    protected String givenName;

    @Setter
    protected String familyName;

    @Column(nullable = false)
    @Setter
    protected String displayName;

    @Column(nullable = true)
    @Pattern(regexp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])",
        flags = {Pattern.Flag.CASE_INSENSITIVE}
    )

    @Setter
    protected String email;

    /**
     * The number of times this user was reported as 'logged in' since the 5.12 release.
     */
    @Column
    protected Instant lastLogin;

    @Column(name = "creationDate", nullable = false)
    protected Instant creationInstant;

    @Column
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

    public AbstractUser(String principalId, String displayName, final String email) {
        this.email = email;
        if (principalId == null) {
            principalId = email;
        }
        this.principalId = principalId == null  ? null/*in some test cases*/ : principalId.toLowerCase();


        if (displayName == null) {
            displayName = this.principalId;
        }
        this.displayName = displayName;
        if (this.principalId == null) {
            log.warn("Create user without principal id: {}", this);
        }
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
        if (!Objects.equals(this.lastLogin, lastLogin)) {
            this.loginCount++;
            if (this.lastLogin != null) {
                log.debug("{} {} -> {} ({})", getPrincipalId(), this.lastLogin, lastLogin, this.loginCount);
            }
            this.lastLogin = lastLogin;
        } else {
            log.debug("lastlogin already is {}. this is not a new login", lastLogin);
        }
    }

    @PrePersist
    public void prepersist() {
        if (creationInstant == null) {
            this.creationInstant = Instant.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractUser that = (AbstractUser) o;

        return principalId.equals(that.principalId);
    }

    @Override
    public int hashCode() {
        return principalId.hashCode();
    }
}
