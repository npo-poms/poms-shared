package nl.vpro.domain.page;


import lombok.*;

import java.io.Serial;
import java.io.Serializable;

import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import nl.vpro.i18n.Displayable;
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

    @Serial
    private static final long serialVersionUID = 669523707422621642L;

    @MonotonicNonNull
    @PathSegment
    private String path;

    @MonotonicNonNull
    private String displayName;

    private Portal portal;

    public Section() {
    }

    public Section(@NonNull String path, @NonNull String displayName) {
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

