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

import com.google.common.collect.Iterables;

import nl.vpro.domain.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.util.DateUtils;
import nl.vpro.util.ObjectFilter;

import static nl.vpro.domain.media.support.Workflow.PUBLICATIONS;
import static nl.vpro.domain.media.support.Workflow.PUBLISHED;


/**
 * Various methods related to dealing with {@link MediaObject}s, like copying and filling.
 *
 * See {@link TextualObjects}, and {@link Embargos} for methods like this (because media objects are {@link TextualObject} and {@link Embargo}
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


    public static void copy(MediaObject from, MediaObject to) {
        Embargos.copy(from, to);
        TextualObjects.copy(from, to);
        to.setCountries(from.getCountries());
        to.setLanguages(from.getLanguages());
        to.setDuration(from.getDuration());
        to.setAgeRating(from.getAgeRating());
        to.setContentRatings(from.getContentRatings());
        to.setWebsites(from.getWebsites());
        to.setEmail(from.getEmail());
        to.setGenres(from.getGenres());
        // TODO: more fields

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
    public static Channel getChannel(MediaObject program) {
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
    public static String getRelationText(MediaObject object, String relationType) {
        Relation rel = getRelation(object, relationType);
        return rel == null ? null : rel.getText();
    }

    /**
     * @since 3.3.0
     */
    public static Relation getRelation(MediaObject object, String relationType) {
        for (Relation relation : object.getRelations()) {
            if (relation.getType().equals(relationType)) {
                return relation;
            }
        }
        return null;
    }

    public static TwitterRef getTwitterHash(MediaObject object) {
        for (TwitterRef ref : object.getTwitterRefs()) {
            if (ref.getType() == TwitterRef.Type.HASHTAG) {
                return ref;
            }
        }
        return null;
    }

    public static TwitterRef getTwitterAccount(MediaObject object) {
        for (TwitterRef ref : object.getTwitterRefs()) {
            if (ref.getType() == TwitterRef.Type.ACCOUNT) {
                return ref;
            }
        }
        return null;
    }

    public static String getKijkwijzer(MediaObject media) {
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


    private static void matchBroadcasters(BroadcasterService broadcasterService, MediaObject mediaObject, Set<MediaObject> handled) throws NotFoundException {
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
                matchBroadcasters(broadcasterService, memberRef.getOwner(), handled);
            }
            if (mediaObject instanceof Program) {
                Program p = (Program) mediaObject;
                for (MemberRef memberRef : p.getEpisodeOf()) {
                    matchBroadcasters(broadcasterService, memberRef.getOwner(), handled);
                }
            }
        }
    }

    public static void removeLocations(MediaObject mediaObject) {
        while (mediaObject.getLocations().size() > 0) {
            mediaObject.removeLocation(mediaObject.getLocations().first());
        }
    }

    public static void addAll(MediaObject mediaObject, Iterable<Location> i) {
        for (Location l : i) {
            mediaObject.addLocation(l);
        }
    }



    public static Instant getSortInstant(MediaObject mo) {
        if (mo instanceof Group) {
            return mo.sortDate;
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
            date = mo.getPublishStartInstant();
        }
        if (date == null) {
            date = mo.getCreationInstant();
        }
        return date;

    }


        public static boolean trim(Collection<?> collection) {
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

    public static <T extends UpdatableIdentifiable<?, T>> void integrate(List<T> existing, List<T> updates) {
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

    public static void markForRepublication(MediaObject media, String reason) {
        if ((Workflow.MERGED.equals(media.getWorkflow()) || Workflow.PUBLISHED.equals(media.getWorkflow())) && media.inPublicationWindow(Instant.now())) {
            media.setWorkflow(Workflow.FOR_REPUBLICATION);
            media.setRepubReason(reason);
            media.setRepubDestinations(null);

        }
    }

    public static void markPublished(MediaObject media, Instant now, String reason) {
        media.setLastPublishedInstant(now);
        media.setRepubReason(reason);
        media.setRepubDestinations(null);
    }

    public static boolean realizeAndExpirePredictions(MediaObject object) {
        boolean change = false;
        for (Prediction prediction : object.getPredictions()) {
            change |= realizeAndExpirePredictions(prediction.getPlatform(), object);
        }
        return change;
    }

    public static boolean realizeAndExpirePredictions(Platform platform, MediaObject mediaObject) {
        if (platform == null) {
            return false;
        }
        boolean changes = false;
        Prediction prediction = getPrediction(platform, mediaObject.getPredictions());
        if (prediction != null) {
            Prediction.State requiredState = Prediction.State.ANNOUNCED;

            for (Location location : mediaObject.getLocations()) {
                Platform locationPlatform = location.getPlatform();
                if (locationPlatform == null) {
                    log.debug("Location has no explicit platform");
                    // this might be a good idea?
                    //log.debug("Location has no explicit platform. Taking it {} implicitely", Platform.INTERNETVOD);
                    //locationPlatform = Platform.INTERNETVOD;
                }
                if (locationPlatform == platform) {
                    if (location.isPublishable()) {
                        requiredState = Prediction.State.REALIZED;
                        break;
                    }
                    if (location.wasUnderEmbargo()) {
                        requiredState = Prediction.State.REVOKED;
                    }
                }
            }
            if (prediction.getState() != requiredState) {
                log.info("Set state of {} {} {} -> {}", mediaObject.getMid(), prediction, prediction.getState(), requiredState);
                prediction.setState(requiredState);
                changes = true;
            }
        }
        return changes;
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

    public static List<String> getAvailablePlatformNamesInLowerCase(Collection<Prediction> preds) {
        if (preds != null) {
            return preds.stream()
                .filter(Prediction::isAvailable)
                .map(Prediction::getPlatform)
                .map(Platform::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        }
        return null;
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

    public static Prediction updatePrediction(MediaObject media, Platform platform, ReadonlyEmbargo embargo) {
        Prediction prediction = media.findOrCreatePrediction(platform);
        Embargos.copy(embargo, prediction);
        return prediction;
    }



    public static boolean subtitlesMayBePublished(MediaObject media) {
        return media != null && PUBLICATIONS.contains(media.getWorkflow()) && media.getLocations().stream().anyMatch(l -> l.getWorkflow() == PUBLISHED);
    }

    /**
     * Filters a PublishableObject. Removes all subobject which dont' have a correct workflow.
     *
     * TODO work in progress. This may replace the hibernate filter solution now in place (but probably broken right now MSE-3526 ?)
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


    public static Optional<List<MemberRef>> getPath(MediaObject parent, MediaObject child, List<? extends MediaObject> descendants) {
        return getPath(parent, child,
            descendants.stream().distinct().collect(Collectors.toMap(MediaObject::getMid, d -> d)
            )
        );
    }


    public static <T extends MediaObject>  void  updateLocationsForOwner(T incomingMedia, T mediaToUpdate, OwnerType owner) {
        for(Location incomingLocation : incomingMedia.getLocations()) {

            if(incomingLocation.getOwner().equals(owner)) {
                Location locationToUpdate = mediaToUpdate.findLocation(incomingLocation.getProgramUrl());
                if(locationToUpdate == null) {
                    mediaToUpdate.addLocation(incomingLocation);
                } else {
                    if (locationToUpdate.getOwner() != owner) {
                        log.warn("Cannot update location {} since it not from {}", locationToUpdate, owner);
                    } else {
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
        updateLocationsForOwner(incomingMedia, mediaToUpdate, owner);
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
    public static <T extends Ownable> List<T> filter(Collection<T> ownables, OwnerType owner) {
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
