/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.xml.bind.JAXB;

import nl.vpro.domain.LocalizedString;
import nl.vpro.domain.classification.Term;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Editor;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.ThirdParty;
import nl.vpro.util.DateUtils;

import static nl.vpro.util.DateUtils.toDate;

public interface MediaBuilder<B extends MediaBuilder<B, M>, M extends MediaObject>   {

    static ProgramBuilder program() {
        return new ProgramBuilder();
    }

    static ProgramBuilder program(ProgramType type) {
        return program().type(type);
    }

    static ProgramBuilder program(Program program) {
        return new ProgramBuilder(program);
    }

    static GroupBuilder group() {
        return new GroupBuilder();
    }

    static GroupBuilder group(GroupType type) {
        return group().type(type);
    }

    static GroupBuilder group(Group group) {
        return new GroupBuilder(group);
    }

    static SegmentBuilder segment() {
        return new SegmentBuilder();
    }

    static SegmentBuilder segment(Segment segment) {
        return new SegmentBuilder(segment);
    }

    M build();

    @SuppressWarnings("unchecked")
    default B id(Long id) {
        build().setId(id);
        return (B)this;
    }

    default B mid(String mid) {
        build().setMid(mid);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B urn(String urn) {
        build().setUrn(urn);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B hasSubtitles(boolean hasSubtitles) {
        build().setHasSubtitles(hasSubtitles);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B createdBy(Editor user) {
        build().setCreatedBy(user);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B createdBy(String user) {
        build().setCreatedBy(user(user));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B lastModifiedBy(Editor user) {
        build().setLastModifiedBy(user);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B lastModifiedBy(String user) {
        build().setLastModifiedBy(user(user));
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B creationDate(Date date) {
        build().setCreationDate(date);
        return (B)this;
    }

    default B creationDate(Instant date) {
        return creationDate(toDate(date));
    }

    default B creationDate(LocalDateTime date) {
        return creationDate(fromLocalDate(date));
    }

    default B clearCreationDate() {
        build().setCreationDate(null);
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B lastModified(Date date) {
        build().setLastModified(date);
        return (B)this;
    }
    default B lastModified(Instant date) {
        return lastModified(toDate(date));
    }

    default B lastModified(LocalDateTime date) {
        return lastModified(fromLocalDate(date));
    }

    @SuppressWarnings("unchecked")
    default B publishStart(Date date) {
        build().setPublishStart(date);
        return (B)this;
    }

    default B publishStart(Instant date) {
        return publishStart(toDate(date));
    }

    default B publishStart(LocalDateTime date) {
        return publishStart(fromLocalDate(date));
    }

    @SuppressWarnings("unchecked")
    default B publishStop(Date date) {
        build().setPublishStop(date);
        return (B)this;
    }


    default B publishStop(Instant date) {
        return publishStop(toDate(date));
    }

    default B publishStop(LocalDateTime date) {
        return publishStop(fromLocalDate(date));
    }

    @SuppressWarnings("unchecked")
    default B lastPublished(Date date) {
        build().setLastPublished(date);
        return (B) this;
    }

    default B lastPublished(Instant date) {
        return lastPublished(toDate(date));
    }

    default B lastPublished(LocalDateTime date) {
        return lastPublished(fromLocalDate(date));
    }

    @SuppressWarnings("unchecked")
    default B workflow(Workflow workflow) {
        build().setWorkflow(workflow);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B crids(String... crids) {
        for(String crid : crids) {
            build().addCrid(crid);
        }
        return (B)this;
    }


    @SuppressWarnings("unchecked")
    default B broadcasters(Broadcaster... broadcasters) {
        for(Broadcaster broadcaster : broadcasters) {
            build().addBroadcaster(broadcaster);
        }
        return (B)this;
    }

    default B broadcasters(String... broadcasters) {
        for (String broadcaster : broadcasters) {
            build().addBroadcaster(new Broadcaster(broadcaster));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B clearBroadcasters() {
        build().getBroadcasters().clear();
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B portals(Portal... portals) {
        for (Portal portal : portals) {
            build().addPortal(portal);
        }
        return (B) this;
    }

    default B portals(String... portals) {
        for (String portal : portals) {
            build().addPortal(new Portal(portal));
        }
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    default B clearPortals() {
        build().clearPortals();
        return (B)this;
    }


    @SuppressWarnings("unchecked")
    default B thirdParties(ThirdParty... thirdParties) {
        for(ThirdParty thirdParty : thirdParties) {
            build().addThirdParty(thirdParty);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B portalRestrictions(PortalRestriction... restrictions) {
        for(PortalRestriction publicationRule : restrictions) {
            build().addPortalRestriction(publicationRule);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B portalRestrictions(Portal... restrictions) {
        for (Portal publicationRule : restrictions) {
            build().addPortalRestriction(new PortalRestriction(publicationRule));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B portalRestrictions(String... portals) {
        for (String portal : portals) {
            build().addPortalRestriction(new PortalRestriction(new Portal(portal)));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B geoRestrictions(GeoRestriction... restrictions) {
        for(GeoRestriction publicationRule : restrictions) {
            build().addGeoRestriction(publicationRule);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B geoRestrictions(Region... restrictions) {
        for (Region region : restrictions) {
            build().addGeoRestriction(new GeoRestriction(region));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B titles(Title... titles) {
        for(Title title : titles) {
            build().addTitle(title);
        }
        return (B)this;
    }

    default B mainTitle(String title, OwnerType owner) {
        return titles(new Title(title, owner, TextualType.MAIN));
    }

    default B mainTitle(String title) {
        return mainTitle(title, OwnerType.BROADCASTER);
    }


    @SuppressWarnings("unchecked")
    default B descriptions(Description... descriptions) {
        for(Description description : descriptions) {
            build().addDescription(description);
        }
        return (B)this;
    }

    default B mainDescription(String description, OwnerType owner) {
        return descriptions(new Description(description, owner, TextualType.MAIN));
    }

    default B mainDescription(String description) {
        return mainDescription(description, OwnerType.BROADCASTER);
    }

    @SuppressWarnings("unchecked")
    default B genres(Genre... genres) {
        for(Genre genre : genres) {
            build().addGenre(genre);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B genres(Collection<Genre> genres) {
        for(Genre genre : genres) {
            build().addGenre(genre);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B genres(String... termIds) {
        for (String genre : termIds) {
            build().addGenre(new Genre(new Term(genre)));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B tags(Tag... tags) {
        for(Tag tag : tags) {
            build().addTag(tag);
        }
        return (B)this;
    }


    @SuppressWarnings("unchecked")
    default B tags(String... tags) {
        for (String tag : tags) {
            build().addTag(new Tag(tag));
        }
        return (B) this;
    }


    @SuppressWarnings("unchecked")
    default B source(String source) {
        build().setSource(source);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B countries(String... countries) {
        for(String country : countries) {
            build().addCountry(country);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B languages(String... languages) {
        for(String language : languages) {
            build().addLanguage(LocalizedString.adapt(language));
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B avType(AVType type) {
        build().setAVType(type);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B avAttributes(AVAttributes avAttribute) {
        build().setAvAttributes(avAttribute);
        return (B)this;

    }

    @SuppressWarnings("unchecked")
    default B aspectRatio(AspectRatio as) {
        AVAttributes av = build().getAvAttributes();
        if(av == null) {
            av = new AVAttributes();
            build().setAvAttributes(av);
        }
        VideoAttributes va = av.getVideoAttributes();
        if(va == null) {
            va = new VideoAttributes();
            av.setVideoAttributes(va);
        }
        va.setAspectRatio(as);
        return (B)this;

    }

    @SuppressWarnings("unchecked")
    @Deprecated
    default B duration(Date duration) {
        try {
            build().setDurationWithDate(duration);
        } catch (ModificationException e) {
            throw new IllegalStateException(e);
        }
        return (B)this;
    }


    @SuppressWarnings("unchecked")
    default B duration(java.time.Duration duration) {
        try {
            build().setDuration(duration);
        } catch (ModificationException e) {
            throw new IllegalStateException(e);
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B releaseYear(Short y) {
        build().setReleaseYear(y);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B persons(Person... persons) {
        for(Person person : persons) {
            build().addPerson(person);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B awards(String... awards) {
        for(String award : awards) {
            build().addAward(award);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B memberOf(MemberRef... memberRef) throws CircularReferenceException {
        build().getMemberOf().addAll(Arrays.asList(memberRef));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B memberOf(MediaObject media, Integer number) throws CircularReferenceException {
        build().createMemberOf(media, number);
        return (B) this;
    }


    default B memberOf(String mid, Integer number) throws CircularReferenceException {
        return memberOf(new MemberRef(mid, number));
    }


    default B memberOf(String mid) throws CircularReferenceException {
        return memberOf(new MemberRef(mid));
    }

    @SuppressWarnings("unchecked")
    default B ageRating(AgeRating ageRating) {
        build().setAgeRating(ageRating);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B contentRatings(ContentRating... contentRatings) {
        build().setContentRatings(Arrays.asList(contentRatings));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B emails(String... emails) {
        build().setEmail(Arrays.asList(emails));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B websites(Website... websites) {
        build().setWebsites(Arrays.asList(websites));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B websites(String... websites) {
        build().setWebsites(Arrays.stream(websites).map(Website::new).collect(Collectors.toList()));
        return (B) this;
    }

    default B twitterRefs(String... twitter) {
        List<TwitterRef> reference = new ArrayList<>();
        for(String t : twitter) {
            reference.add(new TwitterRef(t));
        }
        build().setTwitterRefs(reference);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B teletext(Short teletext) {
        build().setTeletext(teletext);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B locations(Location... locations) {
        for(Location location : locations) {
            build().addLocation(location);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B locations(String... locations) {
        for (String location : locations) {
            build().addLocation(new Location(location, OwnerType.BROADCASTER));
        }
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B clearLocations() {
        build().getLocations().clear();
        return (B) this;
    }

    @SuppressWarnings("unchecked")
    default B scheduleEvents(ScheduleEvent... scheduleEvents) {
        for(ScheduleEvent event : scheduleEvents) {
            event.setMediaObject(build());
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B descendantOf(DescendantRef... refs) throws CircularReferenceException {
        build().setDescendantOf(new TreeSet<>(Arrays.asList(refs)));
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B relations(Relation... relations) {
        for(Relation relation : relations) {
            build().addRelation(relation);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B images(Image... images) {
        for(Image image : images) {
            build().addImage(image);
        }
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B embeddable(boolean isEmbeddable) {
        build().setEmbeddable(isEmbeddable);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B authorityRecord(LocationAuthorityRecord record) {
        build().setLocationAuthorityRecord(record);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B mergedTo(MediaObject media) {
        workflow(Workflow.MERGED);
        build().setMergedTo(media);
        return (B)this;
    }

    @SuppressWarnings("unchecked")
    default B mergedTo(String media) {
        workflow(Workflow.MERGED);
        build().setMergedToRef(media);
        return (B) this;
    }


    /**
     * Makes a (deep) copy of this builder. This returns a new instance on which you can make changes without affecting the original one.
     */
    B copy();

    static Date fromLocalDate(LocalDateTime date) {
        return DateUtils.toDate(date, Schedule.ZONE_ID);
    }

    static Editor user(String principalId) {
        return new Editor(principalId, null, null, null, null);
    }


    abstract class AbstractBuilder<T extends AbstractBuilder<T, M>, M extends MediaObject>  implements MediaBuilder<T, M>, Cloneable {
        @Valid
        transient M mediaObject;

        @Override
        public M build() {
            return mediaObject;
        }

        @Override
        public T copy() {
            try {
                T o = (T) super.clone();
                StringWriter writer = new StringWriter();
                JAXB.marshal(this.mediaObject, writer);
                o.mediaObject = MediaObjects.deepCopy(this.mediaObject);
                return o;
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    abstract class AbstractProgramBuilder<T extends AbstractProgramBuilder<T> & MediaBuilder<T,Program>> extends AbstractBuilder<T, Program> implements MediaBuilder<T, Program> {

        protected AbstractProgramBuilder() {
            this.mediaObject = new Program();
        }

        protected AbstractProgramBuilder(Program program) {
            this.mediaObject = program;
        }

        public T type(ProgramType type) {
            build().setType(type);
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T episodeOf(MemberRef... memberRef) throws CircularReferenceException {
            build().getEpisodeOf().addAll(Arrays.asList(memberRef));
            return (T) this;
        }

        @SuppressWarnings("unchecked")
        public T episodeOf(Group group, Integer number) throws CircularReferenceException {
            build().createEpisodeOf(group, number);
            return (T) this;
        }

        public T episodeOf(String mid, Integer number) {
            return episodeOf(new MemberRef(mid, number));
        }

        public T episodeOf(String mid) {
            return episodeOf(new MemberRef(mid));
        }

        public T segments(Segment... segments) {
            return segments(Arrays.asList(segments));
        }

        @SuppressWarnings("unchecked")
        public T segments(Iterable<Segment> segments) {
            for (Segment segment : segments) {
                build().addSegment(segment);
            }
            return (T) this;
        }



        @SuppressWarnings("unchecked")
        public T predictions(Prediction... predictions) {
            if(predictions == null || predictions.length == 0) {
                build().getPredictions().clear();
            } else {
                for(Prediction pred : predictions) {
                    pred.setMediaObject(mediaObject);
                    build().getPredictions().add(pred);
                }
            }
            return (T) this;
        }

    }

    class ProgramBuilder extends AbstractProgramBuilder<ProgramBuilder> {
        protected ProgramBuilder() {
        }

        protected ProgramBuilder(Program program) {
            super(program);
        }
    }

    abstract  class AbstractGroupBuilder<T extends AbstractGroupBuilder<T>> extends AbstractBuilder<T, Group> implements MediaBuilder<T, Group> {

        protected AbstractGroupBuilder() {
            this.mediaObject = new Group();
        }

        protected AbstractGroupBuilder(Group group) {
            this.mediaObject = group;
        }

        public T type(GroupType type) {
            build().setType(type);
            return (T) this;
        }

        public T poSeriesID(String poSeriesID) {
            build().setMid(poSeriesID);
            return (T) this;
        }
        public T ordered(Boolean ordered) {
            build().setOrdered(ordered);
            return (T) this;
        }
    }
    class GroupBuilder extends AbstractGroupBuilder<GroupBuilder> {
        protected GroupBuilder() {
        }

        protected GroupBuilder(Group group) {
            super(group);
        }
    }

    abstract class AbstractSegmentBuilder<T extends AbstractSegmentBuilder<T>> extends AbstractBuilder<T, Segment> implements MediaBuilder<T, Segment> {

        protected AbstractSegmentBuilder() {
            this.mediaObject = new Segment();
        }

        protected AbstractSegmentBuilder(Segment segment) {
            this.mediaObject = segment;
        }

        /**
         * @deprecated  Use #start(java.time.Duration)
         */
        @Deprecated
        public T start(Date start) {
            build().setStart(start);
            return (T) this;
        }

        public T start(java.time.Duration start) {
            build().setStart(start);
            return (T) this;
        }

        public T parent(ProgramUpdate parent) {
            build().setParent(parent.fetch());
            return (T) this;
        }

        public T midRef(String midRef) {
            build().setMidRef(midRef);
            return (T) this;
        }
    }

    class SegmentBuilder extends AbstractSegmentBuilder<SegmentBuilder> {
        protected SegmentBuilder() {
        }

        protected SegmentBuilder(Segment segment) {
            super(segment);
        }
    }
}
