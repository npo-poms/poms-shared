package nl.vpro.domain.media.update;

import lombok.*;

import java.time.Duration;
import java.util.*;
import java.util.function.*;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.logging.simple.SimpleLogger;
import nl.vpro.logging.simple.Slf4jSimpleLogger;
import nl.vpro.util.*;

import static nl.vpro.util.Predicates.*;


/**
 * Contains hints and configuration about how to assemble media objects from {@link MediaUpdate} objects.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder")
@Data
@EqualsAndHashCode
@ToString
public class AssemblageConfig {

    public static BiPredicate<List<String>, Relation> DEFAULT_RELATION_MATCH = (b, r) -> b.contains(r.getBroadcaster());

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
    BiPredicate<MemberRef, AssemblageConfig> memberOfUpdate = biAlwaysTrue();

    @lombok.Builder.Default
    boolean ratingsUpdate = true;

    @lombok.Builder.Default
    boolean copyTwitterrefs = false;

    @lombok.Builder.Default
    boolean copyIntentions = true;

    @lombok.Builder.Default
    boolean copyTargetGroups = true;

    @lombok.Builder.Default
    boolean copyGeoLocations = true;

    @lombok.Builder.Default
    boolean copyTopics = true;


    @lombok.Builder.Default
    BiPredicate<List<String>, Relation> relations = DEFAULT_RELATION_MATCH;

    @lombok.Builder.Default
    boolean createScheduleEvents = false;

    @lombok.Builder.Default
    Predicate<Program> deleteIfNoScheduleEventsLeft = alwaysFalse();

    /**
     * This is mainly targeted at PREPR which does not support programs spanning 0 o'clock.
     * If this is set to >= 0, then schedule merging will merge adjacent scheduleevents if they are of the same MID
     * The size of the duration defines the maximal gap between the events. (For PREPR there is never anything broadcasted in the second before midnight)
     *
     */
    @lombok.Builder.Default
    Duration mergeScheduleEvents = Duration.ofMillis(-1);

    @lombok.Builder.Default
    BiPredicate<MediaObject, AssemblageConfig> inferDurationFromScheduleEvents = biAlwaysFalse();


    @lombok.Builder.Default
    boolean locationsUpdate = false;


    @lombok.Builder.Default
    Steal stealMids = Steal.NO;

    /**
     * Matching happens on crid. There is a possibility though that the found object is of the wrong type (e.g. a Program and not a Segment)
     * If stealCrids is true, then in that situation the existing object is left, but the matching crid is removed.
     */
    @lombok.Builder.Default
    TriSteal<String> stealCrids= TriSteal.of(Steal.NO);

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
    BiPredicate<Segment, AssemblageConfig> segmentsForDeletion = biAlwaysFalse();

    @lombok.Builder.Default
    Predicate<String> cridsForDelete = alwaysFalse();

    @lombok.Builder.Default
    Steal updateType = Steal.NO;

    /**
     * TODO
     * @since 5.13
     */
    @lombok.Builder.Default
    boolean  followMerges = false;

    /**
     * @since 5.13
     */
    @lombok.Builder.Default
    MidRequire  requireIncomingMid = MidRequire.NO;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    transient SimpleLogger logger;


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
            copyIntentions,
            copyTargetGroups,
            copyGeoLocations,
            copyTopics,
            relations,
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
            followMerges,
            requireIncomingMid,
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

    /**
     * Sets updating a permissive as possible, with few exeptions:
     *
     * - relations: only sync relations of the broadcasters associated with the account (this is also the default)
     */
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
            .copyIntentions(true)
            .copyTargetGroups(true)
            .copyGeoLocations(true)
            .copyTopics(true)
            .relations(DEFAULT_RELATION_MATCH)
            .imageMetaData(true)
            .createScheduleEvents(true)
            .locationsUpdate(true)
            .stealMids(Steal.YES)
            .stealAllCrids(Steal.YES)
            .stealSegments(Steal.YES)
            .updateType(Steal.YES)
            .followMerges(true)
            .requireIncomingMid(MidRequire.YES)
            ;
    }

    public boolean considerForDeletion(Segment segment) {
        return segmentsForDeletion.test(segment, this);
    }

    public void backwardsCompatible(IntegerVersion version) {
        setCopyLanguageAndCountry(version == null || version.isNotBefore(5, 0));
        setCopyPredictions(version == null || version.isNotBefore(5, 6));
        setCopyTwitterrefs(version == null || version.isNotBefore(5, 10));
    }

    public void setMemberOfUpdateBoolean(boolean bool) {
        setMemberOfUpdate(Predicates.biAlways(bool, "always " + bool));
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
            return memberOfUpdate(Predicates.biAlways(b, "always " + b));
        }
        public Builder memberRefMatchOwner() {
            return memberOfUpdate((mr, c) -> mr.getOwner() == c.getOwner());
        }
        public Builder ownerless() {
            return owner(null);
        }

        public Builder stealAllCrids(Steal steal) {
            return stealCrids(TriSteal.of(steal));
        }

        public Builder deleteBroadcastIfNoScheduleEventsLeft() {
            return deleteIfNoScheduleEventsLeft(p -> p.getType() == ProgramType.BROADCAST || p.getType() == ProgramType.STRAND);
        }
    }

    public enum Steal implements BiPredicate<MediaObject, MediaObject> {
        YES((incoming, toUpdate) -> true),
        IF_DELETED((incoming, toUpdate) ->  Workflow.PUBLISHED_AS_DELETED.contains(toUpdate.getWorkflow())),
        NO((incoming, toUpdate) -> true),

        /**
         * Only if the incoming object is new. We matched on crid.
         */
        IF_INCOMING_NO_MID((incoming, toUpdate) -> incoming.getMid() == null)
        ;

        private final BiPredicate<MediaObject, MediaObject> impl;

        Steal(BiPredicate<MediaObject, MediaObject> impl) {
            this.impl = impl;
        }
        @Override
        public boolean test(MediaObject incoming, MediaObject toUpdate) {
            return impl.test(incoming, toUpdate);
        }

    }


    public interface TriSteal<T> extends TriPredicate<MediaObject, MediaObject, T> {
        static <S> TriSteal<S> of(Steal s) {
            return new TriStealImpl<S>(Predicates.ignoreArg3(s));
        }
    }

    @EqualsAndHashCode
    public static class TriStealImpl<T> implements TriSteal<T> {
        private final TriPredicate<MediaObject, MediaObject, T> wrapped;

        public TriStealImpl(TriPredicate<MediaObject, MediaObject, T> wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public boolean test(MediaObject mediaObject, MediaObject mediaObject2, T t) {
            return wrapped.test(mediaObject, mediaObject2, t);
        }
    }

    /**
     * @since 5.13
     */
    public enum RequireEnum {
        YES,
        NO,
        IF_TARGET_EMPTY,
        ELSE_SKIP
    }

    /**
     *
     * @since 5.13
     * @param <S> Type of incoming objects
     * @param <F> Type of field to of those object which are required (or not)
     */
    public static abstract class Require<S, F>  implements BiPredicate<S, S> {
        private final BiFunction<S, S, RequireEnum> value;
        private final Function<S, F> getter;

        protected Require(BiFunction<S, S, RequireEnum> value, Function<S, F> getter) {
            this.value = value;
            this.getter = getter;
        }
        @Override
        public boolean test(S source, S target) {
            return defaultTest(source, target, value.apply(source, target), getter);
        }


        public static <S, F> boolean defaultTest(S source, S target, RequireEnum value, Function<S, F> getter) {
            switch(value) {
                case ELSE_SKIP:
                case YES: {
                    F sourceValue = getter.apply(source);
                    return sourceValue != null;
                }
                case NO:
                    return true;
                case IF_TARGET_EMPTY: {
                    F sourceValue = getter.apply(source);
                    if (sourceValue == null) {
                        F targetValue = getter.apply(target);
                        return targetValue == null;
                    } else {
                        return true;
                    }
                }
                default:
                    throw new IllegalStateException();
            }

        }
        public Optional<S> throwIfIllegal(S o1, S o2, String message, Object... arguments) {
            if (! test(o1, o2)) {

                if (value.apply(o1, o2) == RequireEnum.ELSE_SKIP) {
                    return Optional.empty();
                }
                throw new RequiredFieldException(message, arguments);
            }
            return Optional.of(o2);
        }
        @Override
        public String toString() {
            return value.toString();
        }

         @Override
         public boolean equals(Object o) {
             if (this == o) return true;
             if (o == null || getClass() != o.getClass()) return false;

             Require<?, ?> require = (Require<?, ?>) o;

             return Objects.equals(value, require.value);
         }

         @Override
         public int hashCode() {
             return value != null ? value.hashCode() : 0;
         }
     }
     /**
     * @since 5.13
      */
    public static class MidRequire extends Require<MediaObject, String> {
        public static final MidRequire YES = new MidRequire(RequireEnum.YES);
        public static final MidRequire NO = new MidRequire(RequireEnum.NO);
        public static final MidRequire IF_TARGET_EMPTY = new MidRequire(RequireEnum.IF_TARGET_EMPTY);
        public static final MidRequire ELSE_SKIP = new MidRequire(RequireEnum.ELSE_SKIP);

        private MidRequire(RequireEnum value) {
            this(Functions.biAlways(value));
        }

        public MidRequire(BiFunction<MediaObject, MediaObject, RequireEnum> value) {
            super(value, MediaObject::getMid);
        }
    }

    /**
     * @since 5.13
     */
    public static class RequiredFieldException extends IllegalArgumentException {
        @Getter
        Object[] arguments;
        RequiredFieldException(String format, Object... arguments) {
            super(format);
            this.arguments = arguments;
        }

        /**
         * Returns the formatted message. If you want to supply it do logging directly you could user {@link #getFormat()} and {@link #getArguments()}
         */
        @Override
        public String getMessage() {
             return MessageFormatter.arrayFormat(super.getMessage(), arguments).getMessage();
        }
        public String getFormat() {
            return super.getMessage();
        }
    }

}
