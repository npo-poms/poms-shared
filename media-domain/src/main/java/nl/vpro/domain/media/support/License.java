package nl.vpro.domain.media.support;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.Xmlns;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
@XmlEnum
@XmlType(name = "licenseEnum", namespace = Xmlns.SHARED_NAMESPACE)
public enum License implements nl.vpro.domain.Displayable {


    COPYRIGHTED("Copyrighted"),
    PUBLIC_DOMAIN("Publiek domein"),

    CC_BY("Naamsvermelding"),
    CC_BY_SA("Naamsvermelding-GelijkDelen"),
    CC_BY_ND("Naamsvermelding-GeenAfgeleideWerken"),
    CC_BY_NC("Naamsvermelding-NietCommercieel"),
    CC_BY_NC_SA("Naamsvermelding-NietCommercieel"),
    CC_BY_NC_ND("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken");

    String displayName;

    License(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

}
