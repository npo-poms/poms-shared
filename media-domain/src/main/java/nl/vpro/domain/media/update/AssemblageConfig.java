package nl.vpro.domain.media.update;

import lombok.*;

import java.time.Duration;
import java.util.*;
import java.util.function.*;

import org.slf4j.Logger;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.Slf4jSimpleLogger;
import nl.vpro.util.IntegerVersion;

/**
 * Contains hints and configuration about how to assemble media objects from {@link MediaUpdate} objects.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
@Data
@ToString
public class AssemblageConfig {

    @lombok.Builder.Default
    OwnerType owner = OwnerType.BROADCASTER;

    @Singular
    List<OwnerType> similarOwnerTypes;

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
    boolean guessEpisodePosition = false;

    @lombok.Builder.Default
    BiPredicate<MemberRef, AssemblageConfig> memberOfUpdate = null;

    @lombok.Builder.Default
    boolean ratingsUpdate = true;

    @lombok.Builder.Default
    boolean copyTwitterrefs = false;

    @lombok.Builder.Default
    boolean createScheduleEvents = false;

    @lombok.Builder.Default
    Function<Program, Boolean> deleteIfNoScheduleEventsLeft = (p) -> false;

    /**
     * This is mainly targeted at PREPR which does not support programs spanning 0 o'clock.
     * If this is set to >= 0, then schedule merging will merge adjacent scheduleevents if they are of the same MID
     * The size of the duration defines the maximal gap between the events. (For PREPR there is never anything broadcasted in the second before midnight)
     *
     */
    @lombok.Builder.Default
    Duration mergeScheduleEvents = Duration.ofMillis(-1);

    @lombok.Builder.Default
    BiFunction<MediaObject, AssemblageConfig, Boolean> inferDurationFromScheduleEvents = (s, ac) -> false;


    @lombok.Builder.Default
    boolean locationsUpdate = false;

    @lombok.Builder.Default
    Steal stealMids = Steal.NO;

    /**
     * Matching happens on crid. There is a possibility though that the found object is of the wrong type (e.g. a Program and not a Segment)
     * If stealCrids is true, then in that situation the existing object is left, but the matching crid is removed.
     */
    @lombok.Builder.Default
    Steal stealCrids= Steal.NO;

    /**
     * If an incoming segment matches a segment of _different_ program, then disconnect it from that other program
     * Otherwise consider this situation errorneous.
     */
    @lombok.Builder.Default
    Steal stealSegments = Steal.NO;

    /**
     * On default it you merge a program, exsisting segments will not be removed
     * This can be configured using this.
     * See als {@link Builder#deleteSegmentsForOwner()}
     */
    @lombok.Builder.Default
    BiFunction<Segment, AssemblageConfig, Boolean> segmentsForDeletion = (s, ac) -> false;

    @lombok.Builder.Default
    Function<String, Boolean> cridsForDelete = (c) -> false;

    @lombok.Builder.Default
    Steal updateType = Steal.NO;

    SimpleLogger logger;


    public Set<OwnerType> getOwnerAndSimilar() {
        Set<OwnerType> ownerTypes = new HashSet<>();
        ownerTypes.add(owner);
        ownerTypes.addAll(similarOwnerTypes);
        return ownerTypes;
    }

    public SimpleLogger loggerFor(Logger log) {
        if (logger == null) {
            return Slf4jSimpleLogger.of(log);
        } else {
            return logger.chain(Slf4jSimpleLogger.of(log));
        }
    }
    public AssemblageConfig copy() {
        return new AssemblageConfig(
            owner,
            similarOwnerTypes,
            copyWorkflow,
            copyLanguageAndCountry,
            imageMetaData,
            copyPredictions,
            episodeOfUpdate,
            guessEpisodePosition,
            memberOfUpdate,
            ratingsUpdate,
            copyTwitterrefs,
            createScheduleEvents,
            deleteIfNoScheduleEventsLeft,
            mergeScheduleEvents,
            inferDurationFromScheduleEvents,
            locationsUpdate,
            stealMids,
            stealCrids,
            stealSegments,
            segmentsForDeletion,
            cridsForDelete,
            updateType,
            logger);
    }
    public AssemblageConfig withLogger(SimpleLogger logger) {
        AssemblageConfig copy = copy();
        copy.setLogger(logger);
        return copy;
    }
    public AssemblageConfig withThreadLocalLogger() {
        return withLogger(SimpleLogger.THREAD_LOCAL.get());
    }

    public boolean isMemberOfUpdate() {
        return memberOfUpdate != null;
    }


    public Predicate<MemberRef> getMemberOfUpdatePredicate() {
        return memberRef -> AssemblageConfig.this.memberOfUpdate != null && AssemblageConfig.this.memberOfUpdate.test(memberRef, AssemblageConfig.this);
    }

    public static Builder withAllTrue() {
        return builder()
            .copyWorkflow(true)
            .copyLanguageAndCountry(true)
            .copyPredictions(true)
            .episodeOfUpdate(true)
            .guessEpisodePosition(true)
            .memberOfUpdateBoolean(true)
            .ratingsUpdate(true)
            .copyTwitterrefs(true)
            .imageMetaData(true)
            .createScheduleEvents(true)
            .locationsUpdate(true)
            .stealMids(Steal.YES)
            .stealCrids(Steal.YES)
            .stealSegments(Steal.YES)
            .updateType(Steal.YES)
            ;
    }

    public boolean considerForDeletion(Segment segment) {
        return segmentsForDeletion.apply(segment, this);
    }

    public void backwardsCompatible(IntegerVersion version) {
        setCopyLanguageAndCountry(version == null || version.isNotBefore(5, 0));
        setCopyPredictions(version == null || version.isNotBefore(5, 6));
        setCopyTwitterrefs(version == null || version.isNotBefore(5, 10));
    }

    public void setMemberOfUpdateBoolean(boolean bool) {
        setMemberOfUpdate(new Always(bool));
    }

    @EqualsAndHashCode
    public static class Always implements   BiPredicate<MemberRef, AssemblageConfig> {

        private final boolean always;

        public Always(boolean always) {
            this.always = always;
        }

        @Override
        public boolean test(MemberRef memberRef, AssemblageConfig assemblageConfig) {
            return always;

        }
    }

    public static class Builder {
        /**
         * Since POMS 5.9 a segment can have an owner.
         * This sais that segments that have the configured owner, but are not present in the incoming program are to be deleted from the program to update.
         */
        public Builder deleteSegmentsForOwner() {
            return segmentsForDeletion((s, a) -> s.getOwner() != null && a.getOwnerAndSimilar().contains(s.getOwner()));
        }
        public Builder memberOfUpdateBoolean(boolean b) {
            return memberOfUpdate((new Always(b)));
        }
        public Builder memberRefMatchOwner() {
            return memberOfUpdate((mr, c) -> mr.getOwner() == c.getOwner());
        }
        public Builder ownerless() {
            return owner(null);
        }
        public Builder deleteBroadcastIfNoScheduleEventsLeft() {
            return deleteIfNoScheduleEventsLeft(p -> p.getType() == ProgramType.BROADCAST || p.getType() == ProgramType.STRAND);
        }
    }

    public enum Steal {
        YES((incoming, toUpdate) -> true),
        IF_DELETED((incoming, toUpdate) ->  Workflow.PUBLISHED_AS_DELETED.contains(toUpdate.getWorkflow())),
        NO((incoming, toUpdate) -> true),

        /**
         * Only if the incoming object is new. We matched on crid.
         */
        IF_INCOMING_NO_MID((incoming, toUpdate) -> incoming.getMid() == null)
        ;

        private final BiFunction<MediaObject, MediaObject, Boolean> is;

        Steal(BiFunction<MediaObject, MediaObject, Boolean> is) {
            this.is = is;
        }


        public boolean is(MediaObject incoming, MediaObject toUpdate) {
            return is.apply(incoming, toUpdate);
        }

    }
}
