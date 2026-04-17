package nl.npo.envelope;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import nl.npo.wonvpp.domain.CatalogEntry;
import nl.npo.wonvpp.domain.Utils;
import nl.vpro.jackson2.Jackson2Mapper;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
class NotifyEnvelopeTest {

    static String example = """
       {
"version": "1.0",
"timestamp": "2026-04-14T05:44:08.000Z",
"metadata": {
"fileName": "epg/catalog/20260414074323-CatalogEPG-AT_300022362-npo-svod.json"
},
"type": "notify",
"contents": "77u/W3sicHJpZCI6IkFUXzMwMDAyMjM2MiIsInB1YmxpY2F0aW9uVGltZXN0YW1wIjoiMjAyNi0wNC0xNFQwNzo0MzoyMy4zMjUrMDI6MDAiLCJtZWRpYVR5cGUiOiJ2aWRlbyIsImNvbnRlbnRUeXBlIjoiZXBpc29kZSIsInRpdGxlIjoiQnJ1Z2tsYXMiLCJkaXNwbGF5VGl0bGUiOiJCcnVna2xhcyIsIm9yaWdpbmFsVGl0bGUiOiJCcnVna2xhcyIsInN5bm9wc2lzIjp7InNob3J0IjoiVmlvbGV0IGlzIHZlcmJhYXNkIGFscyBrbGFzZ2Vub290IFl1cmkgbWV0IGhhYXIgbG9vcHQgdGUgZmxpcnRlbjogaGlqIGhlZWZ0IHRvY2ggZWVuIHZyaWVuZGluPyIsImxvbmciOiJWaW9sZXQgaXMgdmVyYmFhc2QgYWxzIGtsYXNnZW5vb3QgWXVyaSBtZXQgaGFhciBsb29wdCB0ZSBmbGlydGVuOiBoaWogaGVlZnQgdG9jaCBlZW4gdnJpZW5kaW4/In0sImxhbmd1YWdlcyI6W3sibGFuZ3VhZ2UiOiJOTCJ9XSwiaXNEdWJiZWQiOmZhbHNlLCJjYXB0aW9ucyI6W3siY2xvc2VkIjp0cnVlLCJsYW5ndWFnZSI6Ik5MIiwic3VwcGxlbWVudGFsIjp0cnVlfV0sImdlbnJlIjp7InByaW1hcnkiOiJKZXVnZCIsInNlY29uZGFyeSI6IlNlcmllIn0sInJhdGluZyI6eyJzeXN0ZW0iOiJOSUNBTSIsImFnZSI6IjYiLCJhZHZpc29yaWVzIjpbeyJnIjoiR2V3ZWxkIn0seyJ0IjoiR3JvZiB0YWFsZ2VicnVpayJ9XSwicGljdG9ncmFtcyI6IjJndCJ9LCJicm9hZGNhc3RlcnMiOlsiQVQiXSwicHJvZHVjdGlvbkNvdW50cnkiOiJOTEQiLCJwcm9kdWN0aW9uWWVhciI6MjAyNSwiZXBpc29kZU51bWJlciI6NTYsImNhc3RBbmRDcmV3IjpbeyJwZXJzb24iOnsiZ2l2ZW5OYW1lIjoiRXZhIiwiZmFtaWx5TmFtZSI6IkxleXNlbiIsImlkIjoyMDAxOTI2NjEwNTE3fSwiZnVuY3Rpb24iOiJEaXJlY3RvciJ9LHsicGVyc29uIjp7ImdpdmVuTmFtZSI6Ik5pY29saWVuIiwiZmFtaWx5TmFtZSI6Iktvb2wiLCJpZCI6MTI5MjkyNzg4MzUxN30sImZ1bmN0aW9uIjoiRGlyZWN0b3IifSx7InBlcnNvbiI6eyJnaXZlbk5hbWUiOiJRdWludGVuIiwiZmFtaWx5TmFtZSI6IlBvb2wiLCJpZCI6MjQ1NDc3MjY5MjUxN30sImZ1bmN0aW9uIjoiRGlyZWN0b3IifSx7InBlcnNvbiI6eyJnaXZlbk5hbWUiOiJKYWlkYWgiLCJmYW1pbHlOYW1lIjoiRGF3c29uIiwiaWQiOjI0MTc3MzYxNDE1MTd9LCJmdW5jdGlvbiI6IkFjdG9yIn0seyJwZXJzb24iOnsiZ2l2ZW5OYW1lIjoiU3RlbGxhIiwiZmFtaWx5TmFtZSI6Ik11bGxlciIsImlkIjoyNDAyMzQ4MDg5NTE3fSwiZnVuY3Rpb24iOiJBY3RvciJ9LHsicGVyc29uIjp7ImdpdmVuTmFtZSI6IkVyaWsiLCJmYW1pbHlOYW1lIjoiUGxhZ2VtYW4iLCJpZCI6MTUzMzg4MjkzMjUxN30sImZ1bmN0aW9uIjoiQWN0b3IifSx7InBlcnNvbiI6eyJnaXZlbk5hbWUiOiJLaWVyYW4iLCJmYW1pbHlOYW1lIjoiT3NlaS1Cb25zdSIsImlkIjoyNDI0NzE0MDc3NTE3fSwiZnVuY3Rpb24iOiJBY3RvciJ9LHsicGVyc29uIjp7ImdpdmVuTmFtZSI6IlNhbXlhIiwiZmFtaWx5TmFtZSI6IkhhZnNhb3VpIiwiaWQiOjE1MDUzMDY5ODc1MTd9LCJmdW5jdGlvbiI6IkFjdG9yIn1dLCJyZWxhdGlvbnMiOnsic2VyaWVzIjp7InByaWQiOiJBVF8yMDM1MTIyIn0sInNlYXNvbiI6eyJwcmlkIjoiQVRfMzAwMDIyMzA2In19LCJtZXRhZGF0YVNvdXJjZSI6IldPTlZQUCJ9LHsicHJpZCI6IkFUXzMwMDAyMjMwNiIsInB1YmxpY2F0aW9uVGltZXN0YW1wIjoiIiwibWVkaWFUeXBlIjoidmlkZW8iLCJjb250ZW50VHlwZSI6InNlYXNvbiIsInNlYXNvbk51bWJlciI6IjE0IiwidGl0bGUiOiJTZWl6b2VuIDE0IiwiZGlzcGxheVRpdGxlIjoiQnJ1Z2tsYXMgU2Vpem9lbiAxNCIsInN5bm9wc2lzIjp7InNob3J0IjoiIiwibG9uZyI6IkpldWdkc2VyaWUgd2FhcmluIGVlbiB2YXN0ZSBncm9lcCBsZWVybGluZ2VuIGdldm9sZ2Qgd29yZHQgdGlqZGVucyBoZXQgZWVyc3RlIGphYXIgdmFuIGRlIG1pZGRlbGJhcmUgc2Nob29sLiBWb29yIHNjaG9vbGdhYW5kZSBraW5kZXJlbiBtZXQgaGVya2VuYmFyZSBlbiBhYW5ncmlqcGVuZGUgdmVyaGFsZW4uIn0sInJlbGF0aW9ucyI6eyJzZXJpZXMiOnsicHJpZCI6IkFUXzIwMzUxMjIifX0sIm1ldGFkYXRhU291cmNlIjoiV09OVlBQIn0seyJwcmlkIjoiQVRfMjAzNTEyMiIsInB1YmxpY2F0aW9uVGltZXN0YW1wIjoiIiwibWVkaWFUeXBlIjoidmlkZW8iLCJjb250ZW50VHlwZSI6InNlcmllIiwidGl0bGUiOiJCcnVna2xhcyIsImRpc3BsYXlUaXRsZSI6IkJydWdrbGFzIiwic3lub3BzaXMiOnsic2hvcnQiOiIiLCJsb25nIjoiIn0sIm1ldGFkYXRhU291cmNlIjoiV09OVlBQIn1d"
}
""";

     @Test
     void test() throws IOException {
         NotifyEnvelope envelope = Jackson2Mapper.getInstance().readerFor(NotifyEnvelope.class).readValue(example);
         assertThat(envelope.version()).isEqualTo("1.0");
         assertThat(envelope.timestamp()).isEqualTo(Instant.parse("2026-04-14T05:44:08.000Z"));
         assertThat(envelope.metadata()).containsEntry("fileName", "epg/catalog/20260414074323-CatalogEPG-AT_300022362-npo-svod.json");
         assertThat(envelope.type()).isEqualTo("notify");
         assertThat(envelope.contents()).isNotBlank();
         byte[] bytes = envelope.bytes();
         assertThat(new String(bytes)).startsWith("[{");
         JsonNode json = envelope.json();
         log.info("{}", json);

         List<CatalogEntry> entries = envelope.unwrapJsonArray(Utils.createObjectMapper(), CatalogEntry.class);
         log.info("{}", entries);



     }

}
