package nl.vpro.domain.media.support;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

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
    CC_BY_NC_ND("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken"),
    CC_BY_1_0("Naamsvermelding_1.0"),
    CC_BY_2_0("Naamsvermelding_2.0"),
    CC_BY_3_0("Naamsvermelding_3.0"),
    CC_BY_4_0("Naamsvermelding_4.0"),
    CC_BY_SA_1_0("Naamsvermelding-GelijkDelen_1.0"),
    CC_BY_SA_2_0("Naamsvermelding-GelijkDelen_2.0"),
    CC_BY_SA_3_0("Naamsvermelding-GelijkDelen_3.0"),
    CC_BY_SA_4_0("Naamsvermelding-GelijkDelen_4.0"),
    CC_BY_ND_1_0("Naamsvermelding-GeenAfgeleideWerken_1.0"),
    CC_BY_ND_2_0("Naamsvermelding-GeenAfgeleideWerken_2.0"),
    CC_BY_ND_3_0("Naamsvermelding-GeenAfgeleideWerken_3.0"),
    CC_BY_ND_4_0("Naamsvermelding-GeenAfgeleideWerken_4.0"),
    CC_BY_NC_1_0("Naamsvermelding-NietCommercieel_1.0"),
    CC_BY_NC_2_0("Naamsvermelding-NietCommercieel_2.0"),
    CC_BY_NC_3_0("Naamsvermelding-NietCommercieel_3.0"),
    CC_BY_NC_4_0("Naamsvermelding-NietCommercieel_4.0"),
    CC_BY_NC_SA_1_0("Naamsvermelding-NietCommercieel-GelijkDelen_1.0"),
    CC_BY_NC_SA_2_0("Naamsvermelding-NietCommercieel-GelijkDelen_2.0"),
    CC_BY_NC_SA_3_0("Naamsvermelding-NietCommercieel-GelijkDelen_3.0"),
    CC_BY_NC_SA_4_0("Naamsvermelding-NietCommercieel-GelijkDelen_4.0"),
    CC_BY_NC_ND_1_0("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_1.0"),
    CC_BY_NC_ND_2_0("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_2.0"),
    CC_BY_NC_ND_3_0("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_3.0"),
    CC_BY_NC_ND_4_0("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_4.0"),
    USA_GOV("United States Government Work");


    String displayName;

    License(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public static License valueOfOrNull(String id) {
        if (StringUtils.isEmpty(id)) {
            return null;
        } else {
            return valueOf(id);
        }

    }

}
