package nl.vpro.domain.media;

import static org.junit.Assert.*;

import java.util.Locale;

import javax.xml.bind.JAXB;

import org.junit.Test;

public class AvailableSubtitleTest {

	@Test
	public void test() {
		JAXB.marshal(new AvailableSubtitle(Locale.ENGLISH, "test"), System.out);
	}

}
