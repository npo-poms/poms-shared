/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.*;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.*;
import org.meeuw.i18n.regions.bind.jaxb.Code;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.*;
import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.TwitterRef;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.validation.BroadcasterValidation;
import nl.vpro.domain.validation.ValidEmbargo;
import nl.vpro.i18n.validation.MustDisplay;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.util.*;
import nl.vpro.validation.*;
import nl.vpro.xml.bind.*;


/**
 * A MediaUpdate is meant for communicating updates. It is not meant as a complete representation of the object.
 * <p>
 * A MediaUpdate is like a {@link MediaObject} but
 * <ul>
 *  <li>It does not have {@link MutableOwnable} objects. When converting between a MediaUpdate and a MediaObject one need to indicate for which owner type this must happen.
 *  If you are updating you are always associated with a certain owner (normally {@link OwnerType#BROADCASTER}), so there is no case for updating fields of other owners.
 * </li>
 * <li>It contains fewer implicit fields. E.g. a Broadcaster is just an id, and it does not contain a better string representation.
 *  These kind of fields are non modifiable, or are implicitely calculated. So there is no case in updating them.
 * </li>
 * <li>It may contain a 'version'
 * Some code may check this version to know whether certain fields ought to be ignored or not. This is to arrange forward and backwards compatibility.
 * It may e.g. happen that a newer version of POMS has a new field. If you are not aware of this, sending an update XML without the field may result in the value to be emptied.
 * To indicate that you <em>are</em> aware, you should sometimes supply a sufficiently high version.
 * </li>
 * </ul>
 *

 * As {@link MediaObject} it has three extensions {@link ProgramUpdate}, {@link GroupUpdate} and {@link SegmentUpdate}
 *
 * @param <M>  The {@link MediaObject} extension this is for.
 *
 * @see nl.vpro.domain.media.update
 * @see MediaObject
 */

