package nl.vpro.domain.stats;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Date;

import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonDecoder;
import org.apache.avro.io.JsonEncoder;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class PageViewEventTest {

    @Test
    public void marshal() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PageViewEvent event = PageViewEvent.newBuilder()
            .setTimestamp(new Date().getTime())
            .setBroadcasters(Arrays.<CharSequence>asList("VPRO"))
            .setUrl("http://www.vpro.nl")
            .setPortal(new Portal("http://www.vpro.nl", "bla"))
            .setClient("ik")
            .setLabels(Arrays.<CharSequence>asList(""))
            .setId("hoi")
            .build();
        JsonEncoder encoder = EncoderFactory.get().jsonEncoder(PageViewEvent.SCHEMA$, out);

        GenericDatumWriter<PageViewEvent> w = new GenericDatumWriter<>(PageViewEvent.SCHEMA$);
        w.write(event, encoder);
        encoder.flush();
        System.out.println(new String(out.toByteArray()));
        System.out.println(event.toString());




    }
    @Test
    public void unmarshall() throws IOException {
        String in = "{\"id\": \"hoi\", \"timestamp\": 1426154379414, \"url\": \"http:\\/\\/www.vpro.nl\", \"broadcasters\": [\"VPRO\"], \"portal\": {\"url\": \"http:\\/\\/www.vpro.nl\", \"section\": \"bla\"}, \"client\": \"ik\", \"labels\": [\"\"], \"statusCode\": 200, \"size\": -1}";

        new ObjectMapper().readValue(new StringReader(in), PageViewEvent.class);
        JsonDecoder decoder = DecoderFactory.get().jsonDecoder(PageViewEvent.SCHEMA$, in);
        GenericDatumReader<PageViewEvent> r = new GenericDatumReader<>(PageViewEvent.SCHEMA$, Portal.SCHEMA$);
        PageViewEvent event = new PageViewEvent();
        r.read(event, decoder);

        assertEquals("VPRO", event.getBroadcasters().get(0));

    }
}
