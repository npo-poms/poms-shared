package nl.vpro.com.neovisionaries.i18n;

/**
 * @author Michiel Meeuwissen
 * @since 3.8
 */
public class Utils {

    public static CountryCode getByCode(String s) {
        CountryCode code = CountryCode.getByCode(s);
        if (code == null) {
            return VehicleRegistrationCode.valueOf(s).getCode();
        } else {
            return code;
        }

    }
}
