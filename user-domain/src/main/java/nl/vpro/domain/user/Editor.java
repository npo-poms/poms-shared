/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import nl.vpro.domain.Accountable;


/**
 * <p>
 * An {@link Editor} is the entity that is used in POMS (and related system) to contain information about the user or
 * 'process' that is performing the actions (e.g. the person who is logged in in the GUI) . It contains authentication and authorization information (like {@link #getRoles()} , {@link #getEmployer()} and {@link #getAllowedBroadcasters()}) about this person or user.
 *</p>
 * <p>
 * In the poms database it is the database entity that is linked to objects to indicate meta data like {@link Accountable#getCreatedBy()} and {@link Accountable#getLastModifiedBy()}
 * </p>
 * <p>
 * It serves as the main information source to determin whether certain actions are permitted or not (e.g. implemented in a  <code>nl.vpro.spring.security.acl.MediaPermissionEvaluator</code>
 *</p>
 */
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable(true)
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Slf4j
public class Editor extends AbstractUser {

    @Serial
    private static final long serialVersionUID = -4381169912123229285L;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editor")
    @Valid
    @XmlTransient
    Set<BroadcasterEditor> broadcasters = new TreeSet<>();


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editor")
    @Valid
    @XmlTransient
    protected Set<PortalEditor> portals = new TreeSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editor")
    @Valid
    @XmlTransient
    @OrderBy("organization.id asc")
    protected Set<ThirdPartyEditor> thirdParties = new TreeSet<>();

    @Transient
    @Nullable
    private SortedSet<Broadcaster> allowedBroadcasterCache;

    @Transient
    @Nullable
    private SortedSet<Broadcaster> activeBroadcasterCache;

    @Transient
    @Nullable
    private SortedSet<Portal> allowedPortalCache;

    @Transient
    @Nullable
    private SortedSet<Portal> activePortalCache;

    @Transient
    @Nullable
    private SortedSet<ThirdParty> allowedThirdPartyCache;

    @Transient
    @Nullable
    private SortedSet<ThirdParty> activeThirdPartyCache;

    @Transient
    @XmlTransient
    @Nullable
    private Supplier<Set<String>> rolesProvider = null;


    @Transient
    @MonotonicNonNull
    private Set<String> roles = null;

    //@Version
    @Getter
    @XmlTransient
    protected int version = 0;

    public Editor(Editor editor) {
        super(editor);
        this.broadcasters.addAll(editor.broadcasters);
        this.portals.addAll(editor.portals);
        this.thirdParties.addAll(editor.thirdParties);
        this.rolesProvider = editor.rolesProvider;
        this.roles = editor.roles;
        this.lastLogin = editor.lastLogin;
        this.loginCount = editor.loginCount;
        this.version = editor.version;
    }

    protected Editor() {
    }

    public Editor(String principalId, String displayName, String email, Broadcaster broadcaster, Set<String> roles) {
        this(principalId, displayName, null, null, email, broadcaster, roles, null);
    }

    Editor(String principalId, String displayName, String email, Broadcaster broadcaster, String givenName, String familyName, Instant lastLogin) {
        this(principalId, displayName, givenName, familyName, email, broadcaster, Collections.emptySet(), lastLogin);
    }

    @lombok.Builder(builderClassName = "Builder")
    Editor(
        String principalId,
        String displayName,
        String givenName,
        String familyName,
        String email,
        Broadcaster broadcaster,
        @lombok.Singular  Set<String> roles,
        Instant lastLogin
    ) {
        super(principalId, displayName, email);
        if (broadcaster != null) {
            broadcasters.add(new BroadcasterEditor(this, broadcaster, true));
        }
        if (roles == null) {
            log.warn("No roles for {}", this.principalId);
        }
        this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(roles);
        this.givenName = givenName;
        this.familyName = familyName;
        this.lastLogin = lastLogin;
    }

    public Editor copy() {
        return new Editor(this);
    }

    public boolean hasEqualRights(Editor editor) {
        return editor != null
            &&
            Objects.equals(this.getPrincipalId(), editor.getPrincipalId()) &&
            Objects.equals(this.getRoles(), editor.getRoles()) &&
            Objects.equals(this.getAllowedBroadcasters(), editor.getAllowedBroadcasters()) &&
            Objects.equals(this.getAllowedPortals(), editor.getAllowedPortals()) &&
            Objects.equals(this.getAllowedThirdParties(), editor.getAllowedThirdParties());
    }


    public void setRoles(Set<String> roles) {
        if (roles == null && this.rolesProvider != null) {
            log.warn("Setting roles to null!");
        }
        this.roles = roles;
        this.rolesProvider = null;
    }

