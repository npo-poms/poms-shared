package nl.vpro.domain.support;

import lombok.Getter;
import lombok.ToString;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.*;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;

import org.checkerframework.checker.nullness.qual.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.Xmlns;
import nl.vpro.i18n.Displayable;
import nl.vpro.validation.LicenseId;

/**
 * Image license information. Possible values are available as a list of static contants.
 * @author Michiel Meeuwissen
 * @since 5.0
 */

@ToString
@XmlType(name = "licenseEnum", namespace = Xmlns.SHARED_NAMESPACE)
@XmlAccessorType(XmlAccessType.NONE)
@JsonSerialize(using = License.Serializer.class)
@Embeddable
public class License implements Displayable, Serializable { // Not an enum, because that is hard for older clients!

    @Serial
    private static final long serialVersionUID = 0L;

    public static final License COPYRIGHTED = new License("Copyrighted", null);
    public static final License PUBLIC_DOMAIN = new License("Publiek domein", "https://creativecommons.org/share-your-work/public-domain/cc0/");
    public static final License CC_BY = new License("Naamsvermelding", "https://creativecommons.org/licenses/by/4.0/");
    @Deprecated
    public static final License CC_BY_1_0 = new License("Naamsvermelding_1.0", null);
    @Deprecated
    public static final License CC_BY_2_0 = new License("Naamsvermelding_2.0", null);
    @Deprecated
    public static final License CC_BY_3_0 = new License("Naamsvermelding_3.0", null);
    @Deprecated
    public static final License CC_BY_4_0 = new License("Naamsvermelding_4.0", "https://creativecommons.org/licenses/by/4.0/");
    public static final License CC_BY_SA = new License("Naamsvermelding-GelijkDelen", "https://creativecommons.org/licenses/by-sa/4.0/");
    @Deprecated
    public static final License CC_BY_SA_1_0 = new License("Naamsvermelding-GelijkDelen_1.0", null);
    @Deprecated
    public static final License CC_BY_SA_2_0 = new License("Naamsvermelding-GelijkDelen_2.0", null);
    @Deprecated
    public static final License CC_BY_SA_3_0 = new License("Naamsvermelding-GelijkDelen_3.0", null);
    @Deprecated
    public static final License CC_BY_SA_4_0 = new License("Naamsvermelding-GelijkDelen_4.0", "https://creativecommons.org/licenses/by-sa/4.0/");
    public static final License CC_BY_ND = new License("Naamsvermelding-GeenAfgeleideWerken", "https://creativecommons.org/licenses/by-nd/4.0/");
    @Deprecated
    public static final License CC_BY_ND_1_0 = new License("Naamsvermelding-GeenAfgeleideWerken_1.0", null);
    @Deprecated
    public static final License CC_BY_ND_2_0 = new License("Naamsvermelding-GeenAfgeleideWerken_2.0", null);
    @Deprecated
    public static final License CC_BY_ND_3_0 = new License("Naamsvermelding-GeenAfgeleideWerken_3.0", null);
    @Deprecated
    public static final License CC_BY_ND_4_0 = new License("Naamsvermelding-GeenAfgeleideWerken_4.0", "https://creativecommons.org/licenses/by-nd/4.0/");
    public static final License CC_BY_NC = new License("Naamsvermelding-NietCommercieel", "https://creativecommons.org/licenses/by-nc/4.0/");
    @Deprecated
    public static final License CC_BY_NC_1_0 = new License("Naamsvermelding-NietCommercieel_1.0", null);
    @Deprecated
    public static final License CC_BY_NC_2_0 = new License("Naamsvermelding-NietCommercieel_2.0", null);
    @Deprecated
    public static final License CC_BY_NC_3_0 = new License("Naamsvermelding-NietCommercieel_3.0", null);
    @Deprecated
    public static final License CC_BY_NC_4_0 = new License("Naamsvermelding-NietCommercieel_4.0", "https://creativecommons.org/licenses/by-nc/4.0/");
    public static final License CC_BY_NC_SA = new License("Naamsvermelding-NietCommercieel-GelijkDelen", "https://creativecommons.org/licenses/by-nc-sa/4.0/");
    @Deprecated
    public static final License CC_BY_NC_SA_1_0 = new License("Naamsvermelding-NietCommercieel-GelijkDelen_1.0", null);
    @Deprecated
    public static final License CC_BY_NC_SA_2_0 = new License("Naamsvermelding-NietCommercieel-GelijkDelen_2.0", null);
    @Deprecated
    public static final License CC_BY_NC_SA_3_0 = new License("Naamsvermelding-NietCommercieel-GelijkDelen_3.0", null);
    @Deprecated
    public static final License CC_BY_NC_SA_4_0 = new License("Naamsvermelding-NietCommercieel-GelijkDelen_4.0", "https://creativecommons.org/licenses/by-nc-sa/4.0/");
    public static final License CC_BY_NC_ND = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken", "https://creativecommons.org/licenses/by-nc-nd/4.0/");
    @Deprecated
    public static final License CC_BY_NC_ND_1_0 = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_1.0", "https://creativecommons.org/licenses/by-nc-nd/1.0/");
    @Deprecated
    public static final License CC_BY_NC_ND_2_0 = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_2.0", "https://creativecommons.org/licenses/by-nc-nd/2.0/");
    @Deprecated
    public static final License CC_BY_NC_ND_3_0 = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_3.0", "https://creativecommons.org/licenses/by-nc-nd/3.0/");
    @Deprecated
    public static final License CC_BY_NC_ND_4_0 = new License("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken_4.0", "https://creativecommons.org/licenses/by-nc-nd/4.0/");
    public static final License USA_GOV = new License("United States Government Work", "http://www.usa.gov/copyright.shtml");

