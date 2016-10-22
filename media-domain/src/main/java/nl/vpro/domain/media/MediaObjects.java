/**
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

import nl.vpro.domain.NotFoundException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

import static nl.vpro.domain.media.MediaObject.sorted;


/**
 * @since 1.5
 */
public class MediaObjects {

    private final static Logger LOG = LoggerFactory.getLogger(MediaObjects.class);

    public static boolean equalsOnAnyId(MediaObject first, MediaObject second) {
        return first == second ||
            first.getId() != null && first.getId().equals(second.getId()) ||
            first.getUrn() != null && first.getUrn().equals(second.getUrn()) ||
            first.getMid() != null && first.getMid().equals(second.getMid()) ||
            equalsOnCrid(first, second);
    }

    public static boolean equalsOnCrid(MediaObject first, MediaObject second) {
        if(first.getCrids().isEmpty() || second.getCrids().isEmpty()) {
            return false;
        }

        for(String firstCrid : first.getCrids()) {
            for(String secondCrid : second.getCrids()) {
                if(secondCrid.equals(firstCrid)) {
                    return true;
                }
            }
        }

        return false;

    }

    public static Collection<String> filterCrids(Collection<String> crids, final String contains) {
        return Collections2.filter(crids, new Predicate<String>() {
            @Override
            public boolean apply(String crid) {
                return crid != null && crid.contains(contains);
            }
        });
    }

    /**
     * Sets the owner of all titles, descriptions, locations and images found in given MediaObject
     */
    public static void forOwner(MediaObject media, OwnerType owner) {
        for(Title title : media.getTitles()) {
            title.setOwner(owner);
        }
        for(Description description : media.getDescriptions()) {
            description.setOwner(owner);
        }
        for(Location location : media.getLocations()) {
            location.setOwner(owner);
        }
        for(Image image : media.getImages()) {
            image.setOwner(owner);
        }
    }

    public static <T extends Ownable> List<T> filter(Collection<T> ownables, OwnerType owner) {
        return ownables.stream().filter(item -> item.getOwner() == owner).collect(Collectors.toList());
    }

    public static String getTitle(MediaObject media, OwnerType owner, TextualType type) {
        for(Title title : media.getTitles()) {
            if(title.getOwner() == owner && title.getType() == type) {
                return title.getTitle();
            }
        }
        return "";
    }

    public static String getTitle(Collection<Title> titles, TextualType... types) {
        return getTitle(titles, "", types);
    }

    public static String getTitle(Collection<Title> titles, String defaultValue, TextualType... types) {
        Title title = getTitleObject(titles, types);
        return title == null ? defaultValue : title.getTitle();
    }

    public static Title getTitleObject(Collection<Title> titles, TextualType... types) {
        if(titles != null) {
            for(Title title : titles) {
                for(TextualType type : types) {
                    if(type == title.getType()) {
                        return title;
                    }
                }
            }
        }
        return null;
    }

    public static Collection<Title> getTitles(Collection<Title> titles, TextualType... types) {
        List<Title> returnValue = new ArrayList<Title>();
        if(titles != null) {
            for(Title title : titles) {
                for(TextualType type : types) {
                    if(type == title.getType()) {
                        returnValue.add(title);
                    }
                }
            }
        }
        return returnValue;
    }

    public static String getDescription(MediaObject media, OwnerType owner, TextualType type) {
        for(Description description : media.getDescriptions()) {
            if(description.getOwner() == owner && description.getType() == type) {
                return description.getDescription();
            }
        }
        return "";
    }

    public static String getDescription(MediaObject media, TextualType... types) {
        return getDescription(media.getDescriptions(), "", types);
    }

    public static String getDescription(Collection<Description> descriptions, TextualType... types) {
        return getDescription(descriptions, "", types);
    }

