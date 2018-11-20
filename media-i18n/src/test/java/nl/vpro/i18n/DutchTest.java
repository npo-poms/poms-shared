package nl.vpro.i18n;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.9
 */
public class DutchTest {


    @Test
    public void formatInstantSmartly() {
        ZonedDateTime now = LocalDateTime.of(2018, 11, 16, 10, 0).atZone(Dutch.ZONE_ID);


        assertThat(Dutch.formatSmartly(now, LocalDateTime.of(2018, 11, 16, 13, 0).atZone(Dutch.ZONE_ID))).isEqualTo("13:00");

        assertThat(Dutch.formatSmartly(now, LocalDateTime.of(2018, 12, 16, 13, 0).atZone(Dutch.ZONE_ID))).isEqualTo("16 december 13:00");

        assertThat(Dutch.formatSmartly(now, LocalDateTime.of(2019, 12, 16, 13, 0).atZone(Dutch.ZONE_ID))).isEqualTo("16 december 2019 13:00");


    }

}