    private static final License[] ALL;

    static {
        final List<License> alls = new ArrayList<>();
        for(Field field : License.class.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers()) && License.class.isAssignableFrom(field.getType())) {
                try {
                    License license = (License) field.get(null);
                    license.id = field.getName();
                    if (field.getName().endsWith("0")) {
                        license.display = false;
                    }
                    alls.add(license);
                } catch (Exception ignored ) {

                }
            }
        }
        ALL =  alls.toArray(new License[0]);
    }


//
//    CC_BY("Naamsvermelding", "https://creativecommons.org/licenses/by/4.0/"),
//    CC_BY_SA("Naamsvermelding-GelijkDelen", "https://creativecommons.org/licenses/by-sa/4.0/"),
//    CC_BY_ND("Naamsvermelding-GeenAfgeleideWerken", "https://creativecommons.org/licenses/by-nd/4.0/"),
//    CC_BY_NC("Naamsvermelding-NietCommercieel", "https://creativecommons.org/licenses/by-nc/4.0/"),
//    CC_BY_NC_SA("Naamsvermelding-NietCommercieel-GelijkDelen", "https://creativecommons.org/licenses/by-nc-sa/4.0/"),
//    CC_BY_NC_ND("Naamsvermelding-NietCommercieel-GeenAfgeleideWerken", ),
//    USA_GOV("United States Government Work",, false)

    @XmlValue
    @LicenseId
    @Column(name = "license")
    private String id;

    @NonNull
    @Transient
    private String displayName;

    @Getter
    @Transient
    private URI url;


    @Transient
    private boolean display = true;

    public License (){}


    /**
     * Poly null constructor.
     */
    public static @PolyNull License of(@PolyNull @LicenseId String id) {
        return id == null ? null : new License(id);
    }


    @JsonCreator
    public License(@LicenseId String id) {
        this.id = id;
    }


    License(@NonNull String displayName, String url) {
        this.displayName = displayName;
        this.url = url == null ? null : URI.create(url);
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @Override
    public String getDisplayName() {
        if (displayName == null && id != null) {
            License constant = valueOfOrNull(id);
            if (constant != null) {
                displayName = constant.getDisplayName();
            }
        }
        return displayName;
    }

    public static License valueOfOrNull(String id) {
        return Arrays.stream(values())
            .filter(l -> l.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public static License valueOf(String id) {
        return Arrays.stream(values())
            .filter(l -> l.getId().equals(id))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No such license " + id));
    }

    public static License[] values() {
        return ALL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        License license = (License) o;

        return id.equals(license.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }



    @Override
    public boolean display() {
        return display;
    }

    static class Serializer extends JsonSerializer<License> {

        @Override
        public void serialize(License license, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeString(license.getId());
        }
    }

}