    /**
     * @see nl.vpro.domain.Roles
     */
    public Set<String> getRoles() {
        if (roles == null && rolesProvider != null) {
            roles = rolesProvider.get();
            rolesProvider = null;
        }
        if (roles == null) {
            throw new IllegalStateException("This user object does not have role information");
        }
        return this.roles;
    }

    public void supplyRoles(Supplier<Set<String>> roles) {
        this.rolesProvider = roles;
        this.roles = null;
    }
    public void supplyRolesIfNeeded(Supplier<Set<String>> roles) {
        if (this.rolesProvider == null && this.roles == null) {
            supplyRoles(roles);
        }
    }

    public boolean rolesLoaded() {
        return roles != null;
    }

    @Nullable
    public Broadcaster getEmployer() {
        for(BroadcasterEditor rel : broadcasters) {
            if(rel.isEmployee()) {
                return rel.getOrganization();
            }
        }
        return null;
    }


    public void setEmployer(final Broadcaster broadcaster) {
        BroadcasterEditor toAdd = broadcaster == null ? null : new BroadcasterEditor(this, broadcaster, true);

        boolean found = false;
        for (BroadcasterEditor existing : broadcasters) {
            if (toAdd != null && toAdd.equals(existing)) {
                found = true;
                existing.setEmployee(true);
            } else {
                existing.setEmployee(false);
            }
        }
        if (! found && toAdd != null) {
            broadcasters.add(toAdd);
        }
        allowedBroadcasterCache = null;
    }



    public SortedSet<Broadcaster> getAllowedBroadcasters() {
        if(allowedBroadcasterCache == null) {
            allowedBroadcasterCache = new TreeSet<>();

            Broadcaster broadcaster = getEmployer();
            if(broadcaster != null) {
                allowedBroadcasterCache.add(broadcaster);
            }

            for(BroadcasterEditor rel : broadcasters) {
                final Broadcaster organization = rel.getOrganization();
                if(organization != null) {
                    allowedBroadcasterCache.add(organization);
                }
            }
            allowedBroadcasterCache = Collections.unmodifiableSortedSet(allowedBroadcasterCache);
        }
        return allowedBroadcasterCache;
    }

    public SortedSet<Broadcaster> getActiveBroadcasters() {
        if(activeBroadcasterCache == null) {
            activeBroadcasterCache = new TreeSet<>();

            for(BroadcasterEditor rel : broadcasters) {
                if(rel.isActive()) {
                    final Broadcaster organization = rel.getOrganization();
                    if(organization != null) {
                        activeBroadcasterCache.add(organization);
                    }
                }
            }
            activeBroadcasterCache = Collections.unmodifiableSortedSet(activeBroadcasterCache);
        }
        return activeBroadcasterCache;
    }

    public boolean isActive(Broadcaster broadcaster) {
        for (BroadcasterEditor be : broadcasters) {
            if (broadcaster.equals(be.getOrganization())) {
                return be.isActive();
            }
        }
        return false;
    }

    boolean setActiveBroadcaster(String broadcasterId, boolean value) {
        return setActive(new Broadcaster(broadcasterId, broadcasterId), value);
    }

