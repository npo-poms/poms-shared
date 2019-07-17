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

import javax.annotation.Nonnull;

import com.google.common.collect.Iterables;

import nl.vpro.domain.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.MutableOwnable;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.util.DateUtils;
import nl.vpro.util.ObjectFilter;

import static nl.vpro.domain.media.support.Workflow.PUBLICATIONS;
import static nl.vpro.domain.media.support.Workflow.PUBLISHED;


/**
 * Various methods related to dealing with {@link MediaObject}s, like copying and filling.
 *
 * See {@link TextualObjects}, and {@link Embargos} for methods like this (because media objects are {@link TextualObject} and {@link MutableEmbargo}
 * @since 1.5
 */
@Slf4j
public class MediaObjects {



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
    public static void copy(@Nonnull MediaObject from, @Nonnull MediaObject to) {
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

        to.setPersons(from.getPersons());
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
    public static void copyFull(@Nonnull MediaObject from, @Nonnull MediaObject to) {
        copy(from, to);
        to.setMid(from.getMid());
        to.setScheduleEvents(from.getScheduleEvents());
        if (to.getClass().isAssignableFrom(from.getClass())) {
            to.setMediaType(from.getMediaType());
        }
        to.setMemberOf(from.getMemberOf());
        to.setCrids(from.getCrids());

    }



    public static void matchBroadcasters(BroadcasterService broadcasterService, MediaObject mediaObject) throws NotFoundException {
        matchBroadcasters(broadcasterService, mediaObject, null);
    }

    public static boolean hasChannel(MediaObject media, Channel... channels) {
        return hasChannel(media, Arrays.asList(channels));
    }

    public static boolean hasChannel(MediaObject media, Collection<Channel> channels) {
        for (ScheduleEvent scheduleEvent : media.getScheduleEvents()) {
            if (channels.contains(scheduleEvent.getChannel())) {
                return true;
            }
        }
        return false;
    }

    public static ScheduleEvent findScheduleEventHonoringOffset(MediaObject media, ScheduleEvent source) {
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
    public static Channel getChannel(@Nonnull MediaObject program) {
        for (ScheduleEvent se : program.getScheduleEvents()) {
            if (! ScheduleEvents.isRerun(se)) {
                return se.getChannel();
            }
        }
        return null;
    }


    /**
     * @since 2.2.3
     */
    public static String getRelationText(@Nonnull MediaObject object, String relationType) {
        Relation rel = getRelation(object, relationType);
        return rel == null ? null : rel.getText();
    }

    /**
     * @since 3.3.0
     */
    public static Relation getRelation(@Nonnull MediaObject object, String relationType) {
        for (Relation relation : object.getRelations()) {
            if (relation.getType().equals(relationType)) {
                return relation;
            }
        }
        return null;
    }

    public static TwitterRef getTwitterHash(@Nonnull  MediaObject object) {
        for (TwitterRef ref : object.getTwitterRefs()) {
            if (ref.getType() == TwitterRef.Type.HASHTAG) {
                return ref;
            }
        }
        return null;
    }

    public static TwitterRef getTwitterAccount(@Nonnull MediaObject object) {
        for (TwitterRef ref : object.getTwitterRefs()) {
            if (ref.getType() == TwitterRef.Type.ACCOUNT) {
                return ref;
            }
        }
        return null;
    }

    public static String getKijkwijzer(@Nonnull MediaObject media) {
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


    private static void matchBroadcasters(@Nonnull  BroadcasterService broadcasterService, @Nonnull  MediaObject mediaObject, @Nonnull Set<MediaObject> handled) throws NotFoundException {
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

    public static void removeLocations(@Nonnull MediaObject mediaObject) {
        while (mediaObject.getLocations().size() > 0) {
            mediaObject.removeLocation(mediaObject.getLocations().first());
        }
    }

    public static void addAll(@Nonnull MediaObject mediaObject, Iterable<Location> i) {
        for (Location l : i) {
            mediaObject.addLocation(l);
        }
    }



    public static Instant getSortInstant(@Nonnull MediaObject mo) {
        if (mo instanceof Group) {
            return mo.sortInstant;
        } else if (mo instanceof Segment) {
            Segment segment = (Segment) mo;
            if (segment.parent != null) {
                return getSortInstant(segment.parent);
            }
        }
        Instant date = null;
        if (mo.scheduleEvents != null && mo.scheduleEvents.size() > 0) {
            List<ScheduleEvent> list = new ArrayList<>(mo.scheduleEvents);
            list.sort(Collections.reverseOrder());
            date = list.stream()
                .filter(ScheduleEvents::isOriginal).findFirst()
                // TODO for groups we also filter before now. Shouldn't we do that for programs too?
                // .filter(se -> se.getStartInstant().isBefore(Instant.now))
                .map(ScheduleEvent::getStartInstant)
                .orElse(null);
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


    public static boolean trim(@Nonnull Collection<?> collection) {
        boolean trimmed = false;
        for (java.util.Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            Object next = iterator.next();
            if (next == null) {
                iterator.remove();
                trimmed = true;
            }
        }

        return trimmed;
    }

    public static <T extends UpdatableIdentifiable<?, T>> void integrate(@Nonnull List<T> existing, @Nonnull List<T> updates) {
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

    public static boolean markForRepublication(@Nonnull MediaObject media, String reason) {
        if ((Workflow.MERGED.equals(media.getWorkflow()) || Workflow.PUBLISHED.equals(media.getWorkflow())) && media.inPublicationWindow(Instant.now())) {
            media.setWorkflow(Workflow.FOR_REPUBLICATION);
            media.setRepubReason(reason);
            media.setRepubDestinations(null);
            return true;
        } else {
            return false;
        }
    }


    public static void markForDeletion(@Nonnull MediaObject media, String reason) {
        if (! Workflow.DELETES.contains(media.getWorkflow())) {
            media.setWorkflow(Workflow.FOR_DELETION);
            media.setRepubReason(reason);
            media.setRepubDestinations(null);
        }
    }

    public static void markForUnDeletion(@Nonnull MediaObject media, String reason) {
        if (Workflow.DELETES.contains(media.getWorkflow())) {
            media.setWorkflow(Workflow.FOR_REPUBLICATION);
            media.setRepubReason(reason);
            media.setRepubDestinations(null);
        }
    }

    public static void markPublished(@Nonnull MediaObject media, @Nonnull Instant now, String reason) {
        media.setLastPublishedInstant(now);
        media.setRepubReason(reason);
        media.setRepubDestinations(null);
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
            if (result.isEmpty()) {
                result = Arrays.stream(Platform.values()).map((p) -> p.name().toLowerCase()).collect(Collectors.toList());
                log.info("No available platforms for {}, taking {}", media, result);
            }
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
     * Whether this mediaobject is playable in a NPO player.
     * @since 5.11
     */
    public static boolean isPlayable(MediaObject media) {
        if (media == null) {
            return false;
        }
        // TODO, it seems that this is enough
        return media.getStreamingPlatformStatus().isAvailable();
        // but why not this?
        //return media.getStreamingPlatformStatus().isAvailable() && Optional.ofNullable(media.getPrediction(Platform.INTERNETVOD)).map(p -> p.inPublicationWindow(Instant.now())).orElse(false);
    }

    /**
     * Filters a PublishableObject. Removes all subobject which dont' have a correct workflow.
     *
     * TODO work in progress. This may replace the hibernate filter solution now in place.
     */
    public static <T extends PublishableObject> T filterPublishable(T object) {
        Predicate<Object> p = (o) -> {
            if (o instanceof PublishableObject) {
                return ((PublishableObject) o).isPublishable();
            } else {
                return true;
            }
        };
        ObjectFilter.Result<T> result = ObjectFilter.filter(object, p);
        log.debug("Filtered {} from {}", result.filterCount(), result.get());
        return result.get();
    }
    public static <T extends PublishableObject> T filterOnWorkflow(T object, Predicate<Workflow> predicate) {
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
     * @javadoc
     */

    public static Optional<List<MemberRef>> getPath(MediaObject parent, MediaObject child, List<? extends MediaObject> descendants) {
        return getPath(parent, child,
            descendants.stream().distinct().collect(Collectors.toMap(MediaObject::getMid, d -> d)
            )
        );
    }


    public static <T extends MediaObject>  void  updateLocationsForOwner(T incomingMedia, T mediaToUpdate, OwnerType owner, boolean steal) {
        for(Location incomingLocation : incomingMedia.getLocations()) {

            if(incomingLocation.getOwner().equals(owner)) {
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


    public static <T extends MediaObject>  List<Location>  updateAndRemoveLocationsForOwner(T incomingMedia, T mediaToUpdate, OwnerType owner) {
        updateLocationsForOwner(incomingMedia, mediaToUpdate, owner, false);
        List<Location> locationsToRemove = new ArrayList<>();
        mediaToUpdate.getLocations().removeIf(
            location ->
                location.getOwner().equals(owner) &&
                incomingMedia.findLocation(location.getProgramUrl(), owner) == null
        );
        return locationsToRemove;
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
        return o.persons;

    }


    public static <T extends PublishableObject> boolean revokeRelatedPublishables(MediaObject media, Collection<T> publishables, Instant now, Runnable callbackOnChange) {
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
        result &= MediaObjects.revokeRelatedPublishables(media, media.getLocations(), now, () -> Locations.updatePredictionStates(media));
        return result;

    }



    // DEPRECATED methods


    /**
     * @since 2.1
     */
    @Deprecated
    public static Date getSortDate(MediaObject mo) {
        return DateUtils.toDate(getSortInstant(mo));

    }

    /**
     * @deprecated Use {@link TextualObjects#filter}
     */
    @Deprecated
    public static <T extends MutableOwnable> List<T> filter(Collection<T> ownables, OwnerType owner) {
        return TextualObjects.filter(ownables, owner);
    }


    /**
     * @deprecated Use {@link TextualObjects#get}
     */
    @Deprecated
    public static String getTitle(MediaObject media, OwnerType owner, TextualType type) {
        return TextualObjects.getTitle(media, owner, type);
    }

    /**
     * @deprecated Use {@link TextualObjects#get}
     */
    @Deprecated
    public static String getTitle(Collection<Title> titles, TextualType... types) {
        return TextualObjects.get(titles, types);
    }

    /**
     * @deprecated Use {@link TextualObjects#get}
     */
    @Deprecated
    public static String getTitle(Collection<Title> titles, String defaultValue, TextualType... types) {
        return TextualObjects.get(titles, defaultValue, types);
    }

    /**
     * @deprecated Use {@link TextualObjects#getObject}
     */
    @Deprecated
    public static Title getTitleObject(Collection<Title> titles, TextualType... types) {
        return TextualObjects.getObject(titles, types);
    }

    /**
     * @deprecated Use {@link TextualObjects#getDescription(TextualObject, OwnerType, TextualType)}
     */
    @Deprecated
    public static String getDescription(MediaObject media, OwnerType owner, TextualType type) {
        return TextualObjects.getDescription(media, owner, type);
    }

    /**
     * @deprecated Use {@link TextualObjects#getDescription(TextualObject, TextualType...)}
     */
    @Deprecated
    public static String getDescription(MediaObject media, TextualType... types) {
        return TextualObjects.getDescription(media, types);
    }

    /**
     * @deprecated Use {@link TextualObjects#get(Collection, TextualType...)}
     */
    @Deprecated
    public static String getDescription(Collection<Description> descriptions, TextualType... types) {
        return TextualObjects.get(descriptions, types);
    }

    /**
     * @deprecated Use {@link TextualObjects#get(Collection, String, TextualType...)}
     */
    @Deprecated
    public static String getDescription(Collection<Description> descriptions, String defaultValue, TextualType... types) {
        return TextualObjects.get(descriptions, defaultValue, types);
    }

    /**
     * @deprecated Use {@link TextualObjects#findOwnersForTextFields(TextualObject)}
     */
    @Deprecated
    public static OwnerType[] findOwnersForTextFields(MediaObject media) {
        return TextualObjects.findOwnersForTextFields(media);
    }



}
