/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.web.media;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newrelic.api.agent.Trace;

import nl.vpro.domain.Identifiable;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.search.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.domain.user.MediaEditorService;
import nl.vpro.domain.user.Portal;
import nl.vpro.spring.security.acl.MediaPermission;
import nl.vpro.spring.security.acl.MediaPermissionEvaluator;
import nl.vpro.transfer.extjs.ExtRecord;
import nl.vpro.transfer.extjs.SuggestList;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.errors.ErrorList;
import nl.vpro.transfer.extjs.media.*;
import nl.vpro.transfer.extjs.success.SuccessList;
import nl.vpro.util.Helper;

import static nl.vpro.spring.security.acl.MediaPermission.*;

@Service("extMediaService")
@Transactional
public class ExtMediaServiceImpl implements ExtMediaService {

    private final static Logger LOG = LoggerFactory.getLogger(ExtMediaServiceImpl.class);

    @Autowired
    private MediaService mediaService;

    @Autowired
    private MediaEditorService editorService;

    @Autowired
    private BroadcasterService broadcasterService;

    @Autowired
    private TagService tagService;

    @Autowired
    private MediaPermissionEvaluator permissionEvaluator;

    @Named("ext.useIndexForSearch")
    private boolean useIndexForSearch = true;

    @Named("ext.useIndexForMembers")
    private boolean useIndexForMembers = true;

    @Trace
    @Override
    public TransferList<?> suggestTitles(MediaForm form) {
        return SuggestList.create(mediaService.findTitles(form));
    }

    @Trace
    @Override
    public TransferList<?> suggestTags(String input, int max) {
        return SuggestList.create(mediaService.findTags(input, max));
    }

    @Trace
    @Override
    public TransferList<?> search(MediaForm form, boolean onlyWriteable) {
        if (useIndexForSearch) {
            MediaSearchItemResult result = mediaService.findMediaListItems(form, onlyWriteable);
            return MediaResultList.create(permissionEvaluator, result.getResult(), result.getCount());
        } else {
            // backwards compatible for the time (days?) that the index is not ready.
            MediaSearchResult result = mediaService.findMedia(form, onlyWriteable);
            return MediaResultList.create(permissionEvaluator, result.getResult().stream().map(MediaListItem::new).collect(Collectors.toList()), result.getCount());

        }
    }

    @Trace
    @Override
    public TransferList<?> createMedia(MultiValueMap<String, String> map) throws ModificationException {
        MediaObject media;

        try {
            MediaType type = MediaType.valueOf(map.getFirst("type"));
            media = type.getMediaInstance();
        } catch(Exception e) {
            return new ErrorList("Invalid media type. " + e.getMessage());
        }

        try {
            media = copyInput(media, map);
        } catch(IllegalArgumentException e) {
            return new ErrorList(e.getMessage());
        } catch(ParseException e) {
            return new ErrorList("Error parsing date string.");
        }

        // Persist before counting
        media = mediaService.merge(media);

        long episodeCount = 0;
        if(media instanceof Group) {
            episodeCount = mediaService.countEpisodes((Group)media);
        }
        long memberCount = mediaService.countMembers(media);

        return MediaEditList.create(permissionEvaluator, media, episodeCount, memberCount,
            hasWritePermission(media), broadcasterService.findAll(),
            editorService.allowedPortals(), hasGenrePermission(media));
    }

    private MediaObject get(Long mediaId) {
        return mediaService.get(mediaId);
    }

    private boolean denyDeleted(MediaObject media) {
        return media.getWorkflow() == Workflow.DELETED && !editorService.currentUserHasRole("ROLE_MEDIA_SUPERADMIN", "ROLE_MEDIA_SUPPORT");
    }

    @Trace
    @Override
    public TransferList<?> getMedia(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null || denyDeleted(media)) {
            return notFound(mediaId);
        }

        checkPermission(media, READ);

        // these turn off the delete and publish filters which leads to faulty counts
        // temporary workaround is to turn on the filters after the method calls
        long episodeCount = 0;
        if(media instanceof Group) {
            episodeCount = mediaService.countEpisodes((Group)media);
        }
        long memberCount = mediaService.countMembers(media);

