package nl.vpro.domain.support;

import lombok.Getter;

import java.net.URI;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.Xmlns;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 * @TODO Licenses have no support for versions. I now suggested 4.0 for the the CC-licenses, but other version may occur too!
 *       Perhaps License should not be enum, or have many more values.
 *       The trouble with enum is any way that java clients are hard to keep in sync.
 */
@XmlEnum
@XmlType(name = "licenseEnum", namespace = Xmlns.SHARED_NAMESPACE)
public enum License implements nl.vpro.domain.Displayable {


    COPYRIGHTED("Copyrighted", null, true),
    PUBLIC_DOMAIN("Publiek domein", null, true),

    CC_BY("Naamsvermelding", "https://creativecommons.org/licenses/by/4.0/", true),
    CC_BY_SA("Naamsvermelding-GelijkDelen", "https://creativecommons.org/licenses/by-sa/4.0/", true),
    CC_BY_ND("Naamsvermelding-GeenAfgeleideWerken", "https://creativecommons.org/licenses/by-nd/4.0/", true),
    CC_BY_NC("Naamsvermelding-NietCommercieel", "https://creativecommons.org/licenses/by-nc/4.0/", true),
    CC_BY_NC_SA("Naamsvermelding-NietCommercieel-GelijkDelen", "https://creativecommons.org/licenses/by-nc-sa/4.0/", true),
    CC_BY_NC_ND("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken", "https://creativecommons.org/licenses/by-nc-nd/4.0/", true),
    USA_GOV("United States Government Work", "http://www.usa.gov/copyright.shtml", false)

    ;

    final String displayName;
    @Getter
    final URI url;

    @Getter
    final boolean listed;


    License(String displayName, String url, boolean listed) {
        this.displayName = displayName;
        this.url = url == null ? null : URI.create(url);
        this.listed = listed;
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