    public boolean setActive(Broadcaster broadcaster, boolean value) {
        for (BroadcasterEditor be : broadcasters) {
            if (broadcaster.equals(be.getOrganization())) {
                if (be.active != value) {
                    be.setActive(value);
                    activeBroadcasterCache = null;
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (value) {
            throw new IllegalArgumentException("No broadcaster " + broadcaster + " found in " + broadcasters);
        } else {
            // nothing to do
            return false;
        }

    }


    public boolean addBroadcaster(Broadcaster broadcaster) {
        BroadcasterEditor toAdd = new BroadcasterEditor(this, broadcaster);
        if (broadcasters.add(toAdd)) {
            allowedBroadcasterCache = null;
            activeBroadcasterCache = null;
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public BroadcasterEditor removeBroadcaster(Broadcaster broadcaster) {
        BroadcasterEditor toRemove = remove(broadcasters, broadcaster);
        if (toRemove != null) {
            activeBroadcasterCache = null;
            allowedBroadcasterCache = null;
        }
        return toRemove;
    }

    @Nullable
    private static <S extends Organization, T extends OrganizationEditor<S>> T remove(Collection<T> collection, S organization) {
        T toRemove = null;
        for (T e : collection) {
            if (e.getOrganization().equals(organization)) {
                toRemove = e;
                break;
            }
        }

        if (toRemove != null) {
            collection.remove(toRemove);
        }
        return toRemove;
    }

    /**
     * Returns the set of Portals this editor is a member of.
     */
    public SortedSet<Portal> getAllowedPortals() {
        if(allowedPortalCache== null) {
            allowedPortalCache = new TreeSet<>();

            for(PortalEditor rel : portals) {
                if (rel.getOrganization() != null) {
                    allowedPortalCache.add(rel.getOrganization());
                }
            }
            allowedPortalCache = Collections.unmodifiableSortedSet(allowedPortalCache);
        }
        return allowedPortalCache;
    }

    /**
     * Returns the set of Portals this editor currenly configure to be 'active'
     * This mains that new objects will receive this portal on default.
     */
    SortedSet<Portal> getActivePortals() {
        if(activePortalCache == null) {
            activePortalCache = new TreeSet<>();

            for(PortalEditor rel : portals) {
                if(rel.isActive()) {
                    activePortalCache.add(rel.getOrganization());
                }
            }
            activePortalCache = Collections.unmodifiableSortedSet(activePortalCache);
        }
        return activePortalCache;
    }

    boolean isActive(Portal portal) {
        for (PortalEditor be : portals) {
            if (portal.equals(be.getOrganization())) {
                return be.isActive();
            }
        }
        return false;
    }

    boolean setActivePortal(String portalId, boolean value) {
        return setActive(new Portal(portalId, null), value);
    }

    boolean setActive(Portal portal, boolean value) {
        for (PortalEditor be : portals) {
            if (portal.equals(be.getOrganization())) {
                if (be.isActive() != value) {
                    be.setActive(value);
                    activePortalCache = null;
                    return true;
                } else {
                    return false;
                }
            }
        }
        if (value) {
            throw new IllegalArgumentException();
        } else {
            return false;
        }
    }

    public boolean addPortal(Portal portal) {
        if (portal == null) {
            log.warn("Cannot add null to {}", this);
            return false;
        }
        PortalEditor toAdd = new PortalEditor(this, portal);
        if(portals.add(toAdd)) {
            allowedPortalCache = null;
            activePortalCache = null;
            return true;
        }
        return false;
    }

    @Nullable
    public PortalEditor removePortal(Portal portal) {
        PortalEditor toRemove = remove(portals, portal);
        if (toRemove != null) {
            allowedPortalCache = null;
            activePortalCache = null;
        }
        return toRemove;
    }


    /**
     * Returns the set of {@link ThirdParty}s this editor is a member of.
     */
    public SortedSet<ThirdParty> getAllowedThirdParties() {
        if(allowedThirdPartyCache == null) {
            allowedThirdPartyCache = new TreeSet<>();

            for(ThirdPartyEditor rel : thirdParties) {
                allowedThirdPartyCache.add(rel.getOrganization());
            }
            allowedThirdPartyCache = Collections.unmodifiableSortedSet(allowedThirdPartyCache);
        }
        return allowedThirdPartyCache;
    }

    public boolean addThirdParty(ThirdParty thirdParty) {
        if (thirdParty == null) {
            log.warn("Cannot add null to {}", this);
            return false;
        }
        ThirdPartyEditor toAdd = new ThirdPartyEditor(this, thirdParty);
        toAdd.setActive(true);
        if(thirdParties.add(toAdd)) {
            allowedThirdPartyCache = null;
            activeThirdPartyCache = null;
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public ThirdPartyEditor removeThirdParty(ThirdParty thirdParty) {
        ThirdPartyEditor toRemove = remove(thirdParties, thirdParty);
        if (toRemove != null) {
            allowedThirdPartyCache = null;
            activeThirdPartyCache = null;
        }
        return toRemove;
    }

    Collection<Organization> getOrganizations() {
        return Stream.concat(
            Stream.concat(
                getAllowedBroadcasters().stream(),
                getAllowedPortals().stream()),
            getAllowedThirdParties().stream())
            .collect(Collectors.toSet());
    }

    void addOrganization(Organization organization) {
        if (organization instanceof Broadcaster) {
            addBroadcaster((Broadcaster) organization);
        } else if (organization instanceof Portal) {
            addPortal((Portal) organization);
        } else if (organization instanceof ThirdParty) {
            addThirdParty((ThirdParty) organization);
        } else {
            throw new IllegalArgumentException("Unknown organization type: " + organization);
        }
    }

    @Nullable
    String getOrganization() {
        Broadcaster b = getEmployer();
        return b == null ? null : b.getId();
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Editor");
        sb.append("{principalId='").append(principalId).append('\'');
        sb.append(", displayName='").append(displayName).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @PreUpdate
    protected void preupdate() {
        this.version++;
    }




    public  static Optional<String> getDomain(Editor editor) {
        return getDomain(editor.getEmail());
    }

    public  static Optional<String> getDomain(@Nullable String mail) {
        if (mail == null) {
            return Optional.empty();
        }
        int i = mail.indexOf('@');
        if (i > 0) {
            return Optional.of(mail.substring(i + 1));
        } else {
            return Optional.empty();
        }
    }



}
