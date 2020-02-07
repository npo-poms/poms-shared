package nl.vpro.domain.page;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.domain.Displayable;
import nl.vpro.validation.PathSegment;

/**
 * @author Michiel Meeuwissen
 * @since 2.0.
 */
@XmlType(name = "sectionType")
@XmlAccessorType(XmlAccessType.NONE)
@ToString(exclude = "portal")
@JsonPropertyOrder({"path", "id", "value"})
@AllArgsConstructor
@Builder
public class Section implements Displayable, Serializable {

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

    public static Section copy(Section section) {
        if (section == null) {
            return null;
        }
        return new Section(section.getPath(), section.getDisplayName());
    }

    @XmlAttribute
    @Pattern(regexp = "/.*")
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

    @JsonProperty("value")
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Portal getPortal() {
        return portal;
    }

    public void setPortal(Portal portal) {
        this.portal = portal;
    }

    @Pattern(regexp = "[A-Z]+\\./.*")
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
        return portal != null ? portal.equals(section.portal) : section.portal == null;
    }

    @Override
    public int hashCode() {
        int result = path.hashCode();
        result = 31 * result + (portal != null ? portal.hashCode() : 0);
        return result;
    }
}

