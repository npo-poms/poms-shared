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
public class PlayEventTest {

    @Test
    public void marshal() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PlayEvent event = PlayEvent.newBuilder()
            .setTimestamp(new Date().getTime())
            .setBroadcasters(Arrays.<CharSequence>asList("VPRO"))
            .setClient("ik")
            .setMid("VPRO_1234")
            .setPlaytype(PlayType.PLAY)
            .setLabels(Arrays.<CharSequence>asList(""))
            .build();
        JsonEncoder encoder = EncoderFactory.get().jsonEncoder(PlayEvent.SCHEMA$, out);

        GenericDatumWriter<PlayEvent> w = new GenericDatumWriter<>(PlayEvent.SCHEMA$);
        w.write(event, encoder);
        encoder.flush();
        System.out.println(new String(out.toByteArray()));

        System.out.println(event.toString());


    }

    @Test
    public void unmarshall() throws IOException {
        String in = "{\"timestamp\": 1426154422954, \"mid\": \"VPRO_1234\", \"broadcasters\": [\"VPRO\"], \"client\": \"ik\", \"labels\": [\"\"], \"playtype\": \"PLAY\", \"duration\": -1, \"offset\": 0}";

        PlayEvent event2 = new ObjectMapper().readValue(new StringReader(in), PlayEvent.class);
        JsonDecoder decoder = DecoderFactory.get().jsonDecoder(PlayEvent.SCHEMA$, in);
        GenericDatumReader<PlayEvent> r = new GenericDatumReader<>(PlayEvent.SCHEMA$);
        PlayEvent event = new PlayEvent();
        r.read(event, decoder);

        assertEquals("VPRO", event.getBroadcasters().get(0));

    }
}
