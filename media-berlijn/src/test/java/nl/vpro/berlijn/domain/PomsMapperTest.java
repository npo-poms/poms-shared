package nl.vpro.berlijn.domain;

import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import nl.vpro.berlijn.domain.productmetadata.Genre;
import nl.vpro.berlijn.domain.productmetadata.GenreType;
import nl.vpro.domain.media.MediaClassificationService;
import nl.vpro.domain.subtitles.SubtitlesProvider;
import nl.vpro.domain.user.BroadcasterService;

import static org.mockito.Mockito.mock;

@Log4j2
class PomsMapperTest {

    BroadcasterService broadcasterService = mock(BroadcasterService.class);
    SubtitlesProvider subtitlesService = (s) -> new ArrayList<>();
    PomsMapper impl = new PomsMapper(broadcasterService, MediaClassificationService.getInstance(), subtitlesService);

    @Test
    public void genre() {

        Set<nl.vpro.domain.media.Genre> mapped = impl.mapGenre(Stream.of(
            new Genre(null, "40"), // Human Interest
            new Genre(GenreType.secondary, "4029") // Reizen
        ));
        log.info("{}", mapped);

    }

}
