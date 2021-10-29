/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.helpers.MessageFormatter;

import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;

import nl.vpro.domain.*;
import nl.vpro.domain.media.gtaa.GTAARecord;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.util.ObjectFilter;

import static nl.vpro.domain.Embargos.CLOCK;
import static nl.vpro.domain.media.support.Workflow.*;


/**
 * Various methods related to dealing with {@link MediaObject}s, like copying and filling.
 *
 * See {@link TextualObjects}, and {@link Embargos} for methods like this (because media objects are {@link TextualObject} and {@link MutableEmbargo}
 * @since 1.5
 */
@Slf4j
public class MediaObjects {

    private MediaObjects() {
    }


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
     *
     * In principal this should be all fields of which the value logically can exist on more than one mediaobject the same time,
     * so not unique fields like id and mid.
     *
     * Also membership of groups will not be automatically filled. This would need write access on those objects.
     *
     * Scheduleevents, predictions, workflow, subtitles status are not copied too, since this this would not make sense.
     *
     *
     *
     */
    public static void copy(@NonNull MediaObject from, @NonNull MediaObject to) {
        Embargos.copy(from, to);
        TextualObjects.copy(from, to);

        to.setAgeRating(from.getAgeRating());
        to.setAvAttributes(from.getAvAttributes());
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
        to.setLocations(from.getLocations());

        //to.setMediaType(from.getMediaType());
        //to.setMemberOf();

        to.setCredits(from.getCredits());
        to.setPortalRestrictions(from.getPortalRestrictions());
        to.setPortals(from.getPortals());
        //to.setPredictions();
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
     * A more full copy, also copying field that you could normally not copy, like MID.
     *
     * The assumption is that both objects are not yet persistent
     * @since 5.11
     */
    public static void copyFull(@NonNull MediaObject from, @NonNull MediaObject to) {
        copy(from, to);
        to.setMid(from.getMid());
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
     *
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
     *
     * An empty collection for all non programs.
     *
     * @since 5.11
     */
    public static SortedSet<ScheduleEvent> getScheduleEvents(MediaObject media) {
        if (media instanceof Program) {
            return ((Program) media).getScheduleEvents();
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
                case _6:
                    sb.append('2');
                    break;
                case _9:
                    sb.append('5');
                    break;
                case _12:
                    sb.append('3');
                    break;
                case _16:
                    sb.append('4');
                    break;
            }
        }

        for (ContentRating contentRating : media.getContentRatings()) {
            if (contentRating != null) {
                sb.append(contentRating.toChar());
            }
        }

        return sb.length() > 0 ? sb.toString() : null;
    }

    public static Long idFromUrn(String urn) {
        final String id = urn.substring(urn.lastIndexOf(':') + 1);
        return Long.valueOf(id);
    }


    private static void matchBroadcasters(@NonNull  BroadcasterService broadcasterService, @NonNull  MediaObject mediaObject, @NonNull Set<MediaObject> handled) throws NotFoundException {
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
                matchBroadcasters(broadcasterService, memberRef.getGroup(), handled);
            }
            if (mediaObject instanceof Program) {
                Program p = (Program) mediaObject;
                for (MemberRef memberRef : p.getEpisodeOf()) {
                    matchBroadcasters(broadcasterService, memberRef.getGroup(), handled);
                }
            }
        }
    }

    public static void removeLocations(@NonNull MediaObject mediaObject) {
        while (mediaObject.getLocations().size() > 0) {
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
        } else if (mo instanceof Segment) {
            Segment segment = (Segment) mo;
            if (segment.parent != null) {
                return getSortInstant(segment.parent);
            }
        }
        Instant date = null;
        if (mo instanceof Program) {
            Program p = (Program) mo;
            if (p.scheduleEvents != null && p.scheduleEvents.size() > 0) {
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
            if (mo.predictions != null && mo.predictions.size() > 0) {
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

    public static <T extends UpdatableIdentifiable<?, T>> void integrate(@NonNull List<T> existing, @NonNull List<T> updates) {
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
        if ((Workflow.MERGED.equals(media.getWorkflow()) || Workflow.PUBLISHED.equals(media.getWorkflow())) && media.inPublicationWindow(CLOCK.instant())) {
            media.setWorkflow(Workflow.FOR_REPUBLICATION);
            appendReason(media, reason, args);
            media.setRepubDestinations(null);
            return true;
        } else {
            appendReason(media, reason);
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
            appendReason(media, reason);
            return false;
        }
    }
    protected static void appendReason(MediaObject media, String reason, Object... args) {
        if (StringUtils.isNotBlank(reason)) {
            String formattedReason =  MessageFormatter.arrayFormat(reason, args).getMessage();
            String existingReason = media.getRepubReason();
            if (StringUtils.isBlank(existingReason)) {
                media.setRepubReason(formattedReason);
            } else {
                TreeSet<String> set = Arrays.stream(existingReason.split(","))
                    .collect(Collectors.toCollection(TreeSet::new));
                set.add(formattedReason);
                media.setRepubReason(String.join(",", set));
            }
        }
    }

    public static boolean markForUnDeletionIfNeeded(@NonNull MediaObject media, String reason) {
        if (Workflow.DELETES.contains(media.getWorkflow())) {
            media.setWorkflow(Workflow.FOR_REPUBLICATION);
            log.info("Marked {} for undeletion", media);
            appendReason(media, reason);
            media.setRepubDestinations(null);
            return true;
        } else {
            appendReason(media, reason);
            return false;
        }
    }

    /**
     * Marks the fields of the mediaobject related to publishing as published.
     *
     * the last publish instant will be set, and the reason and destinations will be cleared.
     *
     * The workflow itself will remain untouched, and would be set to {@link Workflow#PUBLISHED}, {@link Workflow#REVOKED} or {@link Workflow#MERGED}
     */
    public static void markPublished(@NonNull MediaObject media, @NonNull Instant now) {
        media.setLastPublishedInstant(now);
        media.setRepubReason(null);
        media.setRepubDestinations(null);
    }



    /**
     * Sets the workflow of the media object to the 'published' state version of the workflow ({@link Workflow#isPublishable()}
     *
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


    public static Prediction getPrediction(Platform platform, Collection<Prediction> preds) {
        if (preds != null) {
            for (Prediction prediction : preds) {
                if (Objects.equals(prediction.getPlatform(), platform)) {
                    return prediction;
                }
            }
        }
        return null;
    }

    static List<String> getPlannedPlatformNamesInLowerCase(Collection<Prediction> preds) {
        if (preds != null) {
            return preds.stream()
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
        return media != null && PUBLICATIONS.contains(media.getWorkflow()) && media.getLocations().stream().anyMatch(l -> l.getWorkflow() == PUBLISHED);
    }


    /**
     * Filters a PublishableObject. Removes all subobject which dont' have a correct workflow.
     *
     * TODO work in progress. This may replace the hibernate filter solution now in place.
     */
    public static <T extends PublishableObject<?>> T filterPublishable(T object, Instant now) {
        Predicate<Object> p = (o) -> {
            if (o instanceof PublishableObject) {
                return ((PublishableObject) o).isPublishable(now);
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
                return predicate.test(((PublishableObject) o).getWorkflow());
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
     * @TODO: javadoc
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
        AVAttributes incomingAttributes = incomingLocation.getAvAttributes();
        AVAttributes attributesToUpdate = locationToUpdate.getAvAttributes();

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

    public static <T extends PublishableObject<?>> boolean revokeRelatedPublishables(MediaObject media, Collection<T> publishables, Instant now, Runnable callbackOnChange) {
        boolean foundRevokedPublishable = false;
        for(T publishable : publishables) {
            if(Workflow.REVOKES.contains(publishable.getWorkflow())) {
                continue;
            }
            if(!publishable.inPublicationWindow(now)) {
                PublishableObjectAccess.setWorkflow(publishable, Workflow.REVOKED);
                foundRevokedPublishable = true;
            }
        }

        if(foundRevokedPublishable &&
            media.getWorkflow() == Workflow.PUBLISHED &&
            media.inPublicationWindow(now)) {
            callbackOnChange.run();
            return true;
        }
        return false;
    }

    public static boolean revokeRelatedPublishables(MediaObject media, Instant now) {
        boolean result = MediaObjects.revokeRelatedPublishables(media, media.getImages(), now, () -> {});
        result &= MediaObjects.revokeRelatedPublishables(media, media.getLocations(), now, () -> Locations.updatePredictionStates(media, now));
        return result;

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
     * Whether this mediaobject is playable in a NPO player.
     * @since 5.11
     */
    public static boolean isPlayable(MediaObject media) {
        if (media == null) {
            return false;
        }
        // TODO, it seems that this is enough (at least for video?)
        return media.getStreamingPlatformStatus().isAvailable();
        // but why not this?
        //return media.getStreamingPlatformStatus().isAvailable() && Optional.ofNullable(media.getPrediction(Platform.INTERNETVOD)).map(p -> p.inPublicationWindow(Instant.now())).orElse(false);
    }


    /**
     * @since 5.31
     */
    public static boolean nowPlayable(@NonNull Platform platform, @NonNull MediaObject mediaObject) {
        return playabilityCheck(platform, mediaObject, s -> s.getState() == Prediction.State.REALIZED && s.inPublicationWindow(), Embargo::inPublicationWindow);
    }

    /**
     * @since 5.31
     */
    public static Platform[] nowPlayable(@NonNull MediaObject mediaObject) {
        return Arrays.stream(Platform.values()).filter(p -> nowPlayable(p, mediaObject)).toArray(Platform[]::new);
    }

    /**
     * @since 5.31
     */
    public static boolean wasPlayable(@NonNull Platform platform, @NonNull MediaObject mediaObject) {
        return playabilityCheck(platform, mediaObject, s -> s.getState() == Prediction.State.REVOKED, Embargo::wasUnderEmbargo);
    }

    /**
     * @since 5.31
     */
    public static Platform[] wasPlayable(@NonNull MediaObject mediaObject) {
        return Arrays.stream(Platform.values()).filter(p -> wasPlayable(p, mediaObject)).toArray(Platform[]::new);
    }

    /**
     * @since 5.31
     */
    public static boolean willBePlayable(@NonNull Platform platform, @NonNull MediaObject mediaObject) {
        return playabilityCheck(platform, mediaObject, s -> s.getState() == Prediction.State.ANNOUNCED, Embargo::willBePublished);
    }

    /**
     * @since 5.31
     */
    public static Platform[] willBePlayable(@NonNull MediaObject mediaObject) {
        return Arrays.stream(Platform.values()).filter(p -> willBePlayable(p, mediaObject)).toArray(Platform[]::new);
    }

    static final Set<AVFileFormat> ACCEPTABLE_FORMATS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(AVFileFormat.MP3, AVFileFormat.MP4, AVFileFormat.M4V, AVFileFormat.H264)));

    protected static boolean locationFilter(Location l) {
        if (l.isDeleted()) {
            return false;
        }
        // legacy filter on av type
        AVFileFormat format = l.getAvFileFormat();
        if (format == null || format == AVFileFormat.UNKNOWN) {
            format = AVFileFormat.forProgramUrl(l.getProgramUrl());
        }
        if (format != null && format != AVFileFormat.UNKNOWN) {
            boolean acceptable = ACCEPTABLE_FORMATS.contains(format);
            if (!acceptable) {
                log.debug("Ignoring {}", l);
                return false;
            }
        }
        return true;
    }
    /**
     * @since 5.31
     */
    protected static boolean playabilityCheck(@NonNull Platform platform, @NonNull MediaObject mediaObject,  Predicate<Prediction> prediction, Predicate<Location> location) {
        boolean matchedByPrediction = mediaObject.getPredictions().stream().anyMatch(p -> platform.matches(p.getPlatform()) && prediction.test(p));
        if (matchedByPrediction) {
            return true;
        }
        // fall back to location only
        return  mediaObject.getLocations().stream()
            .filter(MediaObjects::locationFilter)
            .anyMatch(l -> platform.matches(l.getPlatform()) && location.test(l));
    }



}
