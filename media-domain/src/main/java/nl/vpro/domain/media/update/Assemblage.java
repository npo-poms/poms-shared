package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;

import nl.vpro.domain.media.support.OwnerType;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@AllArgsConstructor
@lombok.Builder
@Data
public class Assemblage {
    @lombok.Builder.Default
    OwnerType ownerType = OwnerType.BROADCASTER;
    final boolean copyWorkflow;
    final boolean copyLanguageAndCountry;
    final boolean imageMetaData;
}
