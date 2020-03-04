/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.user;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.*;
import javax.validation.Valid;
import javax.xml.bind.annotation.*;


@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable(true)
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Slf4j
public class Editor extends AbstractUser {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editor", fetch = FetchType.EAGER)
    @Valid
    @XmlTransient
    Set<BroadcasterEditor> broadcasters = new TreeSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editor", fetch = FetchType.EAGER)
    @Valid
    @XmlTransient
    protected Set<PortalEditor> portals = new TreeSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "editor", fetch = FetchType.EAGER)
    @Valid
    @XmlTransient
    @OrderBy("organization.id asc")
    protected Set<ThirdPartyEditor> thirdParties = new TreeSet<>();

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
    @XmlTransient
    private Supplier<Set<String>> rolesProvider = null;

    @Transient
    private Set<String> roles = null;

    @Version
    @Getter
    @XmlTransient
    protected Integer version;

    public Editor(Editor editor) {
        super(editor);
        this.broadcasters.addAll(editor.broadcasters);
        this.portals.addAll(editor.portals);
        this.thirdParties.addAll(editor.thirdParties);
        this.rolesProvider = editor.rolesProvider;
        this.roles = editor.roles;
        this.lastLogin = editor.lastLogin;
        this.loginCount = editor.loginCount;
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
        String familiyName,
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
        this.familyName = familiyName;
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

    public Set<String> getRoles() {
        if (roles == null && rolesProvider != null) {
            roles = rolesProvider.get();
            rolesProvider = null;
        }
        return this.roles;
    }

    public void provideRoles(Supplier<Set<String>> roles) {
        this.rolesProvider = roles;
        this.roles = null;
    }

    public boolean rolesLoaded() {
        return roles != null;
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

        boolean found = false;
        for(Iterator<BroadcasterEditor> iterator = broadcasters.iterator(); iterator.hasNext(); ) {
            BroadcasterEditor existing = iterator.next();

            if(toAdd != null && toAdd.equals(existing)) {
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

    boolean setActiveBroadcaster(String broadcasterId, boolean value) {
        return setActive(new Broadcaster(broadcasterId, broadcasterId), value);
    }

    boolean setActive(Broadcaster broadcaster, boolean value) {
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


    boolean addBroadcaster(Broadcaster broadcaster) {
        BroadcasterEditor toAdd = new BroadcasterEditor(this, broadcaster);
        if (broadcasters.add(toAdd)) {
            allowedBroadcasterCache = null;
            activeBroadcasterCache = null;
            return true;
        } else {
            return false;
        }
    }


    BroadcasterEditor removeBroadcaster(Broadcaster broadcaster) {
        BroadcasterEditor toRemove = remove(broadcasters, broadcaster);
        if (toRemove != null) {
            activeBroadcasterCache = null;
            allowedBroadcasterCache = null;
        }
        return toRemove;
    }

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

    boolean addPortal(Portal portal) {
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

    PortalEditor removePortal(Portal portal) {
        PortalEditor toRemove = remove(portals, portal);
        if (toRemove != null) {
            allowedPortalCache = null;
            activePortalCache = null;
        }
        return toRemove;
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

    boolean addThirdParty(ThirdParty thirdParty) {
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

    ThirdPartyEditor removeThirdParty(ThirdParty thirdParty) {
        ThirdPartyEditor toRemove = remove(thirdParties, thirdParty);
        if (toRemove != null) {
            allowedThirdPartyCache = null;
            activeThirdPartyCache = null;
        }
        return toRemove;
    }

    Collection<Organization> getOrganizations() {
        return Stream.concat(Stream.concat(getAllowedBroadcasters().stream(), getAllowedPortals().stream()), getAllowedThirdParties().stream()).collect(Collectors.toSet());
    }

    String getOrganization() {
        return getEmployer().getId();
    }


}
