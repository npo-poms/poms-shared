package nl.vpro.domain.page.update;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;

import nl.vpro.domain.page.Portal;
import nl.vpro.domain.page.Section;
import nl.vpro.domain.page.validation.ValidPortal;
import nl.vpro.domain.user.PortalService;
import nl.vpro.domain.user.ServiceLocator;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@XmlType(name = "portalUpdateType", propOrder = {"section"})
@XmlAccessorType(XmlAccessType.NONE)
@Slf4j
@AllArgsConstructor
@Builder
public class PortalUpdate implements Serializable{

    @Serial
    private static final long serialVersionUID = 1528575304102260572L;

    @ValidPortal
    @NotNull
    private String id;

    @NotNull
    private String url;

    @Valid
    private Section section;

    public PortalUpdate() {

    }
    public PortalUpdate(String id, String url) {
        this.id = id;
        this.url = url;
    }

    public static PortalUpdate of(Portal portal) {
        if (portal == null) {
            return null;
        }
        return new PortalUpdate(portal.getId(), portal.getUrl());
    }

	@XmlAttribute(required = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(required = true)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement
    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public Portal toPortal() {
        PortalService portalService = ServiceLocator.getPortalService();
        nl.vpro.domain.user.Portal userPortal;
        if (portalService != null) {
            userPortal = portalService.find(getId());
            if (userPortal == null) {
                log.warn("Could not find portal " + getId() + " in " + portalService);
            }
        } else {
            log.warn("No portalService found!");
            userPortal = null;
        }
        Portal portal = new Portal(getId(), getUrl(), userPortal == null ? getId() : userPortal.getDisplayName());
        portal.setSection(Section.copy(getSection()));
        return portal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PortalUpdate that = (PortalUpdate) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (section != null ? !section.equals(that.section) : that.section != null) return false;
        return url != null ? url.equals(that.url) : that.url == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (section != null ? section.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return id + ":" + url;
    }
}
