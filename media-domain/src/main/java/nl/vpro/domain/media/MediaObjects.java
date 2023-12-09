/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.helpers.MessageFormatter;

import com.google.common.collect.*;

import nl.vpro.domain.*;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.media.update.GroupUpdate;
import nl.vpro.domain.media.update.MediaUpdate;
import nl.vpro.domain.subtitles.SubtitlesWorkflow;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.logging.Slf4jHelper;
import nl.vpro.logging.simple.Level;
import nl.vpro.util.DateUtils;
import nl.vpro.util.ObjectFilter;

import static nl.vpro.domain.Changeables.instant;
import static nl.vpro.domain.PublicationReason.REASON_SPLITTER;
import static nl.vpro.domain.media.CollectionUtils.removeIf;
import static nl.vpro.domain.media.support.Workflow.*;


/**
 * Various methods related to dealing with {@link MediaObject}s, like copying and filling.
 * <p>
 * See {@link TextualObjects}, and {@link Embargos} for methods like this (because media objects are {@link TextualObject} and {@link MutableEmbargo}
 * @since 1.5
 */
@Slf4j
public class MediaObjects {

    private MediaObjects() {
        // No instances, static utility functions only
    }


    /**
     * A predicate on {@link Class} to determine if it represents some poms group. That is {@link Group} or {@link GroupUpdate}
     * @since 7.7
     */
    public  static final Predicate<Class<?>> GROUPS = c ->
        Group.class.isAssignableFrom(c) || GroupUpdate.class.isAssignableFrom(c);


    /**
     * A predicate on {@link Class} to determine if it represents some poms media object. That is {@link MediaObject} or {@link MediaUpdate}
     * @since 7.7
     */
    public static final Predicate<Class<?>> ANY_MEDIA = c ->
        (MediaObject.class.isAssignableFrom(c) || MediaUpdate.class.isAssignableFrom(c));


    /**
     * Any {@link #ANY_MEDIA any media} that is not {@link #GROUPS a group}.
     * @since 7.7
     */
    public static final Predicate<Class<?>> NO_GROUPS = ANY_MEDIA.and(GROUPS.negate());


    public static boolean equalsOnAnyId(MediaObject first, MediaObject second) {
        return first == second ||
            first.getId() != null && first.getId().equals(second.getId()) ||
            first.getUrn() != null && first.getUrn().equals(second.getUrn()) ||
            first.getMid() != null && first.getMid().equals(second.getMid()) ||
            equalsOnCrid(first, second);
    }

    public static boolean equalsOnCrid(MediaObject first, MediaObject second) {
        if (first.getCrids().isEmpty() || second.getCrids().isEmpty()) {
            return false;
        }

        for (String firstCrid : first.getCrids()) {
            for (String secondCrid : second.getCrids()) {
                if (secondCrid.equals(firstCrid)) {
                    return true;
                }
            }
        }

        return false;

    }


    /**
     * Sets the owner of all titles, descriptions, locations and images found in given MediaObject
     */
    public static void forOwner(MediaObject media, OwnerType owner) {
        TextualObjects.forOwner(media, owner);
        for (Location location : media.getLocations()) {
            location.setOwner(owner);
        }
        for (Image image : media.getImages()) {
            image.setOwner(owner);
        }
    }

