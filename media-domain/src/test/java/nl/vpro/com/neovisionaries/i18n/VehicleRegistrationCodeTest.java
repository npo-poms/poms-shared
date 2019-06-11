package nl.vpro.com.neovisionaries.i18n;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.meeuw.i18n.VehicleRegistrationCode;

public class VehicleRegistrationCodeTest {


    @Test
    public void values() {
        List<String > differences = new ArrayList<>();
        for (VehicleRegistrationCode code : VehicleRegistrationCode.values()) {

            if (! code.getName().equals(code.getCode() == null ? null : code.getCode().getName())) {
                differences.add(code.getName() + " " + (code.getCode() == null ? null : code.getCode().getName()));
            }
        }
        System.out.println(StringUtils.join(differences, "\n"));
    }

}
