package nl.vpro.berlijn.util.kafka;

import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/**
 * Support for format that Intellij kafka plugin that produces dumps in this odd format.
 *<p>
 * Tab separated (with a header?). No escaping. The last field has no newlines, so it can be split on newline then.
 */
@Log4j2
public class KafkaDumpReader {

    // pretty much absurd
    public static DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);


    public static Stream<Record> read(InputStream inputStream) {
        var  reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        var  scanner = new Scanner(reader);

        // skip header
        //log.debug(Arrays.asList(readRecord(scanner, NUMBER_OF_FIELDS_IN_RECORD)));

        return StreamSupport.stream(new Spliterator<>() {
            @Override
            public int characteristics() {
                return Spliterator.DISTINCT;
            }
            @Override
            public long estimateSize() {
                return Integer.MAX_VALUE;
            }
            @Override
            public boolean tryAdvance(Consumer<? super Record> tr) {
                if (scanner.hasNext()) {
                    String[] record = readRecord(scanner, NUMBER_OF_FIELDS_IN_RECORD);
                    Instant timestamp;
                    try {
                        timestamp = ZonedDateTime.parse(record[0].trim(), TIMESTAMP_FORMAT).toInstant();
                    } catch (DateTimeParseException wtf) {
                        log.error("Could not parse {}: {}", record[0], wtf
                            .getMessage());
                        timestamp = Instant.now();
                    }
                    tr.accept(new Record(
                        timestamp,
                        record[1],
                        record[2],
                        Integer.parseInt(record[3]),
                        Long.parseLong(record[4])
                    ));
                    return true;
                } else {
                    return false;
                }

            }

            @Override
            public Spliterator<Record> trySplit() {
                return null;
            }
        }, false);
    }


    public static String[] readRecord(Scanner scanner, int fields) {
        var result = new String[fields];
        scanner.useDelimiter("\t");
        for (int i = 0 ; i < fields -1; i++) {
            result[i] = scanner.next().trim();
        }
        scanner.useDelimiter("[\n\r]");
        result[fields - 1] = scanner.next().trim();
        return result;
    }


    static final int NUMBER_OF_FIELDS_IN_RECORD = Stream.of(Record.class.getDeclaredFields()).filter(f -> !Modifier.isStatic(f.getModifiers())).toList().size();
    static {
        assert NUMBER_OF_FIELDS_IN_RECORD == 5; // If assertion fails, someone changed Record class. And parsing in #read will fail.
    }

    public record Record(Instant timeStamp, String key, String value, int partition, long offset) {


        public byte[] bytes() {
            return value.getBytes(StandardCharsets.UTF_8);
        }

    }
}