    /**
     * Perfomrs of deep copy of the media object, this is currently implemented by serializing/deserializing it.
     */
    @SuppressWarnings("unchecked")
    public static <T extends MediaObject> T deepCopy(T media) {
        ObjectOutputStream objectOut = null;
        ObjectInputStream objectIn = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(media);
            objectOut.flush();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            objectIn = new ObjectInputStream(byteIn);
            return (T) objectIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    log.error("Error closing object output stream after deep copy: {}", e.getMessage());
                }
            }

            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    log.error("Error closing object input stream after deep copy: {}", e.getMessage());
                }
            }
        }
    }


    /**
     * Copies most field values from one media object to another.
     * <p>
     * Principally, this should be all fields of which the value logically can exist on more than one mediaobject the same time,
     * so not unique fields like id and mid.
     * <p>
     * Also, membership of groups will not be automatically filled. This would need write access on those objects.
     * <p>
     * ScheduleEvents, workflow, subtitles status are not copied too, since this would not make sense.
     * <p>
     *
     * @see #copyFull(MediaObject, MediaObject)
     */
    public static void copy(@NonNull MediaObject from, @NonNull MediaObject to) {
        Embargos.copy(from, to);
        TextualObjects.copy(from, to);

        to.setAgeRating(from.getAgeRating());
        to.setAvAttributes(AVAttributes.copy(from.getAvAttributes()));
        to.setAVType(from.getAVType());
        to.setAwards(from.getAwards());

        to.setBroadcasters(from.getBroadcasters());

        to.setContentRatings(from.getContentRatings());
        to.setCountries(from.getCountries());

        to.setDuration(from.getDuration());


        //to.setDescendantOf();
        to.setEmail(from.getEmail());
        to.setEmbeddable(from.isEmbeddable());

        to.setGenres(from.getGenres());
        to.setGeoRestrictions(from.getGeoRestrictions());

        // to.setHasSubtitles(from.hasSubtitles());
        to.setImages(from.getImages());
        to.setIntentions(from.getIntentions());

        to.setIsDubbed(from.isDubbed());

        to.setLanguages(from.getLanguages());

        for (Prediction p : from.getPredictions()) {
            Prediction newPrediction = Prediction.copy(p, to);
            to.getPredictions().add(newPrediction);
        }

        to.setLocations(from.getLocations());

        //to.setMediaType(from.getMediaType());
        //to.setMemberOf();

        to.setCredits(from.getCredits());
        to.setPortalRestrictions(from.getPortalRestrictions());
        to.setPortals(from.getPortals());


        to.setRelations(from.getRelations());
        to.setReleaseYear(from.getReleaseYear());

        //to.setScheduleEvents();
        to.setSource(from.getSource());

        to.setTags(from.getTags());
        to.setTeletext(from.getTeletext());
        to.setTwitterRefs(from.getTwitterRefs());
        to.setTargetGroups(from.getTargetGroups());

        to.setWebsites(from.getWebsites());

    }


    /**
     * A more full copy, also copying field that you could normally would not copy, like MID.
     * <p>
     * The assumption is that both objects are not yet persistent, or e.g. a type conversion is happening
     * @since 5.11
     * @see #copy(MediaObject, MediaObject)
     */
    public static void copyFull(@NonNull MediaObject from, @NonNull MediaObject to) {
        to.setMid(from.getMid());
        to.setStreamingPlatformStatus(from.getModifiableStreamingPlatformStatus());

        copy(from, to);
        if (to.getClass().isAssignableFrom(from.getClass())) {
            to.setMediaType(from.getMediaType());
            if (to instanceof Program) {
                copyFullProgram((Program) from,  (Program) to);
            }
        }
        to.setMemberOf(from.getMemberOf());
        to.setCrids(from.getCrids());


    }

     /**
     * A more full copy, also copying field that you could normally not copy, like MID.
     * <p>
     * The assumption is that both objects are not yet persistent
     * @since 5.11
     */
    public static void copyFullProgram(@NonNull Program from, @NonNull Program  to) {
        to.setScheduleEvents(from.getScheduleEvents());
    }

    public static void matchBroadcasters(BroadcasterService broadcasterService, MediaObject mediaObject) throws NotFoundException {
        matchBroadcasters(broadcasterService, mediaObject, null);
    }

    public static boolean hasChannel(Program media, Channel... channels) {
        return hasChannel(media, Arrays.asList(channels));
    }

    public static boolean hasChannel(Program media, Collection<Channel> channels) {
        for (ScheduleEvent scheduleEvent : media.getScheduleEvents()) {
            if (channels.contains(scheduleEvent.getChannel())) {
                return true;
            }
        }
        return false;
    }

    public static ScheduleEvent findScheduleEventHonoringOffset(Program media, ScheduleEvent source) {
        for (ScheduleEvent existing : media.getScheduleEvents()) {
            if (ScheduleEvents.equalHonoringOffset(existing, source)) {
                return existing;
            }
        }
        return null;
    }

    public static ScheduleEvent findScheduleEvent(Channel channel, Date start, Collection<ScheduleEvent> events) {
        for (ScheduleEvent event : events) {
            if (event.getStartInstant().toEpochMilli() == start.getTime() && event.getChannel().equals(channel)) {
                return event;
            }
        }
        return null;
    }

    public static ScheduleEvent findScheduleEvent(Channel channel, LocalDateTime start, Collection<ScheduleEvent> events) {
        for (ScheduleEvent event : events) {
            if (event.getStartInstant().atZone(Schedule.ZONE_ID).toLocalDateTime().equals(start) && event.getChannel().equals(channel)) {
                return event;
            }
        }
        return null;
    }



    public static SortedSet<ScheduleEvent> filterScheduleEvents(Collection<ScheduleEvent> events, Channel... channels) {
        return filterScheduleEvents(events, Arrays.asList(channels));

    }

    public static SortedSet<ScheduleEvent> filterScheduleEvents(Collection<ScheduleEvent> events, Collection<Channel> channelList) {
        SortedSet<ScheduleEvent> result = new TreeSet<>();
        for (ScheduleEvent event : events) {
            if (channelList.contains(event.getChannel())) {
                result.add(event);
            }
        }
        return result;
    }


    /**
     * Returns the channel associated with this program. That is the channel of the earliest schedule event that is not a rerun.
     */
    public static Channel getChannel(@NonNull Program program) {
        for (ScheduleEvent se : program.getScheduleEvents()) {
            if (! ScheduleEvents.isRerun(se)) {
                return se.getChannel();
            }
        }
        return null;
    }

    /**
     * Returns the {@link ScheduleEvent}s associated with the media object.
     * <p>
     * An empty collection for all non programs.
     *
     * @since 5.11
     */
    public static SortedSet<ScheduleEvent> getScheduleEvents(MediaObject media) {
        if (media instanceof Program program) {
            return program.getScheduleEvents();
        } else {
            return new TreeSet<>();
        }
    }


    /**
     * @since 2.2.3
     */
    public static String getRelationText(@NonNull MediaObject object, String relationType) {
        Relation rel = getRelation(object, relationType);
        return rel == null ? null : rel.getText();
    }

    /**
     * @since 3.3.0
     */
    public static Relation getRelation(@NonNull MediaObject object, String relationType) {
        for (Relation relation : object.getRelations()) {
            if (relation.getType().equals(relationType)) {
                return relation;
            }
        }
        return null;
    }

    public static TwitterRef getTwitterHash(@NonNull  MediaObject object) {
        for (TwitterRef ref : object.getTwitterRefs()) {
            if (ref.getType() == TwitterRef.Type.HASHTAG) {
                return ref;
            }
        }
        return null;
    }

    public static TwitterRef getTwitterAccount(@NonNull MediaObject object) {
        for (TwitterRef ref : object.getTwitterRefs()) {
            if (ref.getType() == TwitterRef.Type.ACCOUNT) {
                return ref;
            }
        }
        return null;
    }

    public static String getKijkwijzer(@NonNull MediaObject media) {
        StringBuilder sb = new StringBuilder();
        if (media.getAgeRating() != null) {
            switch (media.getAgeRating()) {
                case _6 -> sb.append('2');
                case _9 -> sb.append('5');
                case _12 -> sb.append('3');
                case _16 -> sb.append('4');
            }
        }

        for (ContentRating contentRating : media.getContentRatings()) {
            if (contentRating != null) {
                sb.append(contentRating.toChar());
            }
        }

        return !sb.isEmpty() ? sb.toString() : null;
    }

    public static Long idFromUrn(String urn) {
        final String id = urn.substring(urn.lastIndexOf(':') + 1);
        return Long.valueOf(id);
    }

    private static void matchBroadcasters(
        @NonNull BroadcasterService broadcasterService,
        @NonNull MediaObject mediaObject,
        @Nullable Set<MediaObject> handled) throws NotFoundException {
        if (handled == null) {
            handled = new HashSet<>(); // to avoid accidental stack overflows
        }
        if (!handled.contains(mediaObject)) {
            handled.add(mediaObject);
            List<Broadcaster> copy = new ArrayList<>(mediaObject.getBroadcasters());


            mediaObject.getBroadcasters().clear();

            for (Broadcaster b : copy) {
                if (b.getId() != null) {
                    mediaObject.addBroadcaster(broadcasterService.find(b.getId()));
                } else if (b.getWhatsOnId() != null) {
                    mediaObject.addBroadcaster(broadcasterService.findForWhatsOnId(b.getWhatsOnId()));
                } else if (b.getNeboId() != null) {
                    mediaObject.addBroadcaster(broadcasterService.findForNeboId(b.getNeboId()));
                } else {
                    mediaObject.addBroadcaster(b);
                }
            }
            for (MemberRef memberRef : mediaObject.getMemberOf()) {
                if (memberRef.getGroup() != null) {
                    matchBroadcasters(broadcasterService, memberRef.getGroup(), handled);
                }
            }
            if (mediaObject instanceof Program p) {
                for (MemberRef memberRef : p.getEpisodeOf()) {
                    if (memberRef.getGroup() != null) {
                        matchBroadcasters(broadcasterService, memberRef.getGroup(), handled);
                    }
                }
            }
        }
    }

    public static void removeLocations(@NonNull MediaObject mediaObject) {
        while (!mediaObject.getLocations().isEmpty()) {
            mediaObject.removeLocation(mediaObject.getLocations().first());
        }
    }

    public static void addAll(@NonNull MediaObject mediaObject, Iterable<Location> i) {
        for (Location l : i) {
            mediaObject.addLocation(l);
        }
    }



    public static Instant getSortInstant(@NonNull MediaObject mo) {
        if (mo instanceof Group) {
            return mo.sortInstant;
        } else if (mo instanceof Segment segment) {
            if (segment.parent != null) {
                return getSortInstant(segment.parent);
            }
        }
        Instant date = null;
        if (mo instanceof Program p) {

            if (p.scheduleEvents != null && !p.scheduleEvents.isEmpty()) {
                List<ScheduleEvent> list = new ArrayList<>(p.scheduleEvents);
                list.sort(Collections.reverseOrder());
                date = list.stream()
                    .filter(ScheduleEvents::isOriginal).findFirst()
                    // TODO for groups we also filter before now. Shouldn't we do that for programs too?
                    // .filter(se -> se.getStartInstant().isBefore(Instant.now))
                    .map(ScheduleEvent::getStartInstant)
                    .orElse(null);
            }
        }
        if (date == null) {
            if (mo.predictions != null && !mo.predictions.isEmpty()) {
                for (Prediction p : mo.predictions) {
                    if (p.getPublishStartInstant() != null && (date == null || p.getPublishStartInstant().isBefore(date))) {
                        date = p.getPublishStartInstant();
                    }
                }
            }
        }
        if (date == null) {
            date = mo.getPublishStartInstant();
        }
        if (date == null) {
            date = mo.getCreationInstant();
        }
        return date;

    }


    public static boolean trim(@NonNull Collection<?> collection) {
        boolean trimmed = false;
        for (java.util.Iterator<?> iterator = collection.iterator(); iterator.hasNext(); ) {
            Object next = iterator.next();
            if (next == null) {
                iterator.remove();
                trimmed = true;
            }
        }

        return trimmed;
    }

    @SuppressWarnings("SuspiciousListRemoveInLoop")
    public static <T extends UpdatableIdentifiable<?, T>> void integrate(
        @NonNull List<T> existing,
        @NonNull List<T> updates) {
        T move = null;
        for (int i = 0; i < updates.size(); i++) {
            T incoming = updates.get(i);
            if (move != null || i < existing.size()) {
                T target = move != null ? move : existing.get(i);
                move = null;

                if (incoming.getId() == null) {
                    existing.set(i, incoming);
                    move = target;
                } else if (incoming.getId().equals(target.getId())) {
                    target.update(incoming);
                } else {
                    existing.set(i, incoming);
                }
            } else {
                existing.add(incoming);
            }

        }

        for (int i = updates.size(); i < existing.size(); i++) {
            existing.remove(i);
        }
    }

    public static boolean markForRepublication(
        @NonNull MediaObject media,
        String reason,
        Object... args) {

        Workflow workflow = media.getWorkflow();
        if ((Workflow.MERGED.equals(workflow) || Workflow.PUBLISHED.equals(workflow) || FOR_REPUBLICATION.equals(workflow)) && media.inPublicationWindow(instant())) {
            media.setWorkflow(Workflow.FOR_REPUBLICATION);
            appendReason(media, reason, args);
            media.setRepubDestinations(null);
            return true;
        } else {
            // not published or publishable so no need for republication after change
            return false;
        }
    }

    public static boolean markForDeletionIfNeeded(
        @NonNull MediaObject media,
        @Pattern(regexp= "[a-z, ]+", flags = {Pattern.Flag.CASE_INSENSITIVE}) String reason) {
        if (! Workflow.DELETES.contains(media.getWorkflow())) {
            media.setWorkflow(Workflow.FOR_DELETION);
            appendReason(media, reason);
            media.setRepubDestinations(null);
            return true;
        } else {
            return false;
        }
    }
    protected static void appendReason(MediaObject media, String reason, Object... args) {
        if (StringUtils.isNotBlank(reason)) {
            final String formattedReason =  MessageFormatter.arrayFormat(reason, args).getMessage();
            final String existingReason = media.getRepubReason();
            if (StringUtils.isBlank(existingReason)) {
                media.setRepubReason(formattedReason);
            } else {
                // add via a set, to avoid appending a reason that is there already
                TreeSet<String> set = Arrays.stream(existingReason.split(REASON_SPLITTER))
                    .collect(Collectors.toCollection(TreeSet::new));
                set.add(formattedReason);
                media.setRepubReason(String.join(REASON_SPLITTER, set));
            }
        }
    }

    protected static void unappendReason(MediaObject media, Predicate<String> reason) {
        final String existingReason = media.getRepubReason();
        if (StringUtils.isNotBlank(existingReason)) {
            // add via a set, to avoid appending a reason that is there already
            TreeSet<String> set = Arrays.stream(existingReason.split(REASON_SPLITTER))
                .collect(Collectors.toCollection(TreeSet::new));
            set.removeIf(reason);
            media.setRepubReason(String.join(REASON_SPLITTER, set));
        }
    }

    public static boolean markForUnDeletionIfNeeded(@NonNull MediaObject media, String reason) {
        if (CollectionUtils.inCollection(Workflow.DELETES, media.getWorkflow())) {
            media.setWorkflow(Workflow.FOR_REPUBLICATION);
            log.info("Marked {} for undeletion", media);
            appendReason(media, reason);
            media.setRepubDestinations(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Marks the fields of the media object related to publishing as published.
     * <p>
     * the last publish instant will be set, and the reason and destinations will be cleared.
     * <p>
     * The workflow itself will remain untouched, and would be set to {@link Workflow#PUBLISHED}, {@link Workflow#REVOKED} or {@link Workflow#MERGED}
     * @see #setWorkflowPublished(MediaObject)
     */
    public static void markPublished(@NonNull MediaObject media, @NonNull Instant now) {
        media.setLastPublishedInstant(now);
        media.setRepubReason(null);
        media.setRepubDestinations(null);
    }


    /**
     * Sets the workflow of the mediaobject, and its subobjects to appropriate value for current date.
     */
    public static  Workflow setWorkflowPublished(@NonNull MediaObject media) {
        Workflow previous = media.getWorkflow();
        if(media.isMerged()) {
            log.warn("Published a MERGED mediaobject {}. This is unexpected, it should have been revoked", media);
            MediaObjectAccess.setWorkflow(media, Workflow.MERGED);
        } else {
            MediaObjectAccess.setWorkflow(media, Workflow.PUBLISHED);
        }
        MediaObjectAccess.setSubtitlesWorkflow(media,
            MediaObjects.subtitlesMayBePublished(media) ? AvailableSubtitlesWorkflow.PUBLISHED : AvailableSubtitlesWorkflow.REVOKED);

        setWorkflowPublishedSubObjects(media.getLocations());
        setWorkflowPublishedSubObjects(media.getImages());
        return previous;
    }


    private static void setWorkflowPublishedSubObjects(Collection<? extends PublishableObject<?>> publishables) {
        for(PublishableObject<?> po : publishables) {
            if (po.isConsiderableForPublication()) {
                if (po.isPublishable(instant())) {
                    PublishableObjectAccess.setWorkflow(po, Workflow.PUBLISHED);
                    continue;
                } else {
                    if (!po.isDeleted()) {
                        PublishableObjectAccess.setWorkflow(po, Workflow.REVOKED);
                        continue;
                    }
                }
                if (!po.getWorkflow().isPublishable()) {
                    log.warn("Encountered unpublished workflow in {} (should this not be fixed?)", po);
                    //PublishableObjectAccess.markPublished(po); ??
                }
            }
        }
    }



    /**
     * @since 7.10
     */
    public static int removeUnpublishedSubObjects(MediaObject media) {
        int result = 0;
        result += removeIf(media.getAvailableSubtitles(), a -> a.getWorkflow() != SubtitlesWorkflow.PUBLISHED);
        result += removeIf(media.getLocations(), l -> l.getWorkflow() != Workflow.PUBLISHED);
        result += removeIf(media.getImages(), i -> i.getWorkflow() != Workflow.PUBLISHED);

        return result;
    }


    /**
     * Sets the workflow of the media object to the 'published' state version of the workflow ({@link Workflow#isPublishable()} ()}
     * <p>
     * And calls {@link #markPublished(MediaObject, Instant)}
     * @since 5.20.2
     */

    public static void markPublishedAndSetCorrectWorkflow(@NonNull MediaObject media, @NonNull Instant now) {
        markPublished(media, now);
        if (media.isMerged()) {
            media.setWorkflow(MERGED);
        } else if (media.isDeleted()) {
            media.setWorkflow(DELETED);
        } else if (media.getWorkflow() == PARENT_REVOKED) {
            media.setWorkflow(PARENT_REVOKED);
        } else if (media.isPublishable(now)) {
            media.setWorkflow(PUBLISHED);
        } else {
            media.setWorkflow(REVOKED);
        }
    }

    @Nullable
    public static Prediction getPrediction(Platform platform, @Nullable Collection<Prediction> preds) {
        if (preds != null) {
            for (Prediction prediction : preds) {
                if (Objects.equals(prediction.getPlatform(), platform)) {
                    return prediction;
                }
            }
        }
        return null;
    }

    /**
     * Determines if for a given platform, the media object has a location, and returns (one) if there is.
     * If there is no location for a certain platform the {@link Prediction#getState()} should be {@link Prediction.State#REVOKED}, {@link Prediction.State#ANNOUNCED} or {@link Prediction.State#NOT_ANNOUNCED}.
     * <p>
     * Otherwise, it must be {@link Prediction.State#REALIZED};
     *
     * @since 5.32
     */
    public static Optional<Location> getAvailableLocation(Platform platform, MediaObject m, Instant now) {
        for (Location l : m.getLocations()) {
            if (l.isPublishable(now)) {
                if (platform.matches(l.getPlatform())) {
                    return Optional.of(l);
                }
            }
        }
        return Optional.empty();
    }

    static List<String> getPlannedPlatformNamesInLowerCase(Collection<Prediction> predictions) {
        if (predictions != null) {
            return predictions.stream()
                .filter(Prediction::isPlannedAvailability)
                .map(Prediction::getPlatform)
                .map(Platform::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        }
        return Arrays.asList();
    }

    public static List<String> getPlannedPlatformNamesInLowerCaseOrAll(MediaObject media) {
        List<String> result = getPlannedPlatformNamesInLowerCase(media.getPredictions());
        if (result == null || result.isEmpty()) {
            result = Arrays.stream(Platform.values()).map((p) -> p.name().toLowerCase()).collect(Collectors.toList());
            log.info("No available platforms for {}, taking {}", media, result);
        }
        return result;
    }


    public static Prediction getPredictionOrNew(Platform platform, Collection<Prediction> preds, Function<Platform, Prediction> constructor) {
        Prediction p = getPrediction(platform, preds);
        if (p == null){
            return constructor.apply(platform);
        } else {
            return p;
        }
    }


    public static Prediction updatePrediction(MediaObject media, Platform platform, Prediction.State state) {
        Prediction prediction = media.findOrCreatePrediction(platform);
        prediction.setState(state);
        return prediction;
    }

    public static Prediction updatePrediction(MediaObject media, Platform platform, Embargo embargo, Encryption drm) {
        Prediction prediction = media.findOrCreatePrediction(platform);
        prediction.setEncryption(drm);
        Embargos.copy(embargo, prediction);
        return prediction;
    }



    public static boolean subtitlesMayBePublished(MediaObject media) {
        return media != null
            && CollectionUtils.inCollection(PUBLICATIONS, media.getWorkflow())
            && media.getLocations().stream().anyMatch(l -> l.getWorkflow() == PUBLISHED);
    }


    /**
     * Filters a PublishableObject. Removes all subobject which dont' have a correct workflow.
     * <p>
     * TODO work in progress. This may replace the hibernate filter solution now in place.
     */
    public static <T extends PublishableObject<?>> T filterPublishable(T object, Instant now) {
        Predicate<Object> p = (o) -> {
            if (o instanceof PublishableObject) {
                return ((PublishableObject<?>) o).isPublishable(now);
            } else {
                return true;
            }
        };
        ObjectFilter.Result<T> result = ObjectFilter.filter(object, p);
        log.debug("Filtered {} from {}", result.filterCount(), result.get());
        return result.get();
    }

    public static <T extends PublishableObject<?>> T filterOnWorkflow(T object, Predicate<Workflow> predicate) {
        Predicate<Object> p = (o) -> {
            if (o instanceof PublishableObject) {
                return predicate.test(((PublishableObject<?>) o).getWorkflow());
            } else {
                return true;
            }
        };
        ObjectFilter.Result<T> result = ObjectFilter.filter(object, p);
        log.debug("Filtered {} from {}", result.filterCount(), result.get());
        return result.get();
    }



    public static boolean isWebonly(MediaObject media) {
        return media.getMediaType() == MediaType.CLIP;
    }


    /**
     * TODO: javadoc
     */
    public static Optional<List<MemberRef>> getPath(MediaObject parent, MediaObject child, List<? extends MediaObject> descendants) {
        return getPath(parent, child,
            descendants.stream().distinct().collect(Collectors.toMap(MediaObject::getMid, d -> d)
            )
        );
    }


    public static <T extends MediaObject>  void  updateLocationsForOwner(T incomingMedia, T mediaToUpdate, Predicate<Ownable> owns, OwnerType owner, boolean steal) {
        for(Location incomingLocation : incomingMedia.getLocations()) {

            if(owns.test(incomingLocation)) {
                Location locationToUpdate = mediaToUpdate.findLocation(incomingLocation.getProgramUrl());
                if(locationToUpdate == null) {
                    mediaToUpdate.addLocation(incomingLocation);
                    incomingLocation.headRequest();
                } else {
                    boolean update = true;
                    if (locationToUpdate.getOwner() != owner) {
                        if (steal) {
                            log.warn("Updating ownership of location {} -> {}", locationToUpdate, owner);
                            locationToUpdate.setOwner(owner);
                        } else {
                            update = false;
                            log.warn("Cannot update location {} since it is not from {}", locationToUpdate, owner);
                        }
                    }
                    if (update) {
                        locationToUpdate.setDuration(incomingLocation.getDuration());
                        locationToUpdate.setOffset(incomingLocation.getOffset());
                        locationToUpdate.setSubtitles(incomingLocation.getSubtitles());
                        Embargos.copy(incomingLocation, locationToUpdate);
                        mergeAvAttributes(incomingLocation, locationToUpdate);
                        locationToUpdate.headRequest();
                        if (DELETES.contains(incomingLocation.getWorkflow())) {
                            locationToUpdate.setWorkflow(FOR_DELETION);
                        } else {
                            locationToUpdate.setWorkflow(FOR_PUBLICATION);
                        }
                    }
                }
            }
        }
    }

    public static <T extends MediaObject>  void  updateLocationsForOwner(T incomingMedia, T mediaToUpdate, OwnerType owner, boolean steal) {
        updateLocationsForOwner(incomingMedia, mediaToUpdate, o -> o.getOwner() == owner, owner, steal);
    }



    public static <T extends MediaObject>  List<Location>  updateAndRemoveLocationsForOwner(T incomingMedia, T mediaToUpdate,  Predicate<Ownable> owns, OwnerType owner) {
        updateLocationsForOwner(incomingMedia, mediaToUpdate, owns, owner, false);
        List<Location> locationsToRemove = new ArrayList<>();
        mediaToUpdate.getLocations().removeIf(
            location ->
                owns.test(location) &&
                incomingMedia.findLocation(location.getProgramUrl(), owner) == null
        );
        return locationsToRemove;
    }

    public static <T extends MediaObject>  List<Location>  updateAndRemoveLocationsForOwner(T incomingMedia, T mediaToUpdate,  OwnerType owner) {
        return updateAndRemoveLocationsForOwner(incomingMedia, mediaToUpdate, o -> o.getOwner() == owner, owner);

    }


    public static void mergeAvAttributes(Location incomingLocation, Location locationToUpdate) {
        final AVAttributes incomingAttributes = incomingLocation.getAvAttributes();
        final AVAttributes attributesToUpdate = locationToUpdate.getAvAttributes();

        if(incomingAttributes != null && attributesToUpdate != null) {
            attributesToUpdate.setAvFileFormat(incomingAttributes.getAvFileFormat());
            attributesToUpdate.setBitrate(incomingAttributes.getBitrate());

            mergeAudioAttributes(incomingAttributes, attributesToUpdate);
            mergeVideoAttributes(incomingAttributes, attributesToUpdate);

        } else if(incomingAttributes != null) {

            locationToUpdate.setAvAttributes(incomingAttributes);

        } else if(attributesToUpdate != null) {

            locationToUpdate.setAvAttributes(null);

        }
    }



    public static void mergeAudioAttributes(AVAttributes incomingAttributes, AVAttributes attributesToUpdate) {
        AudioAttributes incomingAudio = incomingAttributes.getAudioAttributes();
        AudioAttributes audioToUpdate = attributesToUpdate.getAudioAttributes();

        if(incomingAudio != null && audioToUpdate != null) {
            audioToUpdate.setAudioCoding(incomingAudio.getAudioCoding());
            audioToUpdate.setLanguage(incomingAudio.getLanguage());
            audioToUpdate.setNumberOfChannels(incomingAudio.getNumberOfChannels());
        } else if(incomingAudio != null) {
            attributesToUpdate.setAudioAttributes(incomingAudio);
        } else if(audioToUpdate != null) {
            attributesToUpdate.setAudioAttributes(null);

        }
    }


    public static void mergeVideoAttributes(AVAttributes incomingAttributes, AVAttributes attributesToUpdate) {
        VideoAttributes incomingVideo = incomingAttributes.getVideoAttributes();
        VideoAttributes videoToUpdate = attributesToUpdate.getVideoAttributes();

        if(incomingVideo != null && videoToUpdate != null) {

            videoToUpdate.setAspectRatio(incomingVideo.getAspectRatio());
            videoToUpdate.setHorizontalSize(incomingVideo.getHorizontalSize());
            videoToUpdate.setVerticalSize(incomingVideo.getVerticalSize());
            videoToUpdate.setVideoCoding(incomingVideo.getVideoCoding());

        } else if(incomingVideo != null) {

            attributesToUpdate.setVideoAttributes(incomingVideo);

        } else if(videoToUpdate != null) {

            attributesToUpdate.setVideoAttributes(null);

        }
    }

    protected static Optional<List<MemberRef>> getPath(MediaObject parent, MediaObject child, Map<String, MediaObject> descendants) {
        for (MemberRef ref : getMemberRefs(child)) {
            if (ref.getMidRef().equals(parent.getMid())) {
                // hit!
                return Optional.of(Collections.singletonList(ref));
            }
        }
        // Not directly found, so it is indirect
        List<MemberRef> proposal = null; // we want the shortest
        for (MemberRef ref : getMemberRefs(child)) {
            MediaObject c = descendants.get(ref.getMidRef());
            if (c != null) {
                Optional<List<MemberRef>> path = getPath(parent, c, descendants);
                if (path.isPresent()) {
                    List<MemberRef> result = new ArrayList<>();
                    result.add(ref);
                    result.addAll(path.get());
                    if (proposal == null || (result.size() < proposal.size())) {
                        proposal = result;
                    }
                }
            }
        }

        return Optional.ofNullable(proposal);
    }

    protected static Iterable<MemberRef> getMemberRefs(MediaObject o) {
        Iterable<MemberRef> memberOf = o.getMemberOf();
        if (o instanceof Program) {
            return Iterables.concat(memberOf, ((Program) o).getEpisodeOf());
        } else {
            return memberOf;
        }
    }

    public static List<Person> getPersons(MediaObject o) {
        return o.getPersons();
    }


    public static Stream<GTAARecord> getGTAARecords(MediaObject media) {
        return Streams.concat(
            media.getGeoLocations()
                .stream()
                .map(GeoLocations::getValues)
                .flatMap(Collection::stream)
                .map(GeoLocation::getGtaaRecord),
            media.getCredits()
                .stream()
                .filter(c -> ! (c instanceof Person))
                .map(c -> ((Name) c).getGtaaRecord()),
            media.getTopics()
                .stream()
                .map(Topics::getValues)
                .flatMap(Collection::stream)
                .map(Topic::getGtaaRecord)
        ).distinct();
    }

    /**
     * Whether this media object is playable in a NPO player.
     * @since 5.11
     */
    public static boolean isPlayable(MediaObject media) {
        if (media == null) {
            return false;
        }
        boolean selfIsPlayable = ! nowPlayable(media).isEmpty();
        if (selfIsPlayable) {
            return true;
        }

        // this could only work on backend.
        if (media instanceof Segment segment) {
            return isPlayable(segment.getParent());
        } else {
            return false;
        }
    }


    /**
     * Whether the given mediaobject is now playable at given platform
     * @since 5.31
     */
    public static boolean nowPlayable(@NonNull Platform platform, @NonNull MediaObject mediaObject) {
        return  mediaObject.getLocations()
            .stream()
            .filter(MediaObjects::locationFilter)
            .anyMatch(
                l -> platform.matches(l.getPlatform()) && l.inPublicationWindow()
        );
    }

    /**
     * On which platform the given mediaobject is currently playable.
     * @since 5.31
     */
    public static Set<Platform> nowPlayable(@NonNull MediaObject mediaObject) {
        return Arrays.stream(Platform.values())
            .filter(p -> nowPlayable(p, mediaObject))

            .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * @since 5.31
     */
    public static boolean wasPlayable(@NonNull Platform platform, @NonNull MediaObject mediaObject) {
        if (nowPlayable(platform, mediaObject)) {
            return false;
        }
        Optional<Prediction> prediction = mediaObject.getPredictions()
            .stream()
            .filter(p -> p.getPlatform() == platform)
            .findFirst();
        if (prediction.isPresent()) {
            return prediction.get().getState() == Prediction.State.REVOKED;
        } else { // may be the locations are visible (on the backend) but not yet published
            return mediaObject.getLocations()
                .stream()
                .filter(l -> platform.matches(l.getPlatform()))
                .anyMatch(Embargo::wasUnderEmbargo);
        }
    }

    /**
     * @since 5.31
     */
    public static Set<Platform> wasPlayable(@NonNull MediaObject mediaObject) {
        return Arrays.stream(Platform.values())
            .filter(p -> wasPlayable(p, mediaObject))
            .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Returns whether the mediaobject for given platform is now not playable, but will be.
     * @since 5.31
     */
    public static boolean willBePlayable(@NonNull Platform platform, @NonNull MediaObject mediaObject) {
        if (nowPlayable(platform, mediaObject)) {
            return false;
        }
        Optional<Prediction> prediction = mediaObject.getPredictions()
            .stream()
            .filter(p -> p.getPlatform() == platform)
            .findFirst();
        if (prediction.isPresent()) {
            return prediction.get().getState() == Prediction.State.ANNOUNCED;
        } else { // may be the locations are visible (on the backend) but not yet published
            return mediaObject.getLocations()
                .stream()
                .filter(l -> platform.matches(l.getPlatform()))
                .anyMatch(Embargo::willBePublished);
        }
    }

    /**
     * Determines for a certain {@link Platform}s and {@code MediaObject} when it might become playable.
     * @since 5.31
     */
    public static Optional<LocalDateTime> willBePlayableAt(@NonNull Platform platform, @Nullable MediaObject mediaObject) {
        if (mediaObject == null) {
            return Optional.empty();
        }
        if (! willBePlayable(platform, mediaObject)) {
            return Optional.empty();
        }
        Prediction pred = mediaObject.getPrediction(platform);
        if (pred != null && pred.isPlannedAvailability()) {
            if (pred.getPublishStartInstant() == null) {
                return Optional.of(LocalDateTime.now().plus(Duration.ofDays(1000)).truncatedTo(ChronoUnit.DAYS));
            } else {
                return Optional.of(pred.getPublishStartInstant().atZone(Schedule.ZONE_ID).toLocalDateTime());
            }
        } else {
            return Optional.empty();
        }
    }

    /**
     * Determines on which {@link Platform}s the given {@code MediaObject} {@link #willBePlayable(Platform, MediaObject) will be playable}.
     * @since 5.31
     */
    public static Set<Platform> willBePlayable(@NonNull MediaObject mediaObject) {
        return Arrays.stream(Platform.values())
            .filter(p -> willBePlayable(p, mediaObject))
            .collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * Returns for a certain platform the range it which a mediaobject is playable.
     *
     * @return An {@link Optional} of a {@link Range} of {@link Instant}. The optional is empty if the mediaobject was never announced, and is, was and probably will be unplayable.
     * @since 5.31
     */
    public static Optional<Range<Instant>> playableRange(@NonNull Platform platform, @NonNull MediaObject mediaObject) {
        List<Location> locations = mediaObject.getLocations().stream()
            .filter(l -> platform.matches(l.getPlatform()))
            .filter(MediaObjects::locationFilter)
            .toList();
        if (locations.isEmpty()) {
            // no locations. Maybe,  there really are none, or perhaps they are not published
            // we can juse look at the prediction record
            Prediction prediction = mediaObject.getPrediction(platform);
            if (prediction == null) {
                return Optional.empty();
            } else {
                Range<Instant> range = prediction.asRange();
                if (range.contains(instant())) {
                    // no playable locations, but still the prediction is not under embargo
                    // this means that the available locations are unfit because of ::locationFilter
                    return Optional.empty();

                } else {
                    return Optional.of(range);
                }
            }
        } else {
            // we found relevant locations. So, it is playable in the span of their ranges (unless they don't overlap, but that cannot be covered by a single range)
            Range<Instant> result = null;
            for (Location location : locations) {
                Range<Instant> range = location.asRange();
                if (result == null) {
                    result = range;
                } else {
                    result = result.span(range);
                }
            }
            return Optional.of(result);
        }
    }

    /**
     * Given a {@code MediaObject} returns a map with for every platform for which that is relevant a {@link Range} of {@link LocalDateTime}  is return indicating the period this object is playable at that platform
     *
     * @param zoneId The timezone for which this must be evaluated or {@code null}, to fall back to {@link Schedule#ZONE_ID}
     * @since 5.31
     */
    public static Map<Platform, Range<LocalDateTime>> playableRanges(@NonNull MediaObject mediaObject, ZoneId zoneId) {
        final ZoneId finalZoneId = zoneId== null? Schedule.ZONE_ID : zoneId;
        return playableRanges(mediaObject).entrySet()
            .stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> DateUtils.toLocalDateTimeRange(e.getValue(), finalZoneId))
            );
    }

    /**
     * As {@link #playableRanges(MediaObject, ZoneId)}, but returning ranges of {@link Instant}, indicating absolute times.
     * @see #playableRanges(MediaObject, ZoneId)
     * @since 5.31
     */
    public static Map<Platform, Range<Instant>> playableRanges(@NonNull MediaObject mediaObject) {
        final Map<Platform, Range<Instant>> result = new TreeMap<>();
        Arrays.stream(Platform.values()).forEach(p ->
            playableRange(p, mediaObject)
                .ifPresent(r -> result.put(p, r)
            )
        );
        return Collections.unmodifiableMap(result);
    }

    static final Set<AVFileFormat> ACCEPTABLE_FORMATS = Set.of(
        AVFileFormat.MP3,
        AVFileFormat.MP4,
        AVFileFormat.M4V,
        AVFileFormat.H264,
        AVFileFormat.HASP,
        AVFileFormat.MPEG2
    );

    // every location with this scheme is considered playable
    static final Set<String> ACCEPTABLE_SCHEMES = Set.of("npo+drm", "npo");


    /**
     * Return {@code false} if the given location is not actually playable.
     * <p>
     * Either it is {@link Location#isDeleted()}, which may occur if dealing with unpublished data, or we're dealing
     * with some legacy and the location has a format which is known not to be playable any-more (like WMV)
     * @since 5.31
     */
    protected static boolean locationFilter(Location l) {
        if (l.isDeleted()) {
            return false;
        }
        final String scheme = l.getScheme();
        if (scheme != null && ACCEPTABLE_SCHEMES.contains(scheme)) {
            log.debug("Matched {} on scheme {}", l, scheme);
            return true;
        }

        // legacy filter on av type
        AVFileFormat format = l.getAvFileFormat();
        if (format == null || format == AVFileFormat.UNKNOWN) {
            format = AVFileFormat.forProgramUrl(l.getProgramUrl());
        }
        if (format != null && format != AVFileFormat.UNKNOWN) {
            boolean acceptable = ACCEPTABLE_FORMATS.contains(format);
            if (!acceptable) {
                log.debug("Ignoring {} since {} not in {}", l, format, ACCEPTABLE_FORMATS);
                return false;
            }
        }
        return true;
    }



    public static boolean autoCorrectPredictions = true;

    /**
     *  // TODO: I think is is a bit odd that this kind of logic happens here.
     *  It ensures consistency, that's the good thing, but it seems a patch any way!
     */
    protected static void autoCorrectPrediction(Prediction prediction, MediaObject mediaObject) {
        if (autoCorrectPredictions) {
            correctPrediction(prediction, mediaObject, Level.INFO, instant(), (prevState, p) -> {});
        }
    }

    protected static void correctPrediction(final Prediction prediction, MediaObject mediaObject, Level level, Instant now, BiConsumer<Prediction.State, Prediction> onChange) {
         final Prediction.State prevState = prediction.getState();
         switch (prevState) {
             case ANNOUNCED, REVOKED -> {
                 boolean allInPast = true;
                 boolean hasLocations = false;
                 boolean realized = false;
                 for (Location location : mediaObject.getLocations()) {
                     if ( ! prediction.getPlatform().matches(location.getPlatform())) {
                         continue;
                     }
                     if (location.isDeleted()) {
                         continue;
                     }
                     hasLocations = true;
                     if (location.isConsiderableForPublication() && ! location.wasUnderEmbargo(now)) {
                         allInPast = false;
                     }
                     if (Workflow.PUBLICATIONS.contains(location.getWorkflow())
                         && location.inPublicationWindow(now)
                     ) {
                         prediction.setState(Prediction.State.REALIZED);
                         realized = true;
                         Slf4jHelper.log(log, level, "Set state of {} from {} to REALIZED (by {}) of object {}", prediction, prevState, location.getProgramUrl(), mediaObject.mid);
                         onChange.accept(prevState, prediction);
                         String reason = PublicationReason.Reasons.REALIZED_PREDICTION.formatted(prediction.getPlatform().name());
                         if (prediction.getPreviousState() == prediction.getState()) {
                             unappendReason(mediaObject, (r) -> r.equals(reason));
                         } else {
                             appendReason(mediaObject, reason);
                         }
                         break;
                     }
                 }
                 if (hasLocations && ! realized && allInPast && prediction.getState() == Prediction.State.ANNOUNCED) {
                     prediction.setState(Prediction.State.REVOKED);
                     Slf4jHelper.log(log, level, "Set state of {} from {} to REVOKED of object {} (realized: {}, all in past: {})", prediction, prevState, mediaObject.mid, realized, allInPast);
                     onChange.accept(prevState, prediction);
                     String reason = PublicationReason.Reasons.REVOKED_PREDICTION.formatted(prediction.getPlatform().name());
                     if (prediction.getPreviousState() == prediction.getState()) {
                         unappendReason(mediaObject, (r) -> r.equals(reason));
                     } else {
                         appendReason(mediaObject, reason);
                     }
                 }
             }
             case REALIZED -> {
                 // Are there any 'REALIZED' prediction without locations that can be played anyway?
                 // select *  from prediction p inner join location l on p.mediaobject_id = l.mediaobject_id and p.platform = l.platform and l.workflow = 'PUBLISHED' where p.state = 'REALIZED'  and l is null;
                 // -> no

                 Optional<Location> matchingLocation = mediaObject.getLocations().stream()
                     .filter(l -> prediction.getPlatform().matches(l.getPlatform()))
                     .filter(l -> Workflow.PUBLICATIONS.contains(l.getWorkflow()))
                     .filter(l -> l.inPublicationWindow(now))
                     .findFirst();
                 if (matchingLocation.isEmpty()) {
                     final List<Location> withoutFilter = mediaObject.getLocations().stream()
                         .filter(l -> prediction.getPlatform().matches(l.getPlatform()))
                         .filter(l -> Workflow.PUBLICATIONS.contains(l.getWorkflow()))
                         .toList();
                     Slf4jHelper.log(log, withoutFilter.isEmpty() ? Level.INFO: Level.WARN, "Set state of {} to REVOKED of object {} (no matching locations found {})", prediction, mediaObject.mid, withoutFilter.isEmpty() ? "" : "(ignored: %s)".formatted(withoutFilter));
                     prediction.setState(Prediction.State.REVOKED);
                     onChange.accept(prevState, prediction);
                     String reason = PublicationReason.Reasons.REVOKED_PREDICTION.formatted(prediction.getPlatform().name());
                     if (prediction.getPreviousState() == prediction.getState()) {
                         unappendReason(mediaObject, (r) -> r.equals(reason));
                     } else {
                         appendReason(mediaObject, reason);
                     }
                 }

             }
             default -> {
                 log.debug("Ignoring prediction {}", prediction);
             }
         }
    }

}
