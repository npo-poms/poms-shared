package nl.vpro.domain.media.nebo.base;

import java.io.StringReader;
import java.time.Duration;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Segment;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class FragmentenTypeTest {


    @Test
    public void xml() {
        String example = """
            <fragment id="26088">
                        <titel>Generated Fragment 1</titel>
                        <starttijd>00:07:00</starttijd>
                        <eindtijd>00:09:00</eindtijd>
                        <wijzigingsdatum>2012-06-30 11:40:02</wijzigingsdatum>
                    </fragment>""";

        FragmentenType.Fragment fragment = JAXB.unmarshal(new StringReader(example), FragmentenType.Fragment.class);

        Segment segment = fragment.getSegment();
        assertThat(segment.getStart()).isEqualTo(Duration.ofMinutes(7));
        assertThat(segment.getDuration().get()).isEqualTo(Duration.ofMinutes(2));
    }
}
