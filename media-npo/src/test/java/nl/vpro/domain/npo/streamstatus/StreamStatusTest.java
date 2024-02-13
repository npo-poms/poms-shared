package nl.vpro.domain.npo.streamstatus;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import jakarta.validation.ValidatorFactory;
import jakarta.xml.bind.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

@Log4j2
class StreamStatusTest {


    public static List<String> xmls() throws IOException {

        try (BufferedReader i = new BufferedReader(new InputStreamReader(StreamStatusTest.class.getResourceAsStream("/streamstatus/streamstati")))) {
            return i.lines().map(l -> l.split(":", 2)[1]).collect(Collectors.toList());
        }
    }

    static final Unmarshaller CONTEXT;
    static final Validator VALIDATOR;
    static {
        Unmarshaller c;
        Validator v;
        try {
            c = JAXBContext.newInstance(StreamStatus.class).createUnmarshaller();
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            v = factory.getValidator();
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            c = null;
            v = null;
        }
        CONTEXT = c;
        VALIDATOR = v;
    }
    @ParameterizedTest
    @MethodSource("xmls")
    public void xml(String xml) throws JAXBException {


        StreamStatus status = (StreamStatus) CONTEXT.unmarshal(new StringReader(xml));
        log.info("{}\n->{}", xml,status);
        assertThat(VALIDATOR.validate(status)).isEmpty();

    }
}
