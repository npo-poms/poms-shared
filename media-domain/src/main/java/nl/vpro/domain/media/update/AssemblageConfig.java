package nl.vpro.domain.media.update;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;
import java.util.function.*;

import org.apache.logging.log4j.Logger;
import org.meeuw.functional.*;
import org.slf4j.helpers.MessageFormatter;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.logging.simple.*;
import nl.vpro.util.IntegerVersion;

import static org.meeuw.functional.Predicates.*;


/**
 * Contains hints and configuration about how to assemble media objects from {@link MediaUpdate} objects.
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder(builderClassName = "Builder", buildMethodName = "_build", toBuilder = true)
@Data
@EqualsAndHashCode
@ToString
public class AssemblageConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -6110624203385921307L;


    public static final BiPredicate<List<String>, Relation> DEFAULT_RELATION_MATCH = (b, r) -> b.contains(r.getBroadcaster());

    @lombok.Builder.Default
    OwnerType owner = OwnerType.BROADCASTER;

    @Singular
    List<OwnerType> similarOwnerTypes;

    @lombok.Builder.Default
    boolean copyWorkflow = false;

    Boolean copyLanguageAndCountry;

    @lombok.Builder.Default
    boolean imageMetaData = false;

    @lombok.Builder.Default
    Boolean copyPredictions = null;

    @lombok.Builder.Default
    Authority authority = Authority.USER;

    @lombok.Builder.Default
    boolean episodeOfUpdate = true;

    @lombok.Builder.Default
    boolean guessEpisodePosition = false;

    @lombok.Builder.Default
    BiPredicate<MemberRef, AssemblageConfig> memberOfUpdate = biAlwaysTrue();

    @lombok.Builder.Default
    boolean ratingsUpdate = true;

    @lombok.Builder.Default
    Boolean copyTwitterRefs = null;

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
     * If this is set to >= 0, then schedule merging will merge adjacent schedule events if they are of the same MID
     * The size of the duration defines the maximal gap between the events. (For PREPR there is never anything broadcast in the second before midnight)
     *
     */
    @lombok.Builder.Default
    Duration mergeScheduleEvents = Duration.ofMillis(-1);

    @lombok.Builder.Default
    BiPredicate<MediaObject, AssemblageConfig> inferDurationFromScheduleEvents = biAlwaysFalse();

    @lombok.Builder.Default
    boolean locationsUpdate = true;

    @lombok.Builder.Default
    Steal stealMids = Steal.NO;

    /**
     * Matching happens on crid. There is a possibility though that the found object is of the wrong type (e.g. a Program and not a Segment)
     * If stealCrids is true, then in that situation the existing object is left, but the matching crid is removed.
     */
    @lombok.Builder.Default
    TriPredicate<MediaObject, MediaObject, String> stealCrids = TriSteal.of(Steal.NO);

    /**
     * If an incoming segment matches a segment of _different_ program, then disconnect it from that other program
     * Otherwise consider this situation errorneous.
     */
    @lombok.Builder.Default
    Steal stealSegments = Steal.NO;

    /**
     * On default, if you merge a program, existing segments will not be removed
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


    @lombok.Builder.Default
    BiPredicate<MediaObject, PublishableObject<?>> markForDeleteOnly = (m, mu) -> false;

    @lombok.Builder.Default
    Predicate<MediaObject> deleteBroadcasters = alwaysFalse();

    @lombok.Builder.Default
    Function<MediaObject, Optional<String>> publishImmediately = Functions.always(Optional.empty());


    @lombok.Builder.Default
    Predicate<MediaObject> implicitUndelete = alwaysTrue();


    @lombok.Builder.Default
    UnaryOperator<String> cleaner  = null;

    @lombok.Builder.Default
    UnaryOperator<String> multilineCleaner =  null;


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
            return Log4j2SimpleLogger.of(log);
        } else {
            return logger.chain(Log4j2SimpleLogger.of(log));
        }
    }

    public SimpleLogger loggerFor(org.slf4j.Logger log) {
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
            authority,
            episodeOfUpdate,
            guessEpisodePosition,
            memberOfUpdate,
            ratingsUpdate,
            copyTwitterRefs,
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
            markForDeleteOnly,
            deleteBroadcasters,
            publishImmediately,
            implicitUndelete,
            cleaner,
            multilineCleaner,
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
     * Sets updating as permissive as possible, with a few exceptions:
     * <ul>
     * <li>relations: only sync relations of the broadcasters associated with the account (this is also the default)</li>
     * <li>memberrefs</li>
     * </ul>
     */
    public static Builder withAllTrue() {
        return builder()
            .copyWorkflow(true)
            .copyLanguageAndCountry(true)
            .copyPredictions(true)
            .episodeOfUpdate(true)
            .guessEpisodePosition(true)
            .memberRefMatchOwner()
            .ratingsUpdate(true)
            .copyTwitterRefs(true)
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
            .deleteBroadcasters(alwaysTrue())
            ;
    }

    public boolean considerForDeletion(Segment segment) {
        return segmentsForDeletion.test(segment, this);
    }

    /**
     * Makes the default assemblage backwards compatible for certain properties (unless they were explicitly stated).
     */
    public void backwardsCompatible(IntegerVersion version) {
        if (copyLanguageAndCountry == null) {
            setCopyLanguageAndCountry(version == null || version.isNotBefore(5, 0));
        }
        if (copyPredictions == null) {
            setCopyPredictions(version == null || version.isNotBefore(5, 6));
        }
        if (copyTwitterRefs == null) {
            setCopyTwitterRefs(version == null || version.isNotBefore(5, 10));
        }
    }

    public void setMemberOfUpdateBoolean(boolean bool) {
        setMemberOfUpdate(Predicates.biAlways(bool, "always " + bool));
    }

    public boolean isCopyPredictions() {
        return copyPredictions != null && copyPredictions;
    }

    public boolean isCopyTwitterRefs() {
        return copyTwitterRefs != null && copyTwitterRefs;
    }

    public boolean isCopyLanguageAndCountry() {
        return copyLanguageAndCountry != null && copyLanguageAndCountry;
    }

    public static class Builder {

        private IntegerVersion backwardsCompatibleWith;
        private boolean explicitVersion = false;
        /**
         * Since POMS 5.9 a segment can have an owner.
         * This says that segments that have the configured owner, but are not present in the incoming program are to be deleted from the program to update.
         */
        public Builder deleteSegmentsForOwner() {
            return segmentsForDeletion((s, a) -> s.getOwner() != null && a.getOwnerAndSimilar().contains(s.getOwner()));
        }
        public Builder memberOfUpdateBoolean(boolean b) {
            return memberOfUpdate(Predicates.biAlways(b, "always " + b));
        }
        public Builder memberRefMatchOwner() {
            return memberOfUpdate((mr, c) -> c.getOwnerAndSimilar().contains(mr.getOwner()));
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
        /**
         * Makes the default assemblage backwards compatible.
         */
        public Builder backwardsCompatible(IntegerVersion version) {
            backwardsCompatibleWith = version;
            explicitVersion = true;
            return this;
        }

        public AssemblageConfig build() {
            AssemblageConfig config = _build();
            if (explicitVersion) {
                config.backwardsCompatible(backwardsCompatibleWith);
            }
            return config;
        }
    }

    public enum Steal implements BiPredicate<MediaObject, MediaObject> {
        YES((incoming, toUpdate) -> true),
        IF_DELETED((incoming, toUpdate) ->  Workflow.PUBLISHED_AS_DELETED.contains(toUpdate.getWorkflow())),
        NO((incoming, toUpdate) -> true),

        /**
         * Only if the incoming object is new. We matched on crid.
         */
        IF_INCOMING_NO_MID((incoming, toUpdate) -> incoming != null && incoming.getMid() == null)
        ;

        private final BiPredicate<MediaObject, MediaObject> impl;

        Steal(BiPredicate<MediaObject, MediaObject> impl) {
            this.impl = impl;
        }
        @Override
        public boolean test(MediaObject incoming, MediaObject toUpdate) {
            return impl.test(incoming, toUpdate);
        }

        @Override
        public String toString() {
            return name();
        }
    }


    public interface TriSteal<T> extends TriPredicate<MediaObject, MediaObject, T> {
        static <S> TriSteal<S> of(Steal s) {
            return new TriStealImpl<>(Predicates.ignoreArg3(s));
        }
    }

    @EqualsAndHashCode
    public static class TriStealImpl<T> implements TriSteal<T> {
        private final org.meeuw.functional.TriPredicate<MediaObject, MediaObject, T> wrapped;

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
        /**
         * Required, if not given, give an error. E.g. the MID may be required
         */
        YES,
        /**
         * Required, if not given, it will still work. E.g. a MID can be generated
         */
        NO,
        /**
         * Only required to give this if the target object lacks this. E.g. an object may have been matced on crid, and then existing object provides the MID
         *
         */
        IF_TARGET_EMPTY,
        /**
         * If not given, the entire associated object will be skipped without error (but warnings probably). E.g. an incoming object from PREPR can be ignored.
         */
        ELSE_SKIP
    }

    /**
     *
     * @since 5.13
     * @param <S> Type of incoming objects
     * @param <F> Type of field to of those object which are required (or not)
     */
    public static abstract class Require<S, F>  implements BiPredicate<S, S>, Serializable {
        @Serial
        private static final long serialVersionUID = 4271155751821174345L;

        protected final BiFunction<S, S, RequireEnum> value;
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
            switch (value) {
                case ELSE_SKIP, YES -> {
                    F sourceValue = getter.apply(source);
                    return sourceValue != null;
                }
                case NO -> {
                    return true;
                }
                case IF_TARGET_EMPTY -> {
                    F sourceValue = getter.apply(source);
                    if (sourceValue == null) {
                        F targetValue = getter.apply(target);
                        return targetValue == null;
                    } else {
                        return true;
                    }
                }
                default -> throw new IllegalStateException();
            }

        }
        public void throwIfIllegal(S o1, S o2, String message, Object... arguments) {
            if (! test(o1, o2)) {

                if (value.apply(o1, o2) == RequireEnum.ELSE_SKIP) {
                    throw new SkippingRequiredFieldException(message, arguments);
                } else {
                    throw new FatalRequiredFieldException(message, arguments);
                }
            }
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
    public static class MidRequire extends Require<MediaObject, String> implements Serializable {
        public static final MidRequire YES = new MidRequire(RequireEnum.YES);
        public static final MidRequire NO = new MidRequire(RequireEnum.NO);
        public static final MidRequire IF_TARGET_EMPTY = new MidRequire(RequireEnum.IF_TARGET_EMPTY);
        public static final MidRequire ELSE_SKIP = new MidRequire(RequireEnum.ELSE_SKIP);

        @Serial
        private static final long serialVersionUID = -6648234818264600444L;

        private MidRequire(RequireEnum value) {
            this(Functions.biAlways(value));
        }

        public MidRequire(BiFunction<MediaObject, MediaObject, RequireEnum> value) {
            super(value, MediaObject::getMid);
        }

         @Override
         public String toString() {
             return "MidRequire:" + value.toString();
         }
    }

    /**
     * Exception that can be thrown during assemblage if some required field is not filled.
     * @since 5.13
     */
    public static abstract class  RequiredFieldException extends IllegalArgumentException {

        @Serial
        private static final long serialVersionUID = -7054047338107481793L;

        @Getter
        Serializable[] arguments;
        RequiredFieldException(String format, Serializable... arguments) {
            super(format);
            this.arguments = arguments;
        }

        /**
         * Returns the formatted message. If you want to supply it do logging directly you could use {@link #getFormat()} and {@link #getArguments()}
         */
        @Override
        public String getMessage() {
             return MessageFormatter.arrayFormat(super.getMessage(), arguments).getMessage();
        }
        public String getFormat() {
            return super.getMessage();
        }

        public abstract boolean isFatal();
    }

    public static class FatalRequiredFieldException extends RequiredFieldException {

        @Serial
        private static final long serialVersionUID = -1815466568814368401L;

        FatalRequiredFieldException(String format, Object... arguments) {
            super(format, arguments);
        }

        @Override
        public boolean isFatal() {
            return true;
        }
    }

    /**
     * If a required field is not filled, then this exception can be thrown to indicate that the related object can just
     * be ignored then. So {@link #isFatal()} returns false.
     *
     */
    public static class SkippingRequiredFieldException extends RequiredFieldException {

        @Serial
        private static final long serialVersionUID = -6503681639731951808L;

        SkippingRequiredFieldException(String format, Object... arguments) {
            super(format, arguments);
        }

        @Override
        public boolean isFatal() {
            return false;
        }
    }

}
