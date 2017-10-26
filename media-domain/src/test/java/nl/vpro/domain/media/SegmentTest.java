package nl.vpro.domain.media;

import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class SegmentTest {


	@Test
	public void sortDate() throws Exception {
		Program program = MediaBuilder.program()
				.creationDate(LocalDateTime.of(2017, 10, 24, 0, 0))
				.build();
		Segment segment = MediaBuilder.segment()
				.creationDate(LocalDateTime.of(2017, 10, 25, 0, 0))
				.mainTitle("bla")
				.duration(Duration.ofSeconds(123))
				.parent(program)
				.build();

		assertThat(segment.getSortDate()).isEqualTo(program.getCreationDate());

		assertThat(
				JAXBTestUtil.roundTripAndSimilar(segment, "<segment type=\"SEGMENT\" embeddable=\"true\" sortDate=\"2017-10-24T00:00:00+02:00\" workflow=\"FOR PUBLICATION\" creationDate=\"2017-10-25T00:00:00+02:00\" xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\">\n" +
						"    <title owner=\"BROADCASTER\" type=\"MAIN\">bla</title>\n" +
						"    <duration>P0DT0H2M3.000S</duration>\n" +
						"    <credits/>\n" +
						"    <descendantOf/>\n" +
						"    <locations/>\n" +
						"    <scheduleEvents/>\n" +
						"    <images/>\n" +
						"</segment>").getSortDate()
		).isEqualTo(program.getCreationDate());

		assertThat(
				Jackson2TestUtil.roundTripAndSimilar(segment, "{\n" +
						"  \"objectType\" : \"segment\",\n" +
						"  \"type\" : \"SEGMENT\",\n" +
						"  \"workflow\" : \"FOR_PUBLICATION\",\n" +
						"  \"sortDate\" : 1508796000000,\n" +
						"  \"creationDate\" : 1508882400000,\n" +
						"  \"embeddable\" : true,\n" +
						"  \"broadcasters\" : [ ],\n" +
						"  \"titles\" : [ {\n" +
						"    \"value\" : \"bla\",\n" +
						"    \"owner\" : \"BROADCASTER\",\n" +
						"    \"type\" : \"MAIN\"\n" +
						"  } ],\n" +
						"  \"genres\" : [ ],\n" +
						"  \"countries\" : [ ],\n" +
						"  \"languages\" : [ ],\n" +
						"  \"duration\" : 123000,\n" +
						"  \"descendantOf\" : [ { } ]\n" +
						"}").getSortDate()
		).isEqualTo(program.getSortDate());
	}


}
