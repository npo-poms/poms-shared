package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.user.Organization;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "id",
        "displayName",
        "isActive"
        })
public class OrganizationView {
    private String id;

    private String displayName;

    private boolean isActive;

    private OrganizationView() {
    }

    protected OrganizationView(String id, String displayName, boolean active) {
        this.id = id;
        this.displayName = displayName;
        this.isActive = active;
    }

    public static OrganizationView create(Organization organization, boolean active) {
        return new OrganizationView(
            organization.getId(),
            organization.getDisplayName(),
            active
        );
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
