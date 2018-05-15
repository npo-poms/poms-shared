package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import nl.vpro.domain.media.support.OwnerType;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
@Data
@ToString
public class AssemblageConfig {

    @lombok.Builder.Default
    OwnerType ownerType = OwnerType.BROADCASTER;

    @lombok.Builder.Default
    boolean copyWorkflow = false;

    @lombok.Builder.Default
    boolean copyLanguageAndCountry = false;

    @lombok.Builder.Default
    boolean imageMetaData = false;

    @lombok.Builder.Default
    boolean copyPredictions = false;

    @lombok.Builder.Default
    boolean episodeOfUpdate = true;
    @lombok.Builder.Default
    boolean memberOfUpdate = true;
    @lombok.Builder.Default
    boolean ratingsUpdate = true;

}
