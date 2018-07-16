package nl.vpro.domain.media.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import nl.vpro.domain.media.support.OwnerType;

/**
 * This look a lot like {@link MediaUpdateConfig}
 *
 * TODO: MediaUpdate needs heavy refactoring, and we should think about this kind of stuff then too.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
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

    @lombok.Builder.Default
    boolean createScheduleEvents = false;

    @lombok.Builder.Default
    boolean locationsUpdate = false;

    public static Builder withAllTrue() {
        return builder()
            .copyWorkflow(true)
            .copyLanguageAndCountry(true)
            .copyPredictions(true)
            .episodeOfUpdate(true)
            .memberOfUpdate(true)
            .ratingsUpdate(true)
            .imageMetaData(true)
            .createScheduleEvents(true)
            .locationsUpdate(true);

    }

}
