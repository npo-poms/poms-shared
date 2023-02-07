package nl.vpro.domain.media.support;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

import nl.vpro.domain.image.*;
import nl.vpro.domain.image.backend.BackendImageSourceCreator;

/**
 * Wrapped for poms images.
 */
@Slf4j
public class PomsImages {


    public static class Creator extends BackendImageSourceCreator {

        @Override
        protected Optional<Long> getId(Metadata<?> metadata) {
            if (metadata instanceof Image) {
                return Optional.of(((Image) metadata).getImageId());
            } else {
                return Optional.empty();
            }
        }
    }
}
