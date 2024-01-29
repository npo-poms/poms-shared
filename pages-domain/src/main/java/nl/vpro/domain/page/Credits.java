package nl.vpro.domain.page;


import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.*;

import nl.vpro.validation.URI;


/**
 * See NPA-534
 * @author Michiel Meeuwissen
 * @since 7.7
 */
@XmlType(name = "creditsType")
@XmlAccessorType(XmlAccessType.FIELD)
public class Credits {

    @XmlAttribute
    @Getter
    @Setter
    @URI(schemes = {"http", "https"}, mustHaveScheme = true, hosts = {"data.beeldengeluid.nl"})
    @Pattern(regexp = "http://data\\.beeldengeluid\\.nl/gtaa/\\d+")
    protected java.net.URI gtaaUri;

    @Column(nullable = false)
    @NotNull(message = "{nl.vpro.constraints.NotNull}")
    @XmlAttribute(required = true)
    @Enumerated(EnumType.STRING)
    @Getter
    @Setter
    protected RoleType role = RoleType.UNDEFINED;



}