@SuppressWarnings("LombokSetterMayBeUsed")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlSeeAlso({SegmentUpdate.class, ProgramUpdate.class, GroupUpdate.class})
@Slf4j
@XmlTransient
@ValidEmbargo(groups = WarningValidatorGroup.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ProgramUpdate.class, name = "programUpdate"),
    @JsonSubTypes.Type(value = GroupUpdate.class, name = "groupUpdate"),
    @JsonSubTypes.Type(value = SegmentUpdate.class, name = "segmentUpdate") }
)
@JsonPropertyOrder({ "objectType",
    /* xml attributes */
    "mid",
    "type",
    "avType",
    "workflow",
    "mergedTo",
    "sortDate",
    "creationDate",
    "lastModified",
    "publishStart",
    "publishStop",
    "urn",
    "embeddable",
    /* xml elements */
    "episodeOf",
    "crids",
    "broadcasters",
    "portals",
    "portalRestrictions",
    "geoRestrictions",
    "titles",
    "expandedTitles",
    "descriptions",
    "genres",
    "tags",
    "intentions",
    "expandedIntentions",
    "targetGroups",
    "expandedTargetGroups",
    "geoLocations",
    "expandedGeoLocations",
    "topics",
    "expandedTopics",
    "source",
    "hasSubtitles",
    "countries",
    "languages",
    "isDubbed",
    "availableSubtitles",
    "avAttributes",
    "releaseYear",
    "duration",
    "persons",
    "awards",
    "descendantOf",
    "memberOf",
    "ageRating",
    "contentRatings",
    "email",
    "websites",
    "twitter",
    "teletext",
    "predictionsForXml",
    "locations",
    "relations",
    "images"}
)
@HasTitle(
    groups = PomsValidatorGroup.class,
    message = "{nl.vpro.constraints.hassubormaintitle}",
    type = {TextualType.SUB, TextualType.MAIN}
)
@HasTitle(
    groups = WarningValidatorGroup.class,
    type = {TextualType.MAIN}
)
@HasGenre(
    groups = WarningValidatorGroup.class
)
@AVTypeValidation
public abstract sealed class MediaUpdate<M extends MediaObject>
    implements
    MutableEmbargo<MediaUpdate<M>>,
    TextualObjectUpdate<TitleUpdate, DescriptionUpdate,  MediaUpdate<M>>,
    IntegerVersionSpecific,
    MediaIdentifiable permits ProgramUpdate, GroupUpdate, SegmentUpdate {



    @SuppressWarnings("unchecked")
    public static <M extends MediaObject> @PolyNull MediaUpdate<M> create(@PolyNull M object, OwnerType owner) {
        if (object == null) {
            return null;
        }
        MediaUpdate<M> created;
        if (object instanceof Program) {
            created = (MediaUpdate<M>) ProgramUpdate.create((Program) object, owner);
        } else if (object instanceof Group) {
            created = (MediaUpdate<M>) GroupUpdate.create((Group) object, owner);
        } else {
            created = (MediaUpdate<M>) SegmentUpdate.create((Segment) object, owner);
        }

        return created;
    }

    public static <M extends MediaObject> @PolyNull MediaUpdate<M> create(@PolyNull M object) {
        return create(object, OwnerType.BROADCASTER);
    }

    public static <M extends MediaObject> @PolyNull MediaUpdate<M> create(@PolyNull M object, OwnerType owner, IntegerVersion version) {
        MediaUpdate<M> update = create(object, owner);
        if (update != null) {
            update.setVersion(version);
        }
        return update;
    }

    public static <M extends MediaObject, MB extends MediaBuilder<MB, M>> MediaUpdate<M> createUpdate(MB object, OwnerType ownerType) {
        return create(object.build(), ownerType);
    }

    protected IntegerVersion version;

    protected boolean xmlVersion = true;

    @Valid
    protected MediaObject mediaObjectToValidate;

    protected String mid;

    @Deprecated(since = "7.7")
    @Pattern(regexp = "^urn:vpro:media:(?:group|program|segment):[0-9]+$")
    protected String urn;

    private List<@NotNull @CRID String> crids;

    protected AVType avType;

    protected Boolean embeddable = Boolean.TRUE;

    Boolean isDeleted;

    List<org.meeuw.i18n.regions.@NotNull @PomsValidCountry Region> countries;

    List<@NotNull Locale> languages;

    AVAttributesUpdate avAttributes;

    Instant publishStart;

    Instant publishStop;

    java.time.Duration duration;

    Short releaseYear;

    @NotNull(groups = {WarningValidatorGroup.class })
    @MustDisplay(groups = {PomsValidatorGroup.class})
    AgeRating ageRating;

    List<@NotNull ContentRating> contentRatings;

    List<@NotNull @Email(message = "{nl.vpro.constraints.Email.message}") String> email;

    protected List<@NotNull @Valid ImageUpdate> images;

    /**
     * This represents the editable intentions
     * Only display the intentions for the given owner
     * (more intentions might be present in the metadata).
     */
    protected List<@NotNull IntentionType> intentions;

    protected List<@NotNull TargetGroupType> targetGroups;

    protected List<@NotNull @Valid GeoLocationUpdate> geoLocations;

    private List<@Valid TopicUpdate> topics;

    @Valid
    protected Asset asset;

    private List<
        @NotNull
        @Size(min = 2, max = 4, message = "2 < id < 5")
        @BroadcasterValidation
        @javax.validation.constraints.Pattern(regexp = "[A-Z0-9_-]{2,4}", message = "Broadcaster id ${validatedValue} should match {regexp}")
            String> broadcasters;

    private List<@NotNull String> portals;

    private SortedSet<
        @NotNull
        @Size(min = 1, max = 255)
        String> tags;

    private List<@NotNull @Valid CreditsUpdate> credits;

    private List<@NotNull @Valid PortalRestrictionUpdate> portalRestrictions;

    private SortedSet<@NotNull @Valid GeoRestrictionUpdate> geoRestrictions;

    private SortedSet<@NotNull @Valid TitleUpdate> titles;

    private SortedSet<@NotNull @Valid DescriptionUpdate> descriptions;

    private SortedSet<
        @NotNull
        @Pattern(regexp = "3\\.([0-9]+\\.)*[0-9]+")
        String> genres;

    private SortedSet<@NotNull MemberRefUpdate> memberOf;

    private List<
        @NotNull
        @URI(message = "{nl.vpro.constraints.URI}",
            mustHaveScheme = true,
            minHostParts = 2,
            groups = WarningValidatorGroup.class
        )
        @Size(min = 1, message = "{nl.vpro.constraints.text.Size.min}")
        @Size(max = 255, message = "{nl.vpro.constraints.text.Size.max}")

        String> websites;


    private List<@NotNull @Pattern(message = "{nl.vpro.constraints.twitterRefs.Pattern}", regexp="^[@#][A-Za-z0-9_]{1,139}$") String> twitterrefs;

    private SortedSet<@NotNull @Valid LocationUpdate> locations;

    private SortedSet<@NotNull @Valid RelationUpdate> relations;

    protected SortedSet<@NotNull @Valid PredictionUpdate> predictions;

    @Getter
    @JsonIgnore
    boolean imported = false;

    @XmlTransient
    protected boolean fromXml = false;

    protected MediaUpdate() {
        //
    }

    protected MediaUpdate(IntegerVersion version, M mediaobject, OwnerType ownerType) {
        this.version = version;
        fillFromMedia(mediaobject, ownerType);
        fillFrom(mediaobject, ownerType);
    }

    //Part of the process of creating a MediaUpdate from a MediaObject
    protected final void fillFromMedia(final M mediaobject, final  OwnerType owner) {
        this.mid = mediaobject.getMid();
        this.urn = mediaobject.getUrn();
        this.crids = mediaobject.getCrids();

        this.avType = mediaobject.getAVType();
        this.embeddable = mediaobject.isEmbeddable();
        this.isDeleted = mediaobject.isDeleted();

        this.countries = mediaobject.getCountries();
        this.languages = mediaobject.getLanguages();

        this.avAttributes = AVAttributesUpdate.of(mediaobject.getAvAttributes());

        Embargos.copy(mediaobject, this);

        this.duration = AuthorizedDuration.duration(mediaobject.getDuration());

        this.releaseYear = mediaobject.getReleaseYear();
        this.ageRating = mediaobject.getAgeRating();
        this.contentRatings = mediaobject.getContentRatings();

        this.images = toList(
            mediaobject.getImages(),
            (i) -> i.getOwner() == owner && ! i.isDeleted(),
            ImageUpdate::new,
            false)
        ;
        // asset?

        this.broadcasters = toList(mediaobject.getBroadcasters(), Broadcaster::getId);
        this.portals = toList(mediaobject.getPortals(), Portal::getId);

        this.tags = toSet(mediaobject.getTags(), Tag::getText);
        this.credits = toCreditsUpdate(mediaobject.getCredits());
        if (this.credits.isEmpty()) {
            this.credits = null;
        }
        if (isNotBefore(5, 11)) {
            this.intentions = toUpdateIntentions(mediaobject.getIntentions(), owner);
            this.targetGroups = toUpdateTargetGroups(mediaobject.getTargetGroups(), owner);
        }

        if (isNotBefore(5, 12)) {
            this.portalRestrictions = toList(mediaobject.getPortalRestrictions(), PortalRestrictionUpdate::new);
            this.geoRestrictions = toSet(mediaobject.getGeoRestrictions(), GeoRestrictionUpdate::new);
        }

        if (owner == null || owner == OwnerType.BROADCASTER) {
            TextualObjects.copyToUpdate(mediaobject, this);
        } else {
            TextualObjects.copyToUpdate(mediaobject, this, owner);
        }

        this.genres = toSet(mediaobject.getGenres(), Genre::getTermId);
        this.memberOf = toSet(mediaobject.getMemberOf(), MemberRefUpdate::create);
        this.websites = toList(mediaobject.getWebsites(), Website::get);
        this.twitterrefs= toList(mediaobject.getTwitterRefs(), TwitterRef::get);
        this.email = toList(mediaobject.getEmail(), nl.vpro.domain.media.Email::get);


        this.locations = toSet(mediaobject.getLocations(), (l) -> l.getOwner() == owner && ! l.isDeleted(), LocationUpdate::new);
        this.relations = toSet(mediaobject.getRelations(), RelationUpdate::new);
        this.predictions = toSet(mediaobject.getPredictions(), Prediction::isPlannedAvailability, PredictionUpdate::of);

        if (isNotBefore(5, 12)) {
            this.geoLocations = toGeoLocationUpdates(mediaobject.getGeoLocations(), owner);
            this.topics = toTopicUpdates(mediaobject.getTopics(), owner);
        }
    }

    protected abstract void fillFrom(M mediaObject, OwnerType ownerType);


    /**
     * <p>The POMS version this XML applies too. This is optional, though some features will only be supported if you explicitly specify a version which is big enough (To ensure backward compatibility). If you don't specify it,  there will be no backwards compatibility.
     * </p>
     * <p>
     *    The main point is that the XML may contain elements which' absent means something. E.g. having no {@code <country} would mean that there should be <em>no</em> country associated with the object.  This was introduced in poms 5.0. If you specify a version before 5.0, all country information will be ignored, and left was it was.
     * </p>
     */
    @Override
    @XmlTransient
    public IntegerVersion getVersion() {
        return version;
    }
    @Override
    public void setVersion(IntegerVersion version) {
        this.version = version;
    }

    @XmlAttribute(name = "version")
    protected String getVersionAttribute() {
        if (xmlVersion) {
            IntegerVersion version = getVersion();
            return version == null ? null : version.toString();
        } else {
            return null;
        }
    }
    protected void setVersionAttribute(String version) {
        setVersion(Version.parseIntegers(version));
    }

    @JsonIgnore
    public boolean isValid() {
        return violations().isEmpty();
    }

    public Set<? extends ConstraintViolation<MediaUpdate<? extends M>>> warningViolations() {
        return violations(WarningValidatorGroup.class);
    }
    public Set<? extends ConstraintViolation<MediaUpdate<? extends M>>> violations(Class<?>... groups) {
        if (groups.length == 0) {
            groups = Validation.DEFAULT_GROUPS;
        }
        mediaObjectToValidate = null;
        Set<? extends ConstraintViolation<MediaUpdate<? extends M>>> result = Validation.validate(this, groups);
        if (result.isEmpty()) {
            mediaObjectToValidate = fetch(OwnerType.BROADCASTER);
            try {
                result = Validation.validate(this, groups);
                if (result.isEmpty()) {
                    log.debug("validates");
                }
                return result;
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                return Collections.emptySet();
            }
        }
        return result;
    }

    public String violationMessage() {
        Set<? extends ConstraintViolation<? extends MediaUpdate<? extends M>>> violations = violations();
        return violationMessage(violations);
    }

    public static <N extends MediaObject> String violationMessage(Set<? extends ConstraintViolation<? extends MediaUpdate<? extends N>>> violations) {
        if(violations.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder("List of constraint violations: [\n");
        for(ConstraintViolation<?> violation : violations) {
            sb.append('\t')
                .append(violation.toString())
                .append('\n');
        }
        sb.append(']');
        return sb.toString();
    }

    protected abstract M newMedia();

    private M fetchOwnerless() {
        M media = newMedia();
        media.setUrn(urn);
        media.setMid(mid);
        media.setCreationInstant(null); //   not supported by update format. will be set by persistence layer
        media.setCrids(crids);
        media.setAVType(avType);
        media.setEmbeddable(embeddable == null || embeddable);
        media.setCountries(countries);
        media.setLanguages(languages);

        media.setAvAttributes(AVAttributesUpdate.toAvAttributes(avAttributes));

        Embargos.copy(this, media);

        try {
            media.setDuration(duration);
        } catch(ModificationException mfe) {
            log.error(mfe.getMessage());
        }
        media.setReleaseYear(releaseYear);
        media.setAgeRating(ageRating);
        media.setContentRatings(contentRatings);

        // images have owner

        media.setBroadcasters(toList(broadcasters, Broadcaster::new));

        media.setPortals(toList(portals, Portal::new));
        media.setTags(toSet(tags, Tag::new));
        media.setCredits(toList(credits, CreditsUpdate::toCredits, true));
        media.setPortalRestrictions(toList(portalRestrictions, PortalRestrictionUpdate::toPortalRestriction));
        media.setGeoRestrictions(toSet(geoRestrictions,
            g -> g.getRegion() != Region.UNIVERSE,
            GeoRestrictionUpdate::toGeoRestriction
        ));

        // titles have owner,

        media.setGenres(toSet(genres, Genre::new));
        media.setMemberOf(toSet(memberOf, this::toMemberRef));

        // locations are owned

        media.setRelations(toSet(relations, RelationUpdate::toRelation));

        // scheduleevents are owned

        media.setPredictions(toSet(predictions, PredictionUpdate::toPrediction));

        if (isDeleted == Boolean.TRUE) {
            MediaObjects.markForDeletionIfNeeded(media, "");
        } else {
            MediaObjects.markForRepublication(media, "");
        }
        return media;
    }

    /**
     * Convert this MediaUpdate object to a MediaObject
     * Clone all the fields of MediaUpdate into a new MediaObject
     */
    public M fetch(OwnerType owner) {
        M returnObject = fetchOwnerless();
        TextualObjects.copy(this, returnObject, owner);
        for (Location l : toSet(locations, l -> l.toLocation(owner))) {
            returnObject.addLocation(l);
        }
        Predicate<Image> imageFilter = isImported() ? (i) -> i.getImageUri() != null : (i) -> true;
        returnObject.setImages(toList(images, i -> i.toImage(owner)).stream()
                .filter(imageFilter)
                .collect(Collectors.toList()));

        returnObject.setWebsites(toList(websites, (w) -> new Website(w, owner)));
        returnObject.setTwitterRefs(toList(twitterrefs, (t) -> new TwitterRef(t, owner)));
        returnObject.setEmail(toList(email, (e) -> new nl.vpro.domain.media.Email(e, owner)));
        if(intentions != null) {
            MediaObjectOwnableLists.addOrUpdateOwnableList(returnObject, returnObject.getIntentions(), toIntentions(intentions, owner));
        } else {
            MediaObjectOwnableLists.remove(returnObject.getIntentions(), owner);
        }
        if(targetGroups != null) {
            MediaObjectOwnableLists.addOrUpdateOwnableList(returnObject, returnObject.getTargetGroups(), toTargetGroups(targetGroups, owner));
        } else {
            MediaObjectOwnableLists.remove(returnObject.getTargetGroups(), owner);
        }
        if (isNotBefore(5, 12)) {
            if (geoLocations != null) {
                MediaObjectOwnableLists.addOrUpdateOwnableList(returnObject, returnObject.getGeoLocations(), toGeoLocations(geoLocations, owner));
            } else {
                MediaObjectOwnableLists.remove(returnObject.getGeoLocations(), owner);

            }
            if (topics != null ) {
                MediaObjectOwnableLists.addOrUpdateOwnableList(returnObject, returnObject.getTopics(), toTopics(topics, owner));
            } else {
                MediaObjectOwnableLists.remove(returnObject.getTopics(), owner);
            }
        }

        return returnObject;
    }

    /**
     * From a SortedSet<Intentions> to a List<IntentionType>
     * Returning only the values for the given owner.
     * We decided to return an empty list if owner differ rather than raise an
     * exception (this code will usually be executed behind a queue)
     * <p>
     * Given a null it will return null to keep the distinction between systems
     * that are aware of this field (we use empty list to delete)
     */
    private List<IntentionType> toUpdateIntentions(SortedSet<Intentions> intentions, OwnerType owner){
        if (intentions == null) {
            return null;
        }
        return OwnableLists.filterByOwnerOrFirst(intentions, owner)
            .map(Intentions::getValues)
            .map(l -> l.stream().map(Intention::getValue).collect(Collectors.toList()))
            .orElse(new ArrayList<>());
    }

    private Intentions toIntentions(List<IntentionType> intentionValues, OwnerType owner){
        if (intentionValues == null) {
            return null;
        }
        return Intentions.builder()
                .owner(owner)
                .values(intentionValues)
                .build();
    }

    /**
     * From a SortedSet<TargetGroups> to a List<TargetGroupType>
     * Returning only the values for the given owner.
     * We decided to return an empty list if owner differ rather than raise an
     * exception (this code will usually be executed behind a queue)
     * <p>
     * Given a null it will return null to keep the distinction between systems
     * that are aware of this field (we use empty list to delete)
     */
    private List<TargetGroupType> toUpdateTargetGroups(SortedSet<TargetGroups> targetGroups, OwnerType owner){
        if (targetGroups == null){
            return null;
        }
        return OwnableLists
            .filterByOwnerOrFirst(targetGroups, owner)
            .map(TargetGroups::getValues)
            .map(l -> l.stream().map(TargetGroup::getValue).collect(Collectors.toList()))
            .orElse(new ArrayList<>());
    }

    private TargetGroups toTargetGroups(@Nullable List<TargetGroupType> targetGroupValues, @NonNull  OwnerType owner){
        if (targetGroupValues == null){
            return null;
        }
        return TargetGroups.builder()
                .owner(owner)
                .values(targetGroupValues)
                .build();
    }

    private List<GeoLocationUpdate> toGeoLocationUpdates(SortedSet<GeoLocations> geoLocationsSet, OwnerType owner) {

        if (geoLocationsSet == null) {
            return null;
        }

        return OwnableLists.filterByOwner(geoLocationsSet, owner)
            .map(GeoLocations::getValues)
            .map(l -> l.stream()
                .map(GeoLocationUpdate::new)
                .collect(Collectors.toList()))
            .orElse(new ArrayList<>());
    }

    private GeoLocations toGeoLocations(List<GeoLocationUpdate> geoLocationUpdates, OwnerType owner) {

        if (geoLocationUpdates == null){
            return null;
        }

        List<GeoLocation> geoLocations = geoLocationUpdates
            .stream()
            .map(GeoLocationUpdate::toGeoLocation)
            .collect(Collectors.toList());

        return GeoLocations.builder()
            .owner(owner)
            .values(geoLocations)
            .build();
    }

    private List<TopicUpdate> toTopicUpdates(SortedSet<Topics> topicsSet, OwnerType owner) {

        if (topicsSet == null) {
            return null;
        }

        return OwnableLists.filterByOwner(topicsSet, owner)
            .map(Topics::getValues)
            .map(l -> l.stream()
                .map(TopicUpdate::new)
                .collect(Collectors.toList()))
            .orElse(new ArrayList<>());
    }

    private Topics toTopics(List<TopicUpdate> topicUpdates, OwnerType owner) {

        if (topicUpdates == null) {
            return null;
        }

        List<Topic> topics = topicUpdates
            .stream()
            .map(TopicUpdate::toTopic)
            .collect(Collectors.toList());

        return Topics.builder()
            .owner(owner)
            .values(topics)
            .build();
    }

    private List<CreditsUpdate> toCreditsUpdate(List<Credits> credits) {
        if (credits == null) {
            return null;
        }

        List<CreditsUpdate> creditsUpdates = new ArrayList<>();
        for (Credits credit: credits) {
            if (credit instanceof Person p) {
                if (p.getGtaaUri() != null && isBefore(5, 12)) {
                    continue;
                }
                creditsUpdates.add(new PersonUpdate(p));
            } else {
                if (isNotBefore(5, 12)) {
                    creditsUpdates.add(new NameUpdate((Name) credit));
                }
            }
        }

        return creditsUpdates;
    }

    public M fetch() {
        return fetch(OwnerType.BROADCASTER);
    }

    protected <T, U> List<T> toList(List<U> list, Predicate<U> filter, Function<U, T> mapper, boolean nullToNull) {
        if (list == null) {
            if (nullToNull) {
                return null;
            }
            list = new ArrayList<>();
        }
        return list
            .stream()
            .filter(filter)
            .map(mapper)
            .collect(Collectors.toList());
    }

    protected <T, U> List<T> toList(List<U> list, Function<U, T> mapper, boolean nullToNull) {
        return toList(list, (u) -> true, mapper, nullToNull);
    }

    protected <T, U> List<T> toList(List<U> list, Function<U, T> mapper) {
        return toList(list, (u) -> true, mapper, false);
    }

    protected <T, U extends Comparable<U>> TreeSet<T> toSet(Set<U> list, Predicate<U> filter, Function<U, T> mapper) {
        if (list == null) {
            list = new TreeSet<>();
        }
        return list
            .stream()
            .filter(filter)
            .map(mapper)
            .collect(Collectors.toCollection(TreeSet::new));
    }

    protected <T, U extends Comparable<U>> TreeSet<T> toSet(Set<U> list, Function<U, T> mapper) {
        return toSet(list, (u) -> true, mapper);
    }

    /**
     *
     * @since 1.5
     */
    @XmlAttribute
    @Size.List({@Size(max = 255), @Size(min = 4)})
    @Pattern(regexp = "^[ .a-zA-Z0-9_-]+$", flags = {Pattern.Flag.CASE_INSENSITIVE}, message = "{nl.vpro.constraints.mid}")
    @Override
    public final String getMid() {
        return mid;
    }

    /**
     * @since 1.8
     */
    public void setMid(String mid) {
        this.mid = mid;
    }

    public abstract SubMediaType getType();

    /**
     * @since 5.6
     */
    @Override
    @JsonIgnore
    public final MediaType getMediaType() {
        SubMediaType subMediaType = getType();
        return subMediaType == null ? null : subMediaType.getMediaType();
    }

    @XmlAttribute(name = "deleted")
    protected Boolean getDeletedAttribute() {
        return isDeleted() ? true : null;
    }
    protected void setDeletedAttribute(Boolean deleted) {
        isDeleted = deleted != null ? (deleted ? true : null) : null;
    }

    @XmlTransient
    public boolean isDeleted() {
        return isDeleted != null && isDeleted;
    }
    public void setDeleted(boolean isDeleted) {
        this.isDeleted = !isDeleted ? null : true;
    }

    @XmlAttribute
    @Deprecated
    public String getUrn() {
        return urn;
    }

    /**
     * @deprecated Refer to existing media by {@link #setMid(String) mid} instead.
     */
    @Deprecated(since = "7.7")
    public void setUrn(String s) {
        this.urn = s;
    }

    @XmlTransient
    @Override
    public Long getId() {
        String urn = getUrn();
        if (urn == null) {
            return null;
        }
        return Long.valueOf(urn.substring(getUrnPrefix().length()));
    }

    void setId(Long id) {
        setUrn(getUrnPrefix() + id);
    }

    protected abstract String getUrnPrefix();

    @XmlAttribute(name = "avType")
    @NotNull
    public AVType getAVType() {
        return avType;
    }

    public void setAVType(AVType avType) {
        this.avType = avType;
    }

    @XmlAttribute
    public Boolean getEmbeddable() {
        return embeddable;
    }

    public void setEmbeddable(Boolean isEmbeddable) {
        this.embeddable = isEmbeddable;
    }

    @Override
    @XmlAttribute(name = "publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @NonNull
    @Override
    public MediaUpdate<M> setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return this;
    }

    @Override
    @XmlAttribute(name = "publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @NonNull
    @Override
    public MediaUpdate<M> setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return this;
    }

    @XmlElement(name = "crid")
    @StringList(pattern = "(?i)crid://.*/.*", maxLength = 255)
    @Override
    @NonNull
    public List<String> getCrids() {
        if (crids == null) {
            crids = new ArrayList<>();
        }
        return crids;
    }

    public void setCrids(List<String> crids) {
        this.crids = crids;
    }

    @XmlElement(name = "broadcaster", required = true)
    @Size(min = 1, groups = WarningValidatorGroup.class)
    @NonNull
    public List<String> getBroadcasters() {
        if (broadcasters == null) {
            broadcasters = new ArrayList<>();
        }
        return broadcasters;
    }

    public void setBroadcasters(List<String> broadcasters) {
        this.broadcasters = broadcasters;
    }

    @XmlTransient
    public void setBroadcasters(String... broadcasters) {
        this.broadcasters = Arrays.asList(broadcasters);
    }

    @XmlElement(name = "portal", required = false)
    @NonNull
    public List<String> getPortals() {
        if (portals == null) {
            portals = new ArrayList<>();
        }
        return portals;
    }

    public void setPortals(List<String> portals) {
        this.portals = portals;
    }

    @XmlTransient
    public void setPortals(String... portals) {
        this.portals = Arrays.asList(portals);
    }

    @XmlElement(name = "exclusive")
    @Valid
    public List<PortalRestrictionUpdate> getPortalRestrictions() {
        if (portalRestrictions == null) {
            portalRestrictions = new ArrayList<>();
        }
        return portalRestrictions;
    }

    public void setPortalRestrictions(List<PortalRestrictionUpdate> restrictions) {
        this.portalRestrictions = restrictions;
    }

    @XmlTransient
    public void setPortalRestrictions(String... restrictions) {
        List<PortalRestrictionUpdate> updates = getPortalRestrictions();
        Stream.of(restrictions).forEach(r -> updates.add(PortalRestrictionUpdate.of(r)));
    }

    @XmlElement(name = "region")
    @Valid
    @NonNull
    public SortedSet<GeoRestrictionUpdate> getGeoRestrictions() {
         if (geoRestrictions == null) {
             geoRestrictions = new TreeSet<>();
         }
        return geoRestrictions;
    }

    public void setGeoRestrictions(SortedSet<GeoRestrictionUpdate> restrictions) {
        this.geoRestrictions = restrictions;
    }

    @Override
    @XmlElement(name = "title", required = true)
    @Valid
    @NotNull
    @NonNull
    @Size(min = 1, groups = RedundantValidatorGroup.class)
    public SortedSet<TitleUpdate> getTitles() {
        if (titles == null) {
            titles = new TreeSet<>();
        }
        return titles;
    }

    @Override
    public void setTitles(SortedSet<TitleUpdate> titles) {
        this.titles = titles;
    }
    @XmlTransient
    public void setTitles(TitleUpdate... titles) {
        setTitles(new TreeSet<>(Arrays.asList(titles)));
    }

    @Override
    @XmlElement(name = "description")
    @Valid
    @NonNull
    public SortedSet<DescriptionUpdate> getDescriptions() {
        if (descriptions == null) {
            descriptions = new TreeSet<>();
         }
        return descriptions;
    }

    @Override
    public void setDescriptions(SortedSet<DescriptionUpdate> descriptions) {
        this.descriptions = descriptions;
    }
    @XmlTransient
    public void setDescriptions(DescriptionUpdate... descriptions) {
        this.descriptions = new TreeSet<>(Arrays.asList(descriptions));
    }

    @Override
    @JsonIgnore
    public BiFunction<String, TextualType, TitleUpdate> getTitleCreator() {
        return TitleUpdate::new;
    }

    @Override
    @JsonIgnore
    public BiFunction<String, TextualType, DescriptionUpdate> getDescriptionCreator() {
        return DescriptionUpdate::new;
    }

    @XmlElement(name = "tag")
    public SortedSet<String> getTags() {
        if (tags == null) {
            tags = new TreeSet<>();
        }
        return tags;
    }

    public void setTags(SortedSet<String> tags) {
        this.tags = tags;
    }

    @XmlTransient
    public void setTags(String... tags) {
        setTags(new TreeSet<>(Arrays.asList(tags)));
    }

    @XmlElement(name = "country")
    @XmlJavaTypeAdapter(Code.class)
    public List<org.meeuw.i18n.regions.Region> getCountries() {
         if (countries == null) {
            countries = new ArrayList<>();
         }
        return countries;
    }

    public void setCountries(List<org.meeuw.i18n.regions.Region> countries) {
        this.countries = countries;
    }

    @XmlElement(name = "language")
    @XmlJavaTypeAdapter(value = LocaleAdapter.class)
    public List<Locale> getLanguages() {
         if (languages == null) {
            languages = new ArrayList<>();
         }
        return languages;
    }
    public void setLanguages(List<Locale> languages) {
        this.languages = languages;
    }

    @XmlElement(name = "genre")
    @StringList(pattern = "3\\.([0-9]+\\.)*[0-9]+", maxLength = 255)
    @NonNull
    public SortedSet<String> getGenres() {
        if (genres == null) {
            genres = new TreeSet<>();
        }
        return genres;
    }

    public void setGenres(SortedSet<String> genres) {
        this.genres = genres;
    }

    @XmlTransient
    public void setGenres(String... genres) {
        setGenres(new TreeSet<>(Arrays.asList(genres)));
    }

    @XmlElementWrapper(name = "intentions")
    @XmlElement(name = "intention")
    @NonNull
    public List<IntentionType> getIntentions() {
        return intentions;
    }

    public void setIntentions(List<IntentionType> intentions) {
        this.intentions = intentions;
    }

    @XmlElementWrapper(name = "targetGroups")
    @XmlElement(name = "targetGroup")
    @NonNull
    public List<TargetGroupType> getTargetGroups() {
        return targetGroups;
    }

    public void setTargetGroups(List<TargetGroupType> targetGroups) {
        this.targetGroups = targetGroups;
    }

    @XmlElement(name = "avAttributes")
    public AVAttributesUpdate getAvAttributes() {
        return avAttributes;
    }

    public void setAvAttributes(AVAttributesUpdate avAttributes) {
        this.avAttributes = avAttributes;
    }

    @XmlElement
    @XmlJavaTypeAdapter(DurationXmlAdapter.class)
    public java.time.Duration getDuration() {
        return duration;
    }

    public void setDuration(java.time.Duration duration) {
        this.duration = duration;
    }

    @XmlElement
    public Short getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Short releaseYear) {
        this.releaseYear = releaseYear;
    }

    @XmlElementWrapper(name = "credits")
    @XmlElements({
        @XmlElement(name = "person", type = PersonUpdate.class),
        @XmlElement(name = "name", type = NameUpdate.class)
    })
    @Valid
    @NonNull
    public List<CreditsUpdate> getCredits() {
        if (credits == null) {
            credits = new ArrayList<>();
        }
        return credits;
    }

    public void setCredits(List<CreditsUpdate> credits) {
        this.credits = credits;
    }

    @XmlTransient
    public void setCredits(CreditsUpdate... credits){
        setCredits(new ArrayList<>(Arrays.asList(credits)));
    }

    @XmlElement
    @NonNull
    public SortedSet<MemberRefUpdate> getMemberOf() {
        if (memberOf == null) {
            memberOf = new TreeSet<>();
        }
        return memberOf;
    }

    protected MemberRef toMemberRef(MemberRefUpdate m) {
        MemberRef ref = new MemberRef();
        //ref.setMember(fetch(OwnerType.BROADCASTER));
        ref.setMediaRef(m.getMediaRef());
        ref.setNumber(m.getPosition());
        ref.setHighlighted(m.isHighlighted());
        ref.setAdded(null);
        return ref;
    }

    public void setMemberOf(SortedSet<MemberRefUpdate> memberOf) throws CircularReferenceException {
       this.memberOf = memberOf;
    }


    @XmlElement
    public AgeRating getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(AgeRating ageRating) {
        this.ageRating = ageRating;
    }

    @XmlElement(name = "contentRating")
    @NonNull
    public List<ContentRating> getContentRatings() {
        if (contentRatings == null) {
            contentRatings = new ArrayList<>();
        }
        return contentRatings;
    }

    public void setContentRatings(List<ContentRating> contentRatings) {
        this.contentRatings = contentRatings;
    }

    @XmlElement
    @NonNull
    public List<String> getEmail() {
        if (email == null) {
             email = new ArrayList<>();
        }
        return email;
    }

    public void setEmail(List<String> emails) {
        this.email = emails;
    }

    @XmlTransient
    public void setEmail(String... emails) {
        setEmail(new ArrayList<>(Arrays.asList(emails)));
    }

    @XmlElement(name = "website")
    @NonNull
    public List<String> getWebsites() {
        if (websites == null) {
             websites = new ArrayList<>();
        }
        return websites;
    }

    public void setWebsites(List<String> websites) {
        this.websites = websites;
    }

    @XmlTransient
    public void setWebsites(String... websites) {
        setWebsites(new ArrayList<>(Arrays.asList(websites)));
    }

    public void setWebsiteObjects(List<Website> websites) {
        this.websites = websites.stream()
            .map(Website::getUrl)
            .collect(Collectors.toList());
    }


    @XmlElement(name = "twitterref")
    @NonNull
    public List<String> getTwitterrefs() {
        if (twitterrefs == null) {
             twitterrefs = new ArrayList<>();
        }
        return twitterrefs;
    }

    @XmlTransient
    public void setTwitterRefs(List<String> twitterRefs) {
        this.twitterrefs = twitterRefs;
    }

    /**
     * @since 5.6
     */
    @XmlElement(name = "prediction")
    @Valid
    @NonNull
    public SortedSet<PredictionUpdate> getPredictions() {
        if (predictions == null) {
            predictions = new TreeSet<>();
        }
        return predictions;
    }

    /**
     * @since 5.6
     */
    public void setPredictions(SortedSet<PredictionUpdate> predictions) {
        this.predictions = predictions;
    }

    @XmlElementWrapper(name = "locations")
    @XmlElement(name = "location")
    @Valid
    @NonNull
    public SortedSet<LocationUpdate> getLocations() {
        if (locations == null) {
            locations = new TreeSet<>();
        }
        return locations;
    }

    public void setLocations(SortedSet<LocationUpdate> locations) {
        this.locations = locations;
    }

    @XmlTransient
    public void setLocations(LocationUpdate... locations) {
        setLocations(new TreeSet<>(Arrays.asList(locations)));
    }

    @XmlElement(name = "relation")
    @NonNull
    public SortedSet<RelationUpdate> getRelations() {
        if (relations == null) {
            relations = new TreeSet<>();
        }
        return relations;
    }

    public void setRelations(SortedSet<RelationUpdate> relations) {
        this.relations = relations;
    }

    @XmlElementWrapper(name = "images")
    @XmlElement(name = "image")
    @Valid
    @NonNull
    public List<ImageUpdate> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        return images;
    }

    public void setImages(List<ImageUpdate> images) {
        // Leave builder.images to the fetch(ImageImporter) method
        this.images = images;
    }

    @XmlTransient
    public void setImages(ImageUpdate... images) {
        setImages(new ArrayList<>());
        this.images.addAll(Arrays.asList(images));
    }

    /**
     * Get asset containing the location source to be encoded.
     *
     * @return asset or null when unavailable
     * @since 2.1
     */
    @XmlElement(name = "asset")
    @Nullable
    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    @XmlElementWrapper(name = "geoLocations")
    @XmlElement(name = "geoLocation")
    @Valid
    @NonNull
    public List<GeoLocationUpdate> getGeoLocations() {
        return geoLocations;
    }

    public void setGeoLocations(List<GeoLocationUpdate> geoLocationUpdates) {
        this.geoLocations = geoLocationUpdates;
    }

    @XmlTransient
    public void setGeoLocations(GeoLocationUpdate... geoLocationUpdates) {
        setGeoLocations(Arrays.asList(geoLocationUpdates));
    }

    @XmlElementWrapper(name = "topics")
    @XmlElement(name = "topic")
    @Valid
    @NonNull
    public List<TopicUpdate> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicUpdate> topicUpdates) {
        this.topics = topicUpdates;
    }

    @XmlTransient
    public void setTopics(TopicUpdate... topicUpdates) {
        setTopics(Arrays.asList(topicUpdates));
    }

    @Override
    public String toString() {
        return (isDeleted == Boolean.TRUE ? "DELETED:" : "") + getClass().getSimpleName() + "[" + getType() + ":" + (mid == null ? "<no mid>" : mid) + ":" + Optional.ofNullable(getMainTitle()).orElse("<no main title>") + "]";
    }

    void afterUnmarshal(Unmarshaller u, Object parent) {
        this.fromXml = true;
        if (parent != null) {
            if (parent instanceof IntegerVersionSpecific) {
                version = ((IntegerVersionSpecific) parent).getVersion();
                xmlVersion = false;
            }
        }
    }

    void beforeMarshal(Marshaller marshaller) {
        log.trace("Before");
    }

    protected boolean isNotBefore(Integer... intVersion) {
        return version == null || version.isNotBefore(intVersion);
    }

    protected boolean isBefore(Integer... intVersion) {
        return version != null && version.isBefore(intVersion);
    }

}
