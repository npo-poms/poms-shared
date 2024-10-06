package nl.vpro.berlijn.domain.productmetadata;

import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Stream;

import jakarta.validation.ConstraintViolation;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import nl.vpro.berlijn.domain.*;
import nl.vpro.berlijn.util.kafka.KafkaDumpReader;
import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.update.Validation;
import nl.vpro.domain.subtitles.SubtitlesProvider;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Log4j2
class ProductMetadataTest {
    static  Random random = new Random();

    static {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }
    public static Parser parser = Parser.getInstance();



    public static Stream<ProductMetadata> messages() {
        return KafkaDumpReader
            .read(Util.getTable("/productmetadata/product-metadata.table"))
            .map(KafkaDumpReader.Record::bytes)
            .map(parser::parseProductMetadata)
            //.sorted(PomsMapper.randomOrder(random))
            .limit(1000)
            ;

    }



    BroadcasterService broadcasterService =  mock(BroadcasterService.class);

    {
        when(broadcasterService.findForIds(any()))
            .thenAnswer(new Answer<Optional<Broadcaster>>() {

                @Override
                public Optional<Broadcaster> answer(InvocationOnMock invocationOnMock) {
                    String wid = invocationOnMock.getArgument(0);
                    String id = StringUtils.left(wid, 4);
                    return Optional.of(new Broadcaster(id, wid, wid, null, null));
                }
            });
    }

    SubtitlesProvider subtitlesService = (m) -> new ArrayList<>();
    PomsMapper mapper = new PomsMapper(broadcasterService, MediaClassificationService.getInstance(), subtitlesService);


    @ParameterizedTest
    @MethodSource("messages")
    void json(ProductMetadata metadata) {
        log.debug("{}", metadata);

        assumeThat(metadata.type()).isEqualTo(Type.update);

        assumeThat(metadata.getMediaType()).isNotNull();


        MediaObject o = metadata.getMediaType().getMediaInstance();
        o.setMediaType(metadata.getMediaType());
        o.setMid(metadata.getMid());
        mapper.map(metadata.contents(), o);

        Set<ConstraintViolation<MediaObject>> validate = Validation.validate(o);
        assertThat(validate).withFailMessage(() -> metadata.toString() + " is invalid " + validate).isEmpty();

        if (o instanceof  Group g) {
            testGroup(g);
        }
        if (o instanceof  Program p) {
            testProgram(p);
        }

    }

    void testGroup(Group group) {
        var mainTitle = group.getMainTitle();
        log.info("{}: {}", group.getMid(), mainTitle);
        assertThat(group
            .getMainTitle())
            .withFailMessage("Group %s has no main title", group)
            .isNotNull();
    }

    void testProgram(Program program) {
      /*  assertThat(program.getSubTitle())
            .withFailMessage("Program %s has no episode title", program)
            .isNotNull();

        assertThat(program.getMainTitle())
            .withFailMessage("Program %s has no main title", program)
            .isNotNull();*/
    }

}
