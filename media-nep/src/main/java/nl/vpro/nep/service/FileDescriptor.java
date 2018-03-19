package nl.vpro.nep.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@lombok.Builder
public class FileDescriptor {

    private final Long size;
    private final Instant lastModified;
}
