package nl.vpro.domain.image.backend;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;



/**
 * Wrapped for poms images.
 * @since 7.2
 */
@Slf4j
public class BackendImageSourceCreator extends  PomsImageSourceCreator<BackendImage> {

    @Override
    protected Optional<Long> getId(BackendImage supplier) {
        return Optional.ofNullable(supplier.getId());
    }

}
