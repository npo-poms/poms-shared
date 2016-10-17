package nl.vpro.domain.page;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.support.Displayable;
import nl.vpro.validation.PathSegment;

/**
 * @author Michiel Meeuwissen
 * @since 2.0.
 */
@XmlType(name = "sectionType")
@XmlAccessorType(XmlAccessType.NONE)
public class Section implements Displayable {

    @NotNull
    @PathSegment
    private String path;

    @NotNull
    private String displayName;

    private Portal portal;

    public Section() {
    }

    public Section(String path, String displayName) {
        this.displayName = displayName;
        setPath(path);
    }

    @XmlAttribute
    @Pattern(regexp = "\\/.*")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @XmlValue
    @JsonProperty("value")
    @Override
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Portal getPortal() {
        return portal;
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    @Pattern(regexp = "[A-Z]+\\.\\/.*")
    @JsonProperty("id")
    public String getId() {
        return portal != null ? portal.getId() + "." + path : null;
    }

    public void setId(String id) {
        // ignored
    }

    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if(parent instanceof Portal) {
            this.portal = (Portal)parent;
        }
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Section section = (Section)o;

        if(!path.equals(section.path)) {
            return false;
        }
        if(portal != null ? !portal.equals(section.portal) : section.portal != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + (portal != null ? portal.hashCode() : 0);
        return result;
    }
}