        return MediaEditList.create(
            permissionEvaluator,
            media,
            episodeCount,
            memberCount,
            hasWritePermission(media) && !denyDeleted(media),
            broadcasterService.findAll(),
            editorService.allowedPortals(),
            hasGenrePermission(media));
    }

    @Trace
    @Override
    public TransferList<?> updateMedia(Long mediaId, MultiValueMap<String, String> map) throws ModificationException {
        final Long id = Long.valueOf(map.getFirst("id"));
        if(id == null || id < 0) {
            return new ErrorList("Invalid id.");
        }

        MediaObject media = mediaService.get(id);
        if(media == null) {
            return notFound(id);
        }

        checkPermission(media, MediaPermission.WRITE);

        try {
            media = copyInput(media, map);
        } catch(ParseException e) {
            // Force transaction rollback by throwing a runtime exception
            throw new RuntimeException(e);
        }

        long episodeCount = 0;
        if(media instanceof Group) {
            episodeCount = mediaService.countEpisodes((Group)media);
        }
        long memberCount = mediaService.countMembers(media);

        return MediaEditList.create(
            permissionEvaluator,
            mediaService.merge(media),
            episodeCount,
            memberCount,
            hasWritePermission(media) && media.getWorkflow() != Workflow.DELETED,
            broadcasterService.findAll(),
            editorService.allowedPortals(),
            hasGenrePermission(media));
    }

    @Trace
    @Override
    public TransferList<?> deleteMedia(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, WRITE);

        if(media instanceof Program) {
            Program program = (Program)media;
            if(program.getType().equals(ProgramType.BROADCAST) || program.getType().equals(ProgramType.STRAND)) {
                return new ErrorList("Users cannot delete Programs of type BROADCAST or STRAND");
            }
        }

        mediaService.delete(media);

        return new SuccessList("Deleted media with id: " + mediaId);
    }

    @Trace
    @Override
    public TransferList<?> getSegments(Long mediaId) {
        Program program = (Program)get(mediaId);
        if(program == null) {
            return notFound(mediaId);
        }

        checkPermission(program, READ);

        return SegmentList.create(program);
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateSegments(Long mediaId, MultiValueMap<String, String> map) throws ModificationException {
        Program program = (Program)get(mediaId);
        if(program == null) {
            return notFound(mediaId);
        }

        checkPermission(program, WRITE);

        final SegmentView[] segmentViews;
        try {
            segmentViews = unmarshal(map, SegmentView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        SegmentList confirmation = new SegmentList(true, "Added segment.");

        for(SegmentView view : segmentViews) {
            Segment segment;
            if(view.getId() != null) {
                segment = mediaService.get(view.getId());
                updateTo(view, segment);
                mediaService.merge(segment);
            } else {
                segment = new Segment();
                segment.setAVType(program.getAVType());

                updateTo(view, segment);
                program.addSegment(segment);
                program = mediaService.merge(program);
                segment = latestEntry(program.getSegments());
            }

            if(view.isDeleted()) {
                boolean success = program.deleteSegment(segment);
                if(success) {
                    confirmation.add(view);
                }
                continue;
            }

            SegmentView result = SegmentView.create(segment);
            confirmation.add(result);
        }

        return confirmation;
    }

    private void updateTo(SegmentView segmentView, Segment segment) throws ModificationException {
        segment.setStart(segmentView.getStartDateValue());
        segment.setDurationWithDate(segmentView.getDurationDateValue());

        updateTitle(segmentView, segment);
        updateDescription(segmentView, segment);
        updateImages(segmentView, segment);
    }

    private void updateTitle(SegmentView segmentView, Segment segment) {
        Title existingTitle = segment.findTitle(OwnerType.BROADCASTER, TextualType.MAIN);
        if(existingTitle != null) {
            existingTitle.setTitle(segmentView.getTitle());
        } else {
            segment.addTitle(segmentView.getTitle(), OwnerType.BROADCASTER,
                TextualType.MAIN);
        }
    }

    private void updateDescription(SegmentView segmentView, Segment segment) {
        if(StringUtils.isNotBlank(segmentView.getDescription())) {
            Description existingDescription = segment.findDescription(OwnerType.BROADCASTER, TextualType.MAIN);
            if(existingDescription != null) {
                existingDescription.setDescription(segmentView.getDescription());
            } else {
                segment.addDescription(segmentView.getDescription(), OwnerType.BROADCASTER,
                    TextualType.MAIN);
            }
        } else {
            segment.removeDescription(OwnerType.BROADCASTER, TextualType.MAIN);
        }
    }

    private void updateImages(SegmentView segmentView, Segment segment) {
        List<Image> existingImages = segment.getImages();
        for(ImageView imageView : segmentView.getImages()) {
            Image image = imageView.toImage();
            if(imageView.isDeleted()) {
                segment.removeImage(image);
            } else if(existingImages.contains(image)) {
                imageView.updateTo(segment.getImage(image));
            } else {
                segment.addImage(image);
            }
        }
    }

    @Trace
    @Override
    public TransferList<?> getLocations(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        LocationList result = LocationList.create(media);
        result.setWritable(hasPermission(media, LOCATION_WRITE));
        return result;
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateLocations(Long mediaId, MultiValueMap<String, String> map) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, LOCATION_WRITE);

        LocationView[] locationViews;
        try {
            locationViews = unmarshal(map, LocationView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        // check location consistency
        try {
            checkIncomingLocationConsistency(media, locationViews);
        } catch (IllegalArgumentException iae) {
            return new ErrorList(iae);
        }

        LocationList confirmation = new LocationList(true, "Updated locations");

        for(LocationView view : locationViews) {
            Location incoming = view.toLocation();
            Location existing = media.getLocation(incoming);

            if(existing != null) {
                if(view.isDeleted()) {
                    if(!hasPermission(existing, DELETE)) {
                        throw new IllegalArgumentException("You may not delete ceres locations!");
                    }
                    mediaService.removeLocation(media, existing);
                    confirmation.add(view);
                    continue;
                }

                view.updateTo(existing);
            } else {
                existing = mediaService.addLocation(media, incoming);
                media = mediaService.merge(media);
            }
            if(existing == null) {
                throw new IllegalStateException("Could not found just created location " + view.getUrl());
            }
            LocationView result = LocationView.create(existing);
            confirmation.add(result);
        }

        mediaService.merge(media);

        return confirmation;
    }

    private void checkIncomingLocationConsistency(MediaObject media, LocationView[] views) {

        final List<Location> existingLocations = new ArrayList<>(media.getLocations());
        final List<Location> allLocations = new ArrayList<>();
        // add new/modified locations
        for(LocationView lv : views) {
            // get our incoming location
            Location incoming = lv.toLocation();
            // if this location is updated because it already exists, enrich the object with existing data
            if(existingLocations.contains(incoming)) {
                Location existing = existingLocations.get(existingLocations.indexOf(incoming));
                if(existing.hasPlatform()) {
                    incoming.setPlatform(existing.getPlatform());
                }
            }
            allLocations.add(incoming);
        }
        // add other existing locations, except for the ones modified
        for(Location l : existingLocations) {
            if(!allLocations.contains(l)) {
                allLocations.add(l);
            }
        }
        // perform check
        Location prev = null;
        for(Location l : allLocations) {
            if(l.isCeresLocation()) { // an object's ceres locations must all have equal start/stop times
                if(prev != null) {
                    Date prevStart = prev.getPublishStart();
                    Date prevStop = prev.getPublishStop();
                    if((prevStart == null && l.getPublishStart() != null) ||
                        (prevStop == null && l.getPublishStop() != null) ||
                        (prevStart != null && !prevStart.equals(l.getPublishStart())) ||
                        (prevStop != null && !prevStop.equals(l.getPublishStop()))) {
                        throw new IllegalArgumentException(String.format("All ceres Locations must have equal publication start and stop dates %s != %s", prev, l));
                    }
                }
                prev = l;
            }
        }
    }

    @Trace
    @Override
    public TransferList<?> getRelations(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, READ);

        return RelationList.create(media);
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateRelations(Long mediaId, MultiValueMap<String, String> map) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, WRITE);

        RelationView[] relationViews;
        try {
            relationViews = unmarshal(map, RelationView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        RelationList confirmation = new RelationList(true, "Updated relations");

        for(RelationView view : relationViews) {
            Relation incoming = view.toRelation();
            Relation result = null;

            if(incoming.getId() != null) {
                if(view.isDeleted()) {
                    media.removeRelation(incoming.getId());
                    confirmation.add(view);
                    continue;
                }

                Relation existing = media.findRelation(incoming.getId());
                view.updateTo(existing);
                result = existing;
            } else {
                media.addRelation(incoming);
                media = mediaService.mergeWithoutPublishing(media);

                for(Relation entry : media.getRelations()) {
                    // Find relation with highest id
                    if(result == null || result.getId() < entry.getId()) {
                        result = entry;
                    }
                }
            }

            RelationView resultView = RelationView.create(result);
            confirmation.add(resultView);
        }

        mediaService.merge(media);
        return confirmation;
    }

    @Trace
    @Override
    public TransferList<?> getEpisodes(Long mediaId) {
        Group group = (Group)get(mediaId);
        if(group == null) {
            return notFound(mediaId);
        }
        checkPermission(group, READ);
        if (useIndexForMembers) {
            return MembersList.create(permissionEvaluator, mediaService.getEpisodeItems(group));
        } else {
            return MembersList.create(permissionEvaluator, (mediaService.getEpisodes(group).stream().map(MemberRefItem::create).collect(Collectors.toList())));
        }
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateEpisodes(Long mediaId, MultiValueMap<String, String> map) throws CircularReferenceException {
        Group owner = (Group)get(mediaId);
        if(owner == null) {
            return notFound(mediaId);
        }

        checkPermission(owner, EPISODE_WRITE);

        MembersView[] membersViews;
        try {
            membersViews = unmarshal(map, MembersView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        MembersList confirmation = new MembersList(true, "Updated episodes");

        for(MembersView view : membersViews) {
            Program episode = mediaService.get(view.getMemberId());
            if(episode == null) {
                continue;
            }

            if(view.getId() != null) {
                if(view.isDeleted()) {
                    MemberRef ref = mediaService.getMemberRef(view.getId());
                    boolean success = mediaService.removeEpisodeOf(episode, ref);
                    if(success) {
                        confirmation.add(view);
                    }
                    continue;
                }

                MemberRef updatedRef = new MemberRef(view.getId(), episode,
                    owner, view.getNumber());

                updatedRef = mediaService.updateEpisodeNumber(episode,
                    updatedRef);
                confirmation.add(MembersView.create(permissionEvaluator, MemberRefItem.create(updatedRef)));
            } else {
                checkPermission(episode, EPISODE_OF_WRITE);

                MemberRef ref = mediaService.addEpisode(owner, episode,
                    view.getNumber());
                MembersView result = MembersView.create(permissionEvaluator, MemberRefItem.create(ref));
                confirmation.add(result);
            }
        }

        return confirmation;
    }

    @Trace
    @Override
    public TransferList<?> getEpisodeOf(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null || !(media instanceof Program)) {
            return notFound(mediaId);
        }

        Program episode = (Program)media;

        checkPermission(episode, READ);

        return MemberRefList.createEpisodeOf(permissionEvaluator, episode);
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateEpisodeOfs(Long mediaId, MultiValueMap<String, String> map) throws CircularReferenceException {
        MediaObject media = get(mediaId);
        if(media == null || !(media instanceof Program)) {
            return notFound(mediaId);
        }
        Program episode = (Program)media;

        checkPermission(episode, EPISODE_OF_WRITE);

        MemberRefView[] memberRefViews;
        try {
            memberRefViews = unmarshal(map, MemberRefView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        MemberRefList confirmation = new MemberRefList(true,
            "Updated episode off");

        for(MemberRefView view : memberRefViews) {
            if(view.isDeleted()) {
                MemberRef ref = mediaService.getMemberRef(view.getId());

                boolean success = mediaService.removeEpisodeOf(episode, ref);
                if(success) {
                    confirmation.add(view);
                }
                continue;
            }

            Group owner = mediaService.get(view.getReferenceId());
            if(owner == null) {
                continue;
            }

            MemberRef ref = mediaService.addEpisodeOf(episode, owner);
            confirmation.add(MemberRefView.create(permissionEvaluator, ref));
        }
        return confirmation;
    }

    @Trace
    @Override
    public TransferList<?> getMembers(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, READ);
        if (useIndexForMembers) {
            return MembersList.create(permissionEvaluator, mediaService.getMemberItems(media));
        } else {
            return MembersList.create(permissionEvaluator, mediaService.getMembers(media).stream().map(MemberRefItem::create).collect(Collectors.toList()));
        }
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateMembers(Long mediaId, MultiValueMap<String, String> map) throws CircularReferenceException {
        MediaObject owner = get(mediaId);
        if(owner == null) {
            return notFound(mediaId);
        }

        MembersView[] membersViews;
        try {
            membersViews = unmarshal(map, MembersView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        MembersList confirmation = new MembersList(true, "Updated members");

        for(MembersView view : membersViews) {
            MediaObject member = mediaService.get(view.getMemberId());
            if(member == null) {
                continue;
            }

            if(view.getId() != null) {
                if(view.isDeleted()) {
                    MemberRef ref = mediaService.getMemberRef(view.getId());
                    if(ref == null) {
                        continue;
                    }

                    boolean success = mediaService.removeMemberOf(member, ref);
                    if(success) {
                        confirmation.add(view);
                    }
                    continue;
                }

                MemberRef updatedRef = new MemberRef(view.getId(), member,
                    owner, view.getNumber());
                updatedRef.setHighlighted(view.isHighlighted());
                updatedRef = mediaService
                    .updateMemberNumber(member, updatedRef);
                confirmation.add(MembersView.create(permissionEvaluator, MemberRefItem.create(updatedRef)));
            } else {
                checkPermission(member, MEMBER_OF_WRITE);

                try {
                    MemberRef ref = mediaService.addMember(owner, member,
                        view.getNumber());
                    MembersView result = MembersView.create(permissionEvaluator, MemberRefItem.create(ref));
                    confirmation.add(result);
                } catch(CircularReferenceException e) {
                    return circularReference();
                }
            }

        }

        return confirmation;
    }

    @Trace
    @Override
    public TransferList<?> getMemberOf(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, READ);

        return MemberRefList.createMemberOf(permissionEvaluator, media);
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateMemberOfs(Long mediaId, MultiValueMap<String, String> map) throws CircularReferenceException {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, MEMBER_OF_WRITE);

        final MemberRefView[] memberRefViews;
        try {
            memberRefViews = unmarshal(map, MemberRefView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        MemberRefList confirmation = new MemberRefList(true,
            "Updated member of");

        for(MemberRefView view : memberRefViews) {
            if(view.isDeleted()) {
                MemberRef ref = mediaService.getMemberRef(view.getId());

                boolean success = mediaService.removeMemberOf(media, ref);
                if(success) {
                    confirmation.add(view);
                }
                continue;
            }

            MediaObject ownerMediaObject = mediaService.get(view.getReferenceId());
            if(ownerMediaObject == null) {
                continue;
            }

            MemberRef ref = mediaService.addMemberOf(media, ownerMediaObject);
            confirmation.add(MemberRefView.create(permissionEvaluator, ref));
        }

        return confirmation;
    }

    @Trace
    @Override
    public TransferList<?> getImages(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, READ);

        return ImageList.create(media);
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateImages(Long mediaId, MultiValueMap<String, String> map) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, WRITE);

        final ImageView[] imageView;

        try {
            imageView = unmarshal(map, ImageView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }


        // new images must be added in sorted order
        List<ImageView> sortedImageViews = Arrays.asList(imageView);
        Collections.sort(sortedImageViews, new Comparator<ImageView>() {
            @Trace
            @Override
            public int compare(ImageView o1, ImageView o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });

        ImageList confirmation = new ImageList(true, "Updated images");

        for(ImageView view : sortedImageViews) {
            if(view.isDeleted()) {
                boolean succes = media.removeImage(view.getId());
                if(succes) {
                    confirmation.add(view);
                }
                continue;
            }

            final Image incoming = view.toImage();
            final Image existing = media.getImage(incoming);
            int index = view.getIndex();

            if(existing != null) {
                view.updateTo(existing);
                media.removeImage(existing);
                media.addImage(existing, index);
            } else {
                media.addImage(incoming, index);
                media = mediaService.merge(media);
            }

            Image stored = media.getImage(index);
            if(stored != null) {
                ImageView result = ImageView.create(stored, index);
                confirmation.add(result);
            } else {
                LOG.warn("Could not find image at {} on media {}", index, media.getMid());
            }
        }

        mediaService.merge(media);

        return confirmation;
    }

    @Trace
    @Override
    public TransferList<?> getPersons(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, READ);

        return PersonList.create(media.getPersons());
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdatePersons(Long mediaId, MultiValueMap<String, String> map) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, WRITE);

        final PersonView[] personViews;
        try {
            personViews = unmarshal(map, PersonView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        PersonList confirmation = new PersonList(true, "Updated persons");

        for(PersonView view : personViews) {
            Person incoming = view.toPerson();
            Person existing = media.findPerson(incoming);

            if(existing != null) {
                if(view.isDeleted()) {
                    boolean success = media.removePerson(existing);

                    if(success) {
                        confirmation.add(view);
                    }
                    continue;
                }

                view.updateTo(existing);
                confirmation.add(view);
            } else {
                if(view.isDeleted()) {
                    LOG.warn("Received delete for non-existent person {} for media object {}", incoming, media.getId());
                } else {
                    media.addPerson(incoming);
                    media = mediaService.mergeWithoutPublishing(media);
                    PersonView result = PersonView.create(media.getPersons().get(media.getPersons().size() - 1));
                    confirmation.add(result);
                }
            }
        }

        mediaService.merge(media);

        return confirmation;
    }

    @Trace
    @Override
    public TransferList<?> getWebsites(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, READ);

        return WebsiteList.create(media.getWebsites());
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateWebsites(Long mediaId, MultiValueMap<String, String> map) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, WRITE);

        final WebsiteView[] websiteViews;
        try {
            websiteViews = unmarshal(map, WebsiteView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        WebsiteList confirmation = new WebsiteList(true, "Updated websites");

        for(WebsiteView view : websiteViews) {
            if(view.isDeleted()) {
                boolean success = media.removeWebsite(view.getId());
                if(success) {
                    confirmation.add(view);
                }
                continue;
            }

            Website incoming = view.toWebsite();
            Website existing = media.getWebsite(incoming);
            int index = view.getIndex();

            if(existing != null) {
                view.updateTo(existing);
                media.removeWebsite(existing);
                media.addWebsite(index, existing);
            } else {
                media.addWebsite(index, incoming);
                media = mediaService.merge(media);
            }

            Website stored = media.getWebsites().get(index);
            WebsiteView result = WebsiteView.create(stored, index);
            confirmation.add(result);
        }

        mediaService.merge(media);

        return confirmation;
    }


    @Trace
    @Override
    public TransferList<?> getPortalRestrictions(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, READ);

        return RestrictionList.create(media.getPortalRestrictions());
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdatePortalRestrictions(Long mediaId, MultiValueMap<String, String> map) {
        RestrictionList response;
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, WRITE);

        RestrictionView[] restrictionViews;
        try {
            restrictionViews = unmarshal(map, RestrictionView.class);
        } catch(JsonParseException e) { // unmarshal
            return malFormed(e.getMessage());
        }

        response = new RestrictionList(true, "Updated exclusives");

        for(RestrictionView rawIncomingView : restrictionViews) {
            PortalRestriction restrictionToUpdate = null;
            PortalRestriction incoming = rawIncomingView.toRestriction(PortalRestriction.class);

            for(PortalRestriction existing : media.getPortalRestrictions()) {
                if(existing.getId().equals(incoming.getId())) {
                    restrictionToUpdate = existing;
                    break;
                }
            }

            if(restrictionToUpdate != null) {
                if(rawIncomingView.isDeleted()) {
                    boolean success = media.removePortalRestriction(restrictionToUpdate);

                    if(success) {
                        response.add(rawIncomingView);
                    }
                    continue;
                }

                rawIncomingView.updateTo(restrictionToUpdate); // inverse logic???
                response.add(rawIncomingView);
            } else if(!rawIncomingView.isDeleted()) {

                media.addPortalRestriction(incoming);
                media = mediaService.mergeWithoutPublishing(media);
                RestrictionView result = RestrictionView.create(incoming);
                response.add(result);
            }
        }

        mediaService.merge(media);

        return response;
    }

    @Trace
    @Override
    public TransferList<?> getGeoRestrictions(Long mediaId) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }
        checkPermission(media, READ);

        return RestrictionList.create(media.getGeoRestrictions());
    }

    @Trace
    @Override
    public TransferList<?> createOrUpdateGeoRestrictions(Long mediaId, MultiValueMap<String, String> map) {
        boolean isPublicTVChannel = false;
        RestrictionList response;
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, WRITE);

        // MSE-1755:
        // - webonly: allow change of restrictions, but region NL only (isCeres == true && isPublicTVChannel == false)
        // - NED1-3: do not allow change of restrictions at all (isCeres == true && isPublicTVChannel == true)
        // both case apply to ceres only (mediaobject has ceres record)
        final boolean isCeres = media.getLocationAuthorityRecord(Platform.INTERNETVOD) != null;
        if(isCeres) {
            // webonly doesn't have scheduleevents, so allowChange will never be set to false
            SortedSet<ScheduleEvent> scheduleEvents = media.getScheduleEvents();
            for(ScheduleEvent scheduleEvent : scheduleEvents) {
                if(Channel.NED1.equals(scheduleEvent.getChannel()) ||
                    Channel.NED2.equals(scheduleEvent.getChannel()) ||
                    Channel.NED3.equals(scheduleEvent.getChannel())) {
                    isPublicTVChannel = true;
                    break;
                }
            }
        }

        RestrictionView[] restrictionViews;
        try {
            restrictionViews = unmarshal(map, RestrictionView.class);
        } catch(JsonParseException e) {
            return malFormed(e.getMessage());
        }

        response = new RestrictionList(true, "Updated georestrictions");

        for(RestrictionView rawIncomingView : restrictionViews) {
            GeoRestriction ruleToUpdate = null;
            GeoRestriction ruleFromView = rawIncomingView.toRestriction(GeoRestriction.class);

            for(GeoRestriction existing : media.getGeoRestrictions()) {
                if(existing.getId().equals(ruleFromView.getId())) {
                    ruleToUpdate = existing;
                    break;
                }
            }

            // - ceres webonly allows NL regions only
            if(isCeres && !isPublicTVChannel && !Region.NL.equals(ruleFromView.getRegion())) {
                return new ErrorList("It is not allowed to edit geo restrictions other than NL Public to CERES webonly media");
            }

            // - ceres public channel broadcast doesn't allow any change
            if(isCeres && isPublicTVChannel) {
                return new ErrorList("It is not allowed to edit geo restrictions on broadcasted CERES media");
            }

            if(ruleToUpdate != null) {
                if(rawIncomingView.isDeleted()) {
                    boolean success = media.removeGeoRestriction(ruleToUpdate);

                    if(success) {
                        response.add(rawIncomingView);
                    }
                    continue;
                }

                rawIncomingView.updateTo(ruleToUpdate); // inverse logic???
                response.add(rawIncomingView);
            } else if(!rawIncomingView.isDeleted()) {
                // add restriction if applicable
                media.addGeoRestriction(ruleFromView);
                media = mediaService.mergeWithoutPublishing(media);
                RestrictionView result = RestrictionView.create(ruleFromView);
                response.add(result);
            }
        }

        mediaService.merge(media);

        return response;
    }

    @Trace
    @Override
    public TransferList<?> getOwnerData(Long mediaId, OwnerType owner) {
        MediaObject media = get(mediaId);
        if(media == null) {
            return notFound(mediaId);
        }

        checkPermission(media, READ);

        return OwnerDataList.create(media, owner);
    }


    @Override
    public boolean getUseIndexForSearch() {
        return useIndexForSearch;
    }


    @Override
    public void setUseIndexForSearch(boolean useIndexForSearch) {
        this.useIndexForSearch = useIndexForSearch;
    }


    @Override
    public boolean getUseIndexForMembers() {
        return useIndexForMembers;
    }


    @Override
    public void setUseIndexForMembers(boolean useIndexForMembers) {
        this.useIndexForMembers = useIndexForMembers;
    }

    private void checkPermission(MediaObject media, String permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!permissionEvaluator.hasPermission(authentication, media, permission)) {
            throw new SecurityException("User lacks permission: " + permission);
        }
    }

    private void checkPermission(MediaObject media, Permission permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!permissionEvaluator.hasPermission(authentication, media, permission)) {
            throw new SecurityException("User lacks permission: " + permission);
        }
    }

    private boolean hasWritePermission(MediaObject media) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return permissionEvaluator.hasPermission(authentication, media, WRITE);
    }

    private boolean hasGenrePermission(MediaObject media) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return permissionEvaluator.hasPermission(authentication, media, GENRE_WRITE);
    }

    private boolean hasRatingPermission(MediaObject media) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return permissionEvaluator.hasPermission(authentication, media, RATINGS_WRITE);
    }

    private boolean hasPermission(DomainObject object, Object permission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return permissionEvaluator.hasPermission(authentication, object, permission);
    }

    private <T extends ExtRecord> T[] unmarshal(MultiValueMap<String, String> map, Class<T> clazz) {
        String rawJson = map.getFirst("list");
        return unmarshal(rawJson, clazz);
    }

    protected <T extends ExtRecord> T[] unmarshal(String rawJson, Class<T> clazz) {
        T[] result;

        try {
            ObjectMapper mapper = getObjectMapperWithRFC3339Support();
            JavaType arrayType = mapper.getTypeFactory().constructArrayType(clazz);
            if(!rawJson.startsWith("[")) {
                rawJson = "[" + rawJson + ']';
            }

            result = mapper.readValue(rawJson, arrayType);
        } catch(IOException e) {
            throw new JsonParseException(e.getMessage() + "\n" + rawJson, e);
        }

        return result;
    }

    private static class JsonParseException extends RuntimeException {
        private static final long serialVersionUID = 4307632718286844821L;

        JsonParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private static ErrorList notFound(long id) {
        return new ErrorList("No media for id: " + id);
    }

    private static ErrorList malFormed(String json) {
        return new ErrorList("Malformed JSON: " + json);
    }

    private static ErrorList circularReference() {
        return new ErrorList("Creating a circular refence.");
    }

    private MediaObject copyInput(MediaObject media, MultiValueMap<String, String> map) throws IllegalArgumentException, ParseException, ModificationException {
        final String avType = map.getFirst("avType");
        try {
            media.setAVType(AVType.valueOf(avType));
        } catch(IllegalArgumentException e) {
            throw new IllegalArgumentException(String.format(
                "Must supply a valid avType, got: %1$s", avType));
        }

        updateBroadcasters(media, map);
        updatePortals(media, map);
        updateTags(media, map);

        {
            final String title = map.getFirst("title");
            if(Helper.isNotEmpty(title)) {
                if(!title.equals(media.getMainTitle())) {
                    media.addTitle(title, OwnerType.BROADCASTER, TextualType.MAIN);
                }
            } else {
                throw new IllegalArgumentException(String.format(
                    "Must supply a valid title, got: %1$s", title));
            }
        }
        {
            final String subTitle = map.getFirst("subTitle");
            if(Helper.isNotEmpty(subTitle)) {
                if(!subTitle.equals(media.getSubTitle())) {
                    media.addTitle(subTitle, OwnerType.BROADCASTER, TextualType.SUB);
                }
            } else {
                media.removeTitle(OwnerType.BROADCASTER, TextualType.SUB);
            }
        }
        {
            final String shortTitle = map.getFirst("shortTitle");
            if(Helper.isNotEmpty(shortTitle)) {
                if(!shortTitle.equals(media.getShortTitle())) {
                    media.addTitle(shortTitle, OwnerType.BROADCASTER,
                        TextualType.SHORT);
                }
            } else {
                media.removeTitle(OwnerType.BROADCASTER, TextualType.SHORT);
            }
        }
        {
            final String originalTitle = map.getFirst("originalTitle");
            if(Helper.isNotEmpty(originalTitle)) {
                if(!originalTitle.equals(media.getOriginalTitle())) {
                    media.addTitle(originalTitle, OwnerType.BROADCASTER,
                        TextualType.ORIGINAL);
                }
            } else {
                media.removeTitle(OwnerType.BROADCASTER, TextualType.ORIGINAL);
            }
        }
        {
            final String workTitle = map.getFirst("workTitle");
            if(Helper.isNotEmpty(workTitle)) {
                if(!workTitle.equals(media.getWorkTitle())) {
                    media.addTitle(workTitle, OwnerType.BROADCASTER,
                        TextualType.WORK);
                }
            } else {
                media.removeTitle(OwnerType.BROADCASTER, TextualType.WORK);
            }
        }
        {
            final String lexicoTitle = map.getFirst("lexicoTitle");
            if(Helper.isNotEmpty(lexicoTitle)) {
                if(!lexicoTitle.equals(media.getLexicoTitle())) {
                    media.addTitle(lexicoTitle, OwnerType.BROADCASTER,
                        TextualType.LEXICO);
                }
            } else {
                media.removeTitle(OwnerType.BROADCASTER, TextualType.LEXICO);
            }
        }
        {
            final String abbreviatedTitle = map.getFirst("abbreviatedTitle");
            if(Helper.isNotEmpty(abbreviatedTitle)) {
                if(!abbreviatedTitle.equals(media.getAbbreviatedTitle())) {
                    media.addTitle(abbreviatedTitle, OwnerType.BROADCASTER,
                        TextualType.ABBREVIATION);
                }
            } else {
                media.removeTitle(OwnerType.BROADCASTER, TextualType.ABBREVIATION);
            }
        }
        {
            final String description = map.getFirst("description");
            if(Helper.isNotEmpty(description)) {
                if(!description.equals(media.getMainDescription())) {
                    media.addDescription(description, OwnerType.BROADCASTER,
                        TextualType.MAIN);
                }
            } else {
                media.removeDescription(OwnerType.BROADCASTER, TextualType.MAIN);
            }
        }
        {
            final String shortDescription = map.getFirst("shortDescription");
            if(Helper.isNotEmpty(shortDescription)) {
                if(!shortDescription.equals(media.getShortDescription())) {
                    media.addDescription(shortDescription, OwnerType.BROADCASTER,
                        TextualType.SHORT);
                }
            } else {
                media.removeDescription(OwnerType.BROADCASTER, TextualType.SHORT);
            }
        }
        {
            final String kickerDescription = map.getFirst("kickerDescription");
            if(Helper.isNotEmpty(kickerDescription)) {
                if(!kickerDescription.equals(media.getShortDescription())) {
                    media.addDescription(kickerDescription, OwnerType.BROADCASTER,
                        TextualType.KICKER);
                }
            } else {
                media.removeDescription(OwnerType.BROADCASTER, TextualType.KICKER);
            }
        }
        {
            final String episodeDescription = map.getFirst("episodeDescription");
            if(Helper.isNotEmpty(episodeDescription)) {
                String existing = MediaObjects.getDescription(media, TextualType.EPISODE);
                if(!episodeDescription.equals(existing)) {
                    media.addDescription(episodeDescription, OwnerType.BROADCASTER,
                        TextualType.EPISODE);
                }
            } else {
                media.removeDescription(OwnerType.BROADCASTER, TextualType.EPISODE);
            }
        }
        {
            final String duration = map.getFirst("duration");
            if(Helper.isNotEmpty(duration)) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                Date date = sdf.parse(duration);
                media.setDurationWithDate(date);
            } else {
                media.setDurationWithDate(null);
            }
        }
        {
            final String releaseYear = map.getFirst("releaseYear");
            if(Helper.isNotEmpty(releaseYear)) {
                media.setReleaseYear(Short.valueOf(releaseYear));
            } else {
                media.setReleaseYear(null);
            }
        }
        {
            final boolean isEmbeddable = "on".equals(map.getFirst("embeddable"));
            if(isEmbeddable != media.isEmbeddable()) {
                media.setEmbeddable(isEmbeddable);
            }
        }
        {
            final String publishStart = map.getFirst("publishStart");
            if(Helper.isNotEmpty(publishStart)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = sdf.parse(publishStart);
                media.setPublishStart(date);
            } else {
                media.setPublishStart(null);
            }
        }
        {
            final String publishStop = map.getFirst("publishStop");
            if(Helper.isNotEmpty(publishStop)) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = sdf.parse(publishStop);
                media.setPublishStop(date);
            } else {
                media.setPublishStop(null);
            }
        }
        Kijkwijzer kijkwijzer = getKijkwijzer(map);
        if(kijkwijzer.isChanged(media) && hasRatingPermission(media)) {
            updateKijkwijzer(media, kijkwijzer);
        }

        SortedSet<Genre> newGenres = getGenres(map);
        if(ObjectUtils.notEqual(newGenres, media.getGenres()) && hasGenrePermission(media)) {
            media.setGenres(newGenres);
        }

        updateTwitter(media, map);


        if(media instanceof Group) {
            return mergeGroup((Group)media, map);
        } else if(media instanceof Program) {
            return mergeProgram((Program)media, map);
        } else if(media instanceof Segment) {
            return mergeSegment((Segment)media, map);
        }

        throw new UnsupportedOperationException("Unsupported media subclass: "
            + media.getClass().getSimpleName());
    }

    private void updateTags(MediaObject media, MultiValueMap<String, String> map) {
        final List<String> tags = map.get("tags");
        for(String tag : tags) {
            if(Helper.isNotEmpty(tag)) {
                Tag t = tagService.findOrCreate(tag);
                media.addTag(t);
            }
        }

        for(Iterator<Tag> iterator = media.getTags().iterator(); iterator.hasNext(); ) {
            Tag tag = iterator.next();
            if(!tags.contains(tag.getText())) {
                iterator.remove();
            }
        }
    }

    private void updateKijkwijzer(MediaObject media, Kijkwijzer kijkwijzer) {
        mediaService.setRatings(media, kijkwijzer.ageRating, kijkwijzer.contentRatings);
    }

    private Kijkwijzer getKijkwijzer(MultiValueMap<String, String> map) {
        Kijkwijzer kijkwijzer = new Kijkwijzer();
        final String ageRating = map.getFirst("ageRating");
        kijkwijzer.ageRating = AgeRating.xmlValueOf(ageRating);
        final List<String> contentRatings = map.get("contentRatings");
        kijkwijzer.contentRatings = ContentRating.valueOf(contentRatings);
        return kijkwijzer;

    }

    private class Kijkwijzer {
        AgeRating ageRating;
        List<ContentRating> contentRatings;

        public boolean isChanged(MediaObject media) {
            return ObjectUtils.notEqual(media.getAgeRating(), ageRating) || ObjectUtils.notEqual(media.getContentRatings(), contentRatings);
        }
    }





    private SortedSet<Genre> getGenres(MultiValueMap<String, String> map) {
        final List<String> genres = map.get("genres");
        return Genre.valueOf(genres);
    }

    private void updateTwitter(MediaObject media, MultiValueMap<String, String> map) {
        {
            String twitterhash = map.get("twitterhash").get(0);
            if(!twitterhash.startsWith("#")) {
                twitterhash = "#" + twitterhash;
            }
            TwitterRef hash = MediaObjects.getTwitterHash(media);
            if(hash == null) {
                if(!twitterhash.equals("#")) {
                    hash = new TwitterRef(twitterhash);
                    media.getTwitterRefs().add(hash);
                }
            } else {
                if(twitterhash.equals("#")) {
                    media.getTwitterRefs().remove(hash);
                } else {
                    hash.setValue(twitterhash);
                }
            }
        }
        {
            String twitteraccount = map.get("twitteraccount").get(0);
            if(!twitteraccount.startsWith("@")) {
                twitteraccount = "@" + twitteraccount;
            }

            TwitterRef account = MediaObjects.getTwitterAccount(media);
            if(account == null) {
                if(!twitteraccount.equals("@")) {
                    account = new TwitterRef(twitteraccount);
                    media.getTwitterRefs().add(account);
                }
            } else {
                if(twitteraccount.equals("@")) {
                    media.getTwitterRefs().remove(account);
                } else {
                    account.setValue(twitteraccount);
                }
            }
        }

    }

    private void updateBroadcasters(MediaObject media,
                                    MultiValueMap<String, String> map) {
        final List<String> broadcasters = map.get("broadcasters");

        // refresh list of broadcasters
        List<Broadcaster> currentBroadcasters = new ArrayList<>(media.getBroadcasters());
        for(Broadcaster broadcaster : currentBroadcasters) {
            media.removeBroadcaster(broadcaster);
        }

        if(Helper.isNotEmpty(broadcasters)) {
            for(String bcId : broadcasters) {
                if(Helper.isNotEmpty(bcId)) {
                    media.addBroadcaster(new Broadcaster(bcId, bcId));
                }
            }
        }

    }

    private void updatePortals(MediaObject media,
                               MultiValueMap<String, String> map) {
        final List<String> portals = map.get("portals");
        if(Helper.isNotEmpty(portals)) {
            for(String portalId : portals) {
                if(StringUtils.isNotBlank(portalId)) {
                    media.addPortal(new Portal(portalId, portalId));
                }
            }
        }

        if(media.getPortals() != null) {
            final List<Portal> toBeRemoved = new ArrayList<>();
            for(Portal portal : media.getPortals()) {
                if(!portals.contains(portal.getId())) {
                    toBeRemoved.add(portal);
                }
            }
            for(Portal portal : toBeRemoved) {
                media.removePortal(portal);
            }
        }
    }

    private Group mergeGroup(Group group, MultiValueMap<String, String> map) {
        if(group.getId() == null) {
            group = mediaService.merge(group);
        }

        final boolean isOrdered = "on".equals(map.getFirst("ordered"));
        if(isOrdered && !group.isOrdered()) {
            group = mediaService.setOrdered(group);
        } else if(!isOrdered && group.isOrdered()) {
            group = mediaService.setUnordered(group);
        }

        return group;
    }

    private Program mergeProgram(Program program,
                                 MultiValueMap<String, String> map) {
        return program;
    }

    private Segment mergeSegment(Segment segment,
                                 MultiValueMap<String, String> map) throws ParseException {
        final String start = map.getFirst("start");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = sdf.parse(start);
        segment.setStart(date);

        return segment;
    }

    private <T extends Identifiable<Long>> T latestEntry(
        Collection<T> association) {
        T latest = null;
        for(T entry : association) {
            if(latest == null || entry.getId() > latest.getId()) {
                latest = entry;
            }
        }
        return latest;
    }

    private ObjectMapper getObjectMapperWithRFC3339Support() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss");
        //DRS: Deprecated method. mapper.getDeserializationConfig().setDateFormat(dateFormat);
        mapper.setDateFormat(dateFormat);
        return mapper;
    }
}
