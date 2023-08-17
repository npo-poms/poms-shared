package nl.vpro.validation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import nl.vpro.domain.media.Location;

import static org.assertj.core.api.Assertions.assertThat;

public class LocationValidatorTest {
    LocationValidator validator = new LocationValidator();


    @ParameterizedTest
    @ValueSource(strings = {
        "http://cgi.omroep.nl/cgi-bin/streams?/tv/human/humandonderdag/bb.20040701.rm?title=",
        "sub+http://npo.npoplus.nl/video/MXF/VPWON_1245461",
        "mp3://moby.vpro.nl/data/pac01/mp3/17560686/17560686.mp3",
        "http://download.omroep.nl/vpro/algemeen/vpronl/radio/ovt/FRAGMENT%20OVT%20-%20WILHELMIENTJE%20LANS-GERRIT%20'34.mp3"
    })
    public void testValidation1(String programUrl) {
        assertThat(validator.isValid(programUrl, null)).isTrue();
        assertThat(Location.sanitizedProgramUrl(programUrl)).isEqualTo(programUrl);
    }



    @ParameterizedTest
    @ValueSource(strings = {
        "http://cgi.omroep.nl/cgi-bin/streams?/tv/human/humandonderdag/bb.20040701.rm?title=Wie eegie sanie - Onze eigen dingen",
        "mp3://moby.vpro.nl/data/pac01/mp3/17560686/17560686.mp3"
    }
    )
    public void sanitizable(String programUrl) {

        assertThat(validator.isValid(programUrl, null)).isFalse();
        String sanitized = Location.sanitizedProgramUrl(programUrl);
        assertThat(validator.isValid(sanitized, null)).isTrue();

    }


    @Test
    public void testValidationNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }


    @Test
    public void testValidationNotUrl() {
        String programUrl = "x";
        assertThat(validator.isValid(programUrl, null)).isFalse();
    }


    @Test
    public void testValidationEmptyString() {
        String programUrl = "";
        assertThat(validator.isValid(programUrl, null)).isFalse();
    }

    @Test
    public void testValidationContainsSpaces() {
        String programUrl = "https://download.omroep.nl/vpro/algemeen/3VOOR12/demachine/De Machine_AFL_90.mp3";
        assertThat(validator.isValid(programUrl, null)).isFalse();
    }


    @Test
    public void testValidation() {
        assertThat(validator.isValid("http://download.omroep.nl/secure/04435df317c88b95083691e5868bfaa6/52bea4ee/portal/radiomanager/hours_archive/radio5/2013/12/25/[Radio 5] Logging Radio 5 20131225 1000.mp3", null)).isFalse();
    }

    @Test
    public void testMid() {
        assertThat(validator.isValid("mid://vpro.nl/program/VPWON_1250088", null)).isTrue();
    }

    @Test
    @Disabled
    public void testAll() {
        List<String> invalid = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/programurls.txt")));
        reader.lines().forEach(line -> {
                if (!validator.isValid(line.trim(), null)) {
                    invalid.add(line);
                    System.out.println(line);
                }

            }
        );
        System.out.println(invalid.size());
        assertThat(invalid).isEmpty();
    }

}
