package nl.vpro.domain.image.support;

import lombok.Getter;

import java.net.URI;

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


    COPYRIGHTED("Copyrighted", null),
    PUBLIC_DOMAIN("Publiek domein", null),

    CC_BY("Naamsvermelding", "https://creativecommons.org/licenses/by/3.0/"),
    CC_BY_SA("Naamsvermelding-GelijkDelen", "https://creativecommons.org/licenses/by-sa/3.0/"),
    CC_BY_ND("Naamsvermelding-GeenAfgeleideWerken", "https://creativecommons.org/licenses/by-nd/3.0/"),
    CC_BY_NC("Naamsvermelding-NietCommercieel", "https://creativecommons.org/licenses/by-nc/3.0/"),
    CC_BY_NC_SA("Naamsvermelding-NietCommercieel-GelijkDelen", "https://creativecommons.org/licenses/by-nc-sa/3.0/"),
    CC_BY_NC_ND("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken", "https://creativecommons.org/licenses/by-nc-nd/3.0/");

    final String displayName;
    @Getter
    final URI url;


    License(String displayName, String url) {
        this.displayName = displayName;
        this.url = URI.create(url);
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