    public static String getDescription(Collection<Description> descriptions, String defaultValue, TextualType... types) {
        if(descriptions != null) {
            for(Description description : descriptions) {
                for(TextualType type : types) {
                    if(type == description.getType()) {
                        return description.getDescription();
                    }
                }
            }
        }
        return defaultValue;
    }

    public static OwnerType[] findOwnersForTextFields(MediaObject media) {
        SortedSet<OwnerType> result = new TreeSet<>();
        for(Title title : media.getTitles()) {
            result.add(title.getOwner());
        }
        for(Description description : media.getDescriptions()) {
            result.add(description.getOwner());
        }
        return result.toArray(new OwnerType[result.size()]);
    }

    public static <T extends MediaObject> T deepCopy(T media)  {
        ObjectOutputStream objectOut = null;
        ObjectInputStream objectIn = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            objectOut = new ObjectOutputStream(byteOut);
            objectOut.writeObject(media);
            objectOut.flush();

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            objectIn = new ObjectInputStream(byteIn);
            return (T)objectIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if(objectOut != null) {
                try {
                    objectOut.close();
                } catch(IOException e) {
                    LOG.error("Error closing object output stream after deep copy: {}", e.getMessage());
                }
            }

            if(objectIn != null) {
                try {
                    objectIn.close();
                } catch(IOException e) {
                    LOG.error("Error closing object input stream after deep copy: {}", e.getMessage());
                }
            }
        }
    }


    public static void matchBroadcasters(BroadcasterService broadcasterService, MediaObject mediaObject) throws NotFoundException {
        matchBroadcasters(broadcasterService, mediaObject, null);
    }

    public static boolean hasChannel(MediaObject media, Channel... channels) {
        return hasChannel(media, Arrays.asList(channels));
    }

    public static boolean hasChannel(MediaObject media, Collection<Channel> channels) {
        for(ScheduleEvent scheduleEvent : media.getScheduleEvents()) {
            if(channels.contains(scheduleEvent.getChannel())) {
                return true;
            }
        }
        return false;
    }

    public static ScheduleEvent findScheduleEventHonoringOffset(MediaObject media, ScheduleEvent source) {
        for(ScheduleEvent existing : media.getScheduleEvents()) {
            if(ScheduleEvents.equalHonoringOffset(existing, source)) {
                return existing;
            }
        }
        return null;
    }

    public static ScheduleEvent findScheduleEvent(Channel channel, Date start, Collection<ScheduleEvent> events) {
        for(ScheduleEvent event : events) {
            if(event.getStartInstant().toEpochMilli() == start.getTime() && event.getChannel().equals(channel)) {
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
        for(ScheduleEvent event : events) {
            if(channelList.contains(event.getChannel())) {
                result.add(event);
            }
        }
        return result;
    }

    public static Channel getChannel(MediaObject program) {
        for(ScheduleEvent se : program.getScheduleEvents()) {
            Repeat repeat = se.getRepeat();
            if(repeat == null || (!repeat.isRerun())) {
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
        for(Relation relation : object.getRelations()) {
            if(relation.getType().equals(relationType)) {
                return relation;
            }
        }
        return null;
    }

    public static TwitterRef getTwitterHash(MediaObject object) {
        for(TwitterRef ref : object.getTwitterRefs()) {
            if(ref.getType() == TwitterRef.Type.HASHTAG) {
                return ref;
            }
        }
        return null;
    }

    public static TwitterRef getTwitterAccount(MediaObject object) {
        for(TwitterRef ref : object.getTwitterRefs()) {
            if(ref.getType() == TwitterRef.Type.ACCOUNT) {
                return ref;
            }
        }
        return null;
    }

    public static String getKijkwijzer(MediaObject media) {
        StringBuilder sb = new StringBuilder();
        if(media.getAgeRating() != null) {
            switch(media.getAgeRating()) {
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

        for(ContentRating contentRating : media.getContentRatings()) {
            if(contentRating != null) {
                sb.append(contentRating.toChar());
            }
        }

        return sb.length() > 0 ? sb.toString() : null;
    }


    protected static void matchBroadcasters(BroadcasterService broadcasterService, MediaObject mediaObject, Set<MediaObject> handled) throws NotFoundException {
        if(handled == null) {
            handled = new HashSet<>(); // to avoid accidental stack overflows
        }
        if(!handled.contains(mediaObject)) {
            handled.add(mediaObject);
            List<Broadcaster> copy = new ArrayList<>(mediaObject.getBroadcasters());


            mediaObject.getBroadcasters().clear();

            for(Broadcaster b : copy) {
                if(b.getId() != null) {
                    mediaObject.addBroadcaster(broadcasterService.find(b.getId()));
                } else if(b.getWhatsOnId() != null) {
                    mediaObject.addBroadcaster(broadcasterService.findForWhatsOnId(b.getWhatsOnId()));
                } else if(b.getNeboId() != null) {
                    mediaObject.addBroadcaster(broadcasterService.findForNeboId(b.getNeboId()));
                } else {
                    mediaObject.addBroadcaster(b);
                }
            }
            for(MemberRef memberRef : mediaObject.getMemberOf()) {
                matchBroadcasters(broadcasterService, memberRef.getOwner(), handled);
            }
            if(mediaObject instanceof Program) {
                Program p = (Program)mediaObject;
                for(MemberRef memberRef : p.getEpisodeOf()) {
                    matchBroadcasters(broadcasterService, memberRef.getOwner(), handled);
                }
            }
        }
    }

    public static void removeLocations(MediaObject mediaObject) {
        while(mediaObject.getLocations().size() > 0) {
            mediaObject.removeLocation(mediaObject.getLocations().first());
        }
    }

    public static void addAll(MediaObject mediaObject, Iterable<Location> i) {
        for(Location l : i) {
            mediaObject.addLocation(l);
        }
    }


    /**
     * @since 2.1
     */
    public static Date getSortDate(MediaObject mo) {
        if(mo instanceof Group) {
            return mo.sortDate;
        } else if(mo instanceof Segment) {
            Segment segment = (Segment)mo;
            if(segment.parent != null) {
                return getSortDate(segment.parent);
            }
        }
        Date date = null;
        if(mo.scheduleEvents != null && mo.scheduleEvents.size() > 0) {
            date = sorted(mo.scheduleEvents).first().getStart();
        }
        if(date == null) {
            date = mo.getPublishStart();
        }
        if(date == null) {
            date = mo.getCreationDate();
        }
        return date;

    }

    public static boolean trim(Collection<?> collection) {
        boolean trimmed = false;
        for(java.util.Iterator iterator = collection.iterator(); iterator.hasNext(); ) {
            Object next = iterator.next();
            if(next == null) {
                iterator.remove();
                trimmed = true;
            }
        }

        return trimmed;
    }

    public static <T extends UpdatableIdentifiable<?, T>> void integrate(List<T> existing, List<T> updates) {
        T move = null;
        for(int i = 0; i < updates.size(); i++) {
            T incoming = updates.get(i);
            if(move != null || i < existing.size()) {
                T target = move != null ? move : existing.get(i);
                move = null;

                if(incoming.getId() == null) {
                    existing.set(i, incoming);
                    move = target;
                } else if(incoming.getId().equals(target.getId())) {
                    target.update(incoming);
                } else {
                    existing.set(i, incoming);
                }
            } else {
                existing.add(incoming);
            }

        }

        for(int i = updates.size(); i < existing.size(); i++) {
            existing.remove(i);
        }
    }

    public static void markForRepublication(MediaObject media) {
        if((Workflow.MERGED.equals(media.getWorkflow()) || Workflow.PUBLISHED.equals(media.getWorkflow())) && media.isPublishable()) {
            media.setWorkflow(Workflow.FOR_REPUBLICATION);
        }
    }
}
