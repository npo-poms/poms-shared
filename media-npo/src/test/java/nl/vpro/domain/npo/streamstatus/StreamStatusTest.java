package nl.vpro.domain.npo.streamstatus;

import lombok.extern.log4j.Log4j2;

import java.io.StringReader;

import javax.xml.bind.JAXB;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Log4j2
class StreamStatusTest {


    public static String[] xmls() {
        return new String[] {
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<streamstatus timestamp=\"2023-05-17T18:56:33\"><prid>VPWON_1349023</prid><channel>LI_NL2_4188105</channel><encryptie>DRM</encryptie><tijdsbeperking><starttijd>2023-05-17T18:56:29</starttijd><eindtijd>2023-05-17T19:21:38</eindtijd></tijdsbeperking><streamtype>live</streamtype><platform>extra</platform><status>online</status><profielen><profiel><versie>0</versie><protocol>dash</protocol><encryptie>cenc</encryptie></profiel><profiel><versie>0</versie><protocol>hls</protocol><encryptie>fairplay</encryptie></profiel><profiel><versie>0</versie><protocol>smooth</protocol><encryptie>playready</encryptie></profiel></profielen></streamstatus>"
        };
    }
    @ParameterizedTest
    @MethodSource("xmls")
    public void xml(String xml) {
        StreamStatus status = JAXB.unmarshal(new StringReader(xml), StreamStatus.class);
        log.info("{}", status);
    }
}
