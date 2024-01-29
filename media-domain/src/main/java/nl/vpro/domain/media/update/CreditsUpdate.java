package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.ToString;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.xml.bind.annotation.XmlAttribute;

import nl.vpro.domain.media.*;

/**
 * @see nl.vpro.domain.media.update
 * @see Credits
 */
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
