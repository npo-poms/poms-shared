package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.xml.bind.annotation.XmlAttribute;

import nl.vpro.domain.media.Credits;
import nl.vpro.domain.media.RoleType;

@ToString(of = { "role" })
public abstract class CreditsUpdate {

    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    protected RoleType role;

    public CreditsUpdate(RoleType role) {
        this.role = role;
    }

    protected CreditsUpdate() {
        // needed for jaxb
    }

    public abstract Credits toCredits();
}
