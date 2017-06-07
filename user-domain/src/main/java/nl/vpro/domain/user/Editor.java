/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.*;
import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;


@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable(true)
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Slf4j
public class Editor extends AbstractUser {

    @OneToMany(cascade = CascadeType.ALL)
    @Valid
    @XmlTransient
    @JoinColumn(name = "organization_id")
    Set<BroadcasterEditor> broadcasters = new TreeSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_id")
    @Valid
    @XmlTransient
    Set<PortalEditor> portals = new TreeSet<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "organization_id")
    @Valid
    @XmlTransient
    @OrderBy("organization.id asc")
    Set<ThirdPartyEditor> thirdParties = new TreeSet<>();

    @Transient
    private SortedSet<Broadcaster> allowedBroadcasterCache;

    @Transient
    private SortedSet<Broadcaster> activeBroadcasterCache;

    @Transient
    private SortedSet<Portal> allowedPortalCache;

    @Transient
    private SortedSet<Portal> activePortalCache;

    @Transient
    private SortedSet<ThirdParty> allowedThirdPartyCache;

    @Transient
    private SortedSet<ThirdParty> activeThirdPartyCache;

    @Transient
    private Set<String> roles;

    protected Editor() {
    }

    public Editor(String principalId, String displayName, String email, Broadcaster broadcaster, Set<String> roles) {
        this(principalId, displayName, null, null, email, broadcaster, roles, null);
    }

    Editor(String principalId, String displayName, String email, Broadcaster broadcaster, String givenName, String familyName, Instant lastLogin) {
        this(principalId, displayName, givenName, familyName, email, broadcaster, Collections.emptySet(), lastLogin);
    }

    @Builder
    Editor(String principalId, String displayName, String givenName, String familiyName, String email, Broadcaster broadcaster, Set<String> roles, Instant lastLogin) {
        super(principalId, displayName, email);
        if (broadcaster != null) {
            broadcasters.add(new BroadcasterEditor(this, broadcaster, true));
        }
        if (roles == null) {
            log.warn("No roles for {}", principalId);
        }
        this.roles = roles == null ? Collections.emptySet() : Collections.unmodifiableSet(roles);
        this.givenName = givenName;
        this.familyName = familiyName;
        this.lastLogin = lastLogin;
    }

    public boolean hasEqualRights(Editor editor) {
        return editor != null
            &&
            Objects.equals(this.getPrincipalId(), editor.getPrincipalId()) &&
            Objects.equals(this.roles, editor.getRoles()) &&
            Objects.equals(this.getAllowedBroadcasters(), editor.getAllowedBroadcasters()) &&
            Objects.equals(this.getAllowedPortals(), editor.getAllowedPortals()) &&
            Objects.equals(this.getAllowedThirdParties(), editor.getAllowedThirdParties());
    }


    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return this.roles;
    }

    public Broadcaster getEmployer() {
        for(BroadcasterEditor rel : broadcasters) {
            if(rel.isEmployee()) {
                return rel.getOrganization();
            }
        }
        return null;
    }


    void setEmployer(final Broadcaster broadcaster) {
        BroadcasterEditor toAdd = broadcaster == null ? null : new BroadcasterEditor(this, broadcaster, true);

        for(Iterator<BroadcasterEditor> iterator = broadcasters.iterator(); iterator.hasNext(); ) {
            BroadcasterEditor existing = iterator.next();

            if(toAdd != null && toAdd.equals(existing)) {
                if(existing.isEmployee()) {
                    return;
                } else {
                    iterator.remove();
                }
            } else if(existing.isEmployee()) {
                iterator.remove();
            }
        }
        if (toAdd != null) {
            broadcasters.add(toAdd);
        }
        allowedBroadcasterCache = null;
    }

    SortedSet<Broadcaster> getAllowedBroadcasters() {
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

    SortedSet<Broadcaster> getActiveBroadcasters() {
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

    boolean isActive(Broadcaster broadcaster) {
        for (BroadcasterEditor be : broadcasters) {
            if (broadcaster.equals(be.getOrganization())) {
                return be.isActive();
            }
        }
        return false;
    }

    void setActiveBroadcaster(String broadcasterId, boolean value) {
        setActive(new Broadcaster(broadcasterId, broadcasterId), value);
    }

    void setActive(Broadcaster broadcaster, boolean value) {
        for (BroadcasterEditor be : broadcasters) {
            if (broadcaster.equals(be.getOrganization())) {
                be.setActive(value);
                activeBroadcasterCache = null;
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    void addBroadcaster(Broadcaster broadcaster) {
        BroadcasterEditor toAdd = new BroadcasterEditor(this, broadcaster);
        if (broadcasters.add(toAdd)) {
            allowedBroadcasterCache = null;
            activeBroadcasterCache = null;
        }
    }

    void removeBroadcaster(Broadcaster broadcaster) {
        BroadcasterEditor toRemove = new BroadcasterEditor(this, broadcaster);
        if (broadcasters.remove(toRemove)) {
            activeBroadcasterCache = null;
            allowedBroadcasterCache = null;
        }
    }

    SortedSet<Portal> getAllowedPortals() {
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

    void setActivePortal(String portalId, boolean value) {
        setActive(new Portal(portalId, null), value);
    }

    void setActive(Portal portal, boolean value) {
        for (PortalEditor be : portals) {
            if (portal.equals(be.getOrganization())) {
                be.setActive(value);
                activePortalCache= null;
                return;
            }
        }
        throw new IllegalArgumentException();
    }

    void addPortal(Portal portal) {
        if (portal == null) {
            log.warn("Cannot add null to {}", this);
            return;
        }
        PortalEditor toAdd = new PortalEditor(this, portal);
        if(portals.add(toAdd)) {
            allowedPortalCache = null;
            activePortalCache = null;
        }
    }

    void removePortal(Portal portal) {
        PortalEditor toRemove = new PortalEditor(this, portal);
        if (portals.remove(toRemove)) {
            allowedPortalCache = null;
            activePortalCache = null;
        }
    }

    SortedSet<ThirdParty> getAllowedThirdParties() {
        if(allowedThirdPartyCache == null) {
            allowedThirdPartyCache = new TreeSet<>();

            for(ThirdPartyEditor rel : thirdParties) {
                allowedThirdPartyCache.add(rel.getOrganization());
            }
            allowedThirdPartyCache = Collections.unmodifiableSortedSet(allowedThirdPartyCache);
        }
        return allowedThirdPartyCache;
    }

    void addThirdParty(ThirdParty thirdParty) {
        if (thirdParty == null) {
            log.warn("Cannot add null to {}", this);
            return;
        }
        ThirdPartyEditor toAdd = new ThirdPartyEditor(this, thirdParty);
        toAdd.setActive(true);
        if(thirdParties.add(toAdd)) {
            allowedThirdPartyCache = null;
            activeThirdPartyCache = null;
        }
    }

    void removeThirdParty(ThirdParty thirdParty) {
        ThirdPartyEditor toRemove = new ThirdPartyEditor(this, thirdParty);
        if (thirdParties.remove(toRemove)) {
            allowedThirdPartyCache = null;
            activeThirdPartyCache = null;
        }
    }

    Collection<Organization> getOrganizations() {
        return Stream.concat(Stream.concat(getAllowedBroadcasters().stream(), getAllowedPortals().stream()), getAllowedThirdParties().stream()).collect(Collectors.toSet());
    }

    String getOrganization() {
        return getEmployer().getId();
    }
}
