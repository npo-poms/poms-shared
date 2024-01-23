/*
 * Copyright (C) 2008 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.OrderBy;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.meeuw.functional.TriFunction;
import org.meeuw.i18n.countries.Country;
import org.meeuw.i18n.regions.RegionService;
import org.meeuw.i18n.regions.validation.Language;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.annotations.Beta;
import com.google.common.collect.Collections2;
import com.neovisionaries.i18n.CountryCode;

import nl.vpro.domain.*;
import nl.vpro.domain.bind.CollectionOfPublishable;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.bind.*;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.user.*;
import nl.vpro.domain.validation.NoDuplicateOwner;
import nl.vpro.i18n.Locales;
import nl.vpro.i18n.validation.MustDisplay;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.jackson2.Views;
import nl.vpro.logging.simple.Level;
import nl.vpro.util.*;
import nl.vpro.validation.*;
import nl.vpro.xml.bind.FalseToNullAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static javax.persistence.CascadeType.*;
import static nl.vpro.domain.Changeables.instant;
import static nl.vpro.domain.TextualObjects.sorted;
import static nl.vpro.domain.media.CollectionUtils.*;
import static nl.vpro.domain.media.MediaObjectFilters.*;
import static nl.vpro.domain.media.support.Workflow.PUBLICATIONS;

/**
 * Base objects for programs, groups and segments.
 * <p>
 * Media objects are the most central objects of POMS. A media object  represents one document of meta-information, with all titles, descriptions, tags and
 * all other fields that are associated with 'media' in general.
 * <p>
 * Also {@link Group}s are an extension, which implies e.g. that things like a {@link GroupType#PLAYLIST} may themselves have similar metadata, though they
 * basically represent groups of other {@link MediaObject}s, and are not themselves associated with actual audio or video.
 * <p>
 * But also {@link Program}s themselves can function as a group and therefor have 'members' (e.g. such a member may be a {@link ProgramType#PROMO}).
 * <p>
 * {@link Segment}s are a special kind of members of only {@link Program}s, and represent a 'segment' from a larger 'program' only.
 * <p>
 * The purpose of a mediaobject is
 * <ol>
 *     <li>Be a full representation of meta data related to one entity</li>
 *     <li>Be also its database representation. Therefore e.g. {@link javax.persistence} annotations are present. These are optional, and are probably only relevant in the realm of 'poms backend application'</li>
 *     <li>Be also the XML/Json representation of most of this data. For a few fields it doesn't make sense to be exposed in that way, like for example the {@link Editor}s of  {@link Accountable}. For this the object is annotated with some annotation from {@link javax.xml} and {@link com.fasterxml.jackson}</li>
 *     <li>The JSON version is basically also the representation used in Elasticsearch (only with {@link Views.Publisher} enabled)</li>
 * </ol>
 *
 *
 * @author roekoe
 */
@SuppressWarnings("WSReferenceInspection")

// jpa
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable

// binding
//   binding xml
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ Program.class, Group.class, Segment.class })
@XmlType(name = "baseMediaType",
    propOrder = {
        "crids",
        "broadcasters",
        "portals",
        "portalRestrictions",
        "geoRestrictions",
        "titles",
        "descriptions",
        "genres",
        "tags",
        "intentions",
        "targetGroups",
        "geoLocations",
        "topics",
        "source",
        "countries",
        "languages",
        "isDubbed",
        "availableSubtitles",
        "avAttributes",
        "releaseYear",
        "duration",
        "credits",
        "awards",
        "descendantOf",
        "memberOf",
        "ageRating",
        "contentRatings",
        "email",
        "websites",
        "twitterRefs",
        "teletext",
        "predictionsForXml",
        "_Locations",
        "_Relations",
        "images"
    })
//   binding json
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
    "images"

})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = Program.class, name = "program"),
    @JsonSubTypes.Type(value = Group.class, name = "group"),
    @JsonSubTypes.Type(value = Segment.class, name = "segment") }
)

// hibernate filtering
@FilterDef(name = PUBLICATION_FILTER)
@Filter(name = PUBLICATION_FILTER, condition = PUBLICATION_FILTER_CONDITION_PUBLISHABLES)

@FilterDef(name = EMBARGO_FILTER, parameters = {@ParamDef(name = PARAMETER_BROADCASTERS, type = "string") })
@Filter(name = EMBARGO_FILTER, condition = EMBARGO_FILTER_CONDITION)

@FilterDef(name = DELETED_FILTER)
@Filter(name = DELETED_FILTER, condition = DELETED_FILTER_CONDITION)

@FilterDef(name = ORGANIZATION_FILTER, parameters = { @ParamDef(name = PARAMETER_ORGANIZATIONS, type = "string") })
@Filter(name = ORGANIZATION_FILTER, condition = ORGANIZATION_FILTER_CONDITION)

// logging. We stick to slf4j in the domain classes for now. Most projects use @Log4j2.
@Slf4j

// validation
@HasGenre(groups = WarningValidatorGroup.class)
@HasTitle(
    groups = PomsValidatorGroup.class,
    message = "{nl.vpro.constraints.hassubormaintitle}",
    type = {TextualType.SUB, TextualType.MAIN}
)
@HasTitle(
    groups = WarningValidatorGroup.class,
    type = {TextualType.MAIN}
)
@AVTypeValidation
public abstract class MediaObject extends PublishableObject<MediaObject>
    implements Media<MediaObject> {
    // permits Program, Group, Segment, MediaObject$HibernateBasicProxy {
    //hibernate will make a HibernateBasicProxy, which is not permitted (nor available)


    @Serial
    private static final long serialVersionUID = -9095662256792069374L;

    @Column(name = "mid", nullable = false, unique = true)
    @Size(max = 255, min = 4)
    @Pattern(
        regexp = "^[a-zA-Z0-9][ .a-zA-Z0-9_-]*$",
        flags = {
            Pattern.Flag.CASE_INSENSITIVE }, message = "{nl.vpro.constraints.mid}")
    @NotNull(groups = PrePersistValidatorGroup.class)
    @MonotonicNonNull
    protected String mid;

    //@Version
    @Transient // Remove for MSE-3753
    @Getter
    protected Integer version;

    @Setter
    @ElementCollection
    @Column(name = "crids", nullable = false, unique = true) // TODO, rename to 'crid'.
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    // Improvement: cache configuration can be put in a hibernate-config.xml. See
    // https://docs.jboss.org/hibernate/orm/4.0/devguide/en-US/html/ch06.html
    @StringList(maxLength = 255)
    protected List<@NotNull @CRID String> crids;

    @Setter
    @ManyToMany
    @OrderColumn(name = "list_index",
        nullable = false)
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Size(min = 0, message = "{nl.vpro.constraints.Size.min}") // komt soms voor bij imports.
    protected List<@NotNull Broadcaster> broadcasters;

    @ManyToMany
    @OrderColumn(name = "list_index", nullable = false)
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Nullable
    protected List<@NotNull Portal> portals;

    @ManyToMany
    @OrderColumn(name = "list_index", nullable = false)
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<@NotNull  ThirdParty> thirdParties;

    @Setter
    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition = PUBLICATION_FILTER_CONDITION_RESTRICTIONS)
    @PublicationFilter
    @Valid
    protected List<@NotNull PortalRestriction> portalRestrictions;

    @Setter
    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "mediaobject_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition = PUBLICATION_FILTER_CONDITION_RESTRICTIONS)
    @PublicationFilter
    @Valid
    protected Set<@NotNull GeoRestriction> geoRestrictions;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = {ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    // @NotNull(message = "titles: {nl.vpro.constraints.NotNull}") // Somewhy
    // hibernates on merge first merges an object without titles.
    @Valid
    @Size(min = 1, message = "{nl.vpro.constraints.collection.Size.min}", groups=RedundantValidatorGroup.class)
    protected Set<@NotNull Title> titles;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade=ALL)
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected Set<@NotNull Description> descriptions;

    @ManyToMany(cascade = {DETACH, MERGE, PERSIST, REFRESH}) // todo since the genre table only contains 1 field, namely the id, which is already in the mediaobject_genre table, this is odd.
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected Set<@NotNull Genre> genres;

    @ManyToMany(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    @JoinTable(foreignKey = @ForeignKey(name = "fk_mediaobject_tag__mediaobject"), inverseForeignKey = @ForeignKey(name = "fk_mediaobject_tag__tag"))
    protected Set<@NotNull Tag> tags;


    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "parent_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    @NoDuplicateOwner
    @XmlElement(name = "intentions")
    @JsonProperty("intentions")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @SortNatural
    protected SortedSet<@NotNull Intentions> intentions;

    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "parent_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    @NoDuplicateOwner
    @XmlElement
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @SortNatural
    protected SortedSet<@NotNull TargetGroups> targetGroups;

    @Setter
    protected String source;

    @ElementCollection
    @Column(length = 10)
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Schema(implementation = String.class, type = "string")
    protected List<
        // valid are countries (further validated by @ValidCountry), and a list of codes.
        org.meeuw.i18n.regions.
        @PomsValidCountry Region> countries;


    @ElementCollection
    @Column(length = 10)
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<
        @PomsValidCountry(groups = WarningValidatorGroup.class)
        @Language(mayContainCountry = true, groups = WarningValidatorGroup.class)
        Locale> languages;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "avType: {nl.vpro.constraints.NotNull}")
    @Nullable
    protected AVType avType = null;

    @Setter
    @OneToOne(orphanRemoval = true, cascade = {ALL})
    protected AVAttributes avAttributes;

    @Setter
    @Column(name = "releaseDate")
    protected Short releaseYear;

    @Embedded
    @Valid
    @Nullable
    protected AuthorizedDuration duration;

    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "mediaobject_id")
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<@NotNull @Valid Credits> credits;

    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "parent_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NoDuplicateOwner
    @XmlElement(name = "geoLocations")
    @JsonProperty("geoLocations")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @SortNatural
    protected SortedSet<@NotNull @Valid GeoLocations> geoLocations;

    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "parent_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @NoDuplicateOwner
    @XmlElement(name = "topics")
    @JsonProperty("topics")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @SortNatural
    protected SortedSet<@NotNull @Valid Topics> topics;

    @ElementCollection
    @JoinTable(name = "mediaobject_awards")
    @OrderColumn(name = "list_index", nullable = false)
    @Column(name = "awards")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<@NotNull String> awards;

    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinTable(name = "mediaobject_memberof", inverseJoinColumns = @JoinColumn(name = "id"))
    //@SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)


    // Before hibernate 5.2 we used Filter rather than FilterJoinTable.
    // It doesn't really make much sense.

    @PublicationFilter
    @Filter(name = MR_DELETED_FILTER, condition = MR_DELETED_FILTER_CONDITION)
    @Filter(name = MR_EMBARGO_FILTER, condition = MR_EMBARGO_FILTER_CONDITION)
    @Filter(name = MR_PUBLICATION_FILTER, condition = MR_PUBLICATION_FILTER_CONDITION)
    protected Set<@NotNull @Valid MemberRef> memberOf;

    @Enumerated(EnumType.STRING)
    @NotNull(groups = {WarningValidatorGroup.class })
    @MustDisplay(groups = {PomsValidatorGroup.class})
    @Nullable
    protected AgeRating ageRating;

    @ElementCollection
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Enumerated(value = EnumType.STRING)
    protected List<@NotNull ContentRating> contentRatings;


    @OneToMany(targetEntity = Email.class, orphanRemoval = true, cascade = {ALL})
    @JoinColumn(name = "mediaobject_id", nullable = true)
    @OrderColumn(name = "list_index", nullable = true /* hibernate sucks */)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected @Valid List<@NotNull @Valid Email> email;

    @OneToMany(targetEntity = Website.class, orphanRemoval = true, cascade = {ALL})
    @JoinColumn(name = "mediaobject_id", nullable = true)
    // not nullable media/index blocks ordering updates on the collection
    @OrderColumn(name = "list_index",
        nullable = true // Did I mention that hibernate sucks?
    )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected @Valid List<@NotNull @Valid Website> websites;

    @OneToMany(cascade = ALL, targetEntity = TwitterRef.class, orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id", nullable = true)
    // not nullable media/index blocks ordering updates on the collection
    @OrderColumn(name = "list_index",
        nullable = true // hibernate sucks
    )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<@NotNull @Valid TwitterRef> twitterRefs;

    @Setter
    protected Short teletext;

    @XmlElement
    @Setter
    protected Boolean isDubbed;

    @OneToMany(orphanRemoval = true, mappedBy = "mediaObject", cascade={ALL})
    protected Set<@NonNull @Valid Prediction> predictions;

    @Transient
    @Nullable
    transient Collection<@NonNull @Valid Prediction> predictionsForXml;

    @OneToMany(cascade = ALL, mappedBy = "mediaObject", orphanRemoval = true)
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    //@Filter(name = PUBLICATION_FILTER, condition = "workflow = 'PUBLISHED'") doesn't work
    @PublicationFilter
    protected SortedSet<@NotNull Location> locations = new TreeSet<>();


    @OneToMany(orphanRemoval = true, cascade= {ALL})
    @JoinColumn(name = "mediaobject_id", updatable = false, nullable = false)
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected Set<@NotNull @Valid Relation> relations;

    @Setter
    @OneToMany(
        orphanRemoval = true,
            mappedBy = "mediaObject",
            cascade = {ALL}
    )
    @OrderColumn(
        name = "list_index",
        nullable = true // hibernate sucks
    )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition = PUBLICATION_FILTER_CONDITION_PUBLISHABLES)
    // @Field(name = "images", store=Store.YES, analyze = Analyze.NO,
    // bridge = @FieldBridge(impl = JsonBridge.class, params = @Parameter(name =
    // "class", value = "[Lnl.vpro.domain.media.support.Image;")))
    @PublicationFilter
    protected List<@NotNull @Valid Image> images;

    @Column(nullable = false)
    @JsonIgnore // Oh Jackson2...
    protected boolean isEmbeddable = true;

    // The sortDate field is actually calculable, but it can be a bit
    // expensive, so we cache its value in a persistent field.
    @Column(name = "sortdate", nullable = true, unique = false)
    protected Instant sortInstant;

    // Used for monitoring publication delay. Not exposed via java.
    // Set its value in sql to now() when unmodified media is republished.
    @Column(name = "repubDate", unique = false)
    protected Instant repubDate;


    /**
     * ␟  and ␟ are used to encode multiple reasons (with a date) in headers, and therefore should not be in the database field itself. \t can be used to temporary store multiple reasons, which will all be published at the same time.
     */
    @Column
    @XmlTransient
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    @Pattern(regexp = PublicationReason.REASON_PATTERN)
    protected String repubReason;

    @Column
    @XmlTransient
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected String repubDestinations;

    @Setter
    @Column(nullable = false)
    @JsonIgnore // Oh Jackson2...
    private Boolean locationAuthorityUpdate = false;

    @OneToOne
    @Nullable
    private MediaObject mergedTo;

     // Holds the descendantOf value when unmarshalled from XML. Used by XML
    // clients working in a detached environment.
    @Transient
    @Nullable
    private String mergedToRef;

    // Holds the descendantOf value when unmarshalled from XML. Used by XML
    // clients working in a detached environment.
    @Transient
    @Nullable
    Set<DescendantRef> descendantOf;

    /**
     * If this is set to true, then that indicates that something is changed in
     * the mediaobject which would require a recalculation of the sort date.
     */
    @Transient
    private boolean sortDateValid = false;

    /**
     * If this is set to false, then that indicates that the sort date was set
     * _explictely_ (JAXB unmarshalling), and no other setters can invalidate
     * that.
     */
    @Transient
    private boolean sortDateInvalidatable = true;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Getter(AccessLevel.PACKAGE)
    @Setter(AccessLevel.PACKAGE)
    private AvailableSubtitlesWorkflow subtitlesWorkflow = AvailableSubtitlesWorkflow.NONE;

    @ElementCollection(fetch = FetchType.EAGER)
    // it is needed for every persist and display (because of hasSubtitles), so lets fetch it eager
    // also we got odd NPE's from PersistentBag otherwise. (2023-01: seems still relevant. I think it occurs when you _change_ a type)
    @CollectionTable(name = "Subtitles", joinColumns = @JoinColumn(name = "mid", referencedColumnName = "mid"))
    @OrderBy("language, type")
    @Setter
    private List<@NonNull AvailableSubtitles> availableSubtitles;

    @Embedded()
    @XmlTransient
    @Setter(AccessLevel.PACKAGE)
    private StreamingStatusImpl streamingPlatformStatus = StreamingStatus.unset();


    /**
     * Beta, may be dropped again later.
     * @since 7.1
     */
    @Column
    @XmlTransient
    @Beta()
    String correlationId;


    public MediaObject() {
    }

    public MediaObject(long id) {
        super(id);
    }

    public MediaObject(MediaObject source) {
        super(source);
        this.avType = source.avType;
        this.mid = source.mid;
        this.setEmbeddable(source.isEmbeddable);
        source.getCrids().forEach(this::addCrid);
        source.getBroadcasters().forEach(this::addBroadcaster);
        source.getPortals().forEach(this::addPortal);
        source.getPortalRestrictions()
            .forEach(restriction -> this.addPortalRestriction(PortalRestriction.copy(restriction)));
        source.getGeoRestrictions().forEach(restriction -> this.addGeoRestriction(GeoRestriction.copy(restriction)));
        TextualObjects.copy(source, this);
        source.getGenres().forEach(this::addGenre);
        source.getTags().forEach(this::addTag);
        this.source = source.source;
        source.getCountries().forEach(this::addCountry);
        source.getLanguages().forEach(this::addLanguage);
        this.avAttributes = AVAttributes.copy(source.avAttributes);
        this.releaseYear = source.releaseYear;
        this.duration = AuthorizedDuration.copy(source.duration);
        source.getCredits().forEach(credits -> this.giveCredits(Credits.copy(credits, this)));
        source.getAwards().forEach(this::addAward);
        source.getMemberOf().forEach(ref -> this.createMemberOf(ref.getGroup(), ref.getNumber(), ref.getOwner()));
        this.ageRating = source.ageRating;
        source.getContentRatings().forEach(this::addContentRating);
        source.getEmail().forEach(e ->
            this.getEmail().add(new Email(e.get(), e.getOwner()))
        );
        source.getWebsites().forEach(website ->
            this.addWebsite(Website.copy(website))
        );
        source.getTwitterRefs().forEach(ref -> this.addTwitterRef(TwitterRef.copy(ref)));
        this.teletext = source.teletext;
        source.getPredictions().forEach(prediction -> {
            MediaObjects.updatePrediction(this, prediction.getPlatform(), prediction, prediction.getEncryption());
            MediaObjects.updatePrediction(this, prediction.getPlatform(), prediction.getState());
        });
        source.getLocations().forEach(location -> this.addLocation(Location.copy(location, this)));
        source.getRelations().forEach(relation -> this.addRelation(Relation.copy(relation)));
        source.getImages().forEach(images -> this.addImage(Image.copy(images)));
        this.mergedTo = source.mergedTo;
        this.streamingPlatformStatus = source.streamingPlatformStatus;
    }

    @XmlAttribute(required = true)
    @Override
    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        if (mid == null || mid.isEmpty()) {
            this.mid = null;
            return;
        }
        if (this.mid != null && !this.mid.equals(mid)) {
            throw new IllegalArgumentException(
                    "Not allowed to assign new value to MID (current = " + this.mid + ", new = " + mid + ")");
        }
        this.mid = mid;
    }

    /**
     * Return the available subtitles. These subtitles may not be published.
     * <p>
     * In the publisher this list is explicitly cleared before publishing to the API if there are no published locations
     * This is kind of a hack.  May be it is better to have the workflow in AvailableSubtitles also.
     */
    @XmlElement(name = "availableSubtitles")
    public List<AvailableSubtitles> getAvailableSubtitles() {
        if (availableSubtitles == null) {
            availableSubtitles = new ArrayList<>();
        }
        return availableSubtitles;
    }

    @Override
    @XmlElement(name = "crid")
    @JsonProperty("crids")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getCrids() {
        if (crids == null) {
            crids = new ArrayList<>();
        }
        return crids;
    }

    public MediaObject addCrid(@CRID String crid) {
        if (StringUtils.isNotBlank(crid)) {
            crid = crid.trim();
            if (crids == null) {
                crids = new ArrayList<>();
                crids.add(crid);
            } else if (!crids.contains(crid)) {
                crids.add(crid);
            }
        }
        return this;
    }

    public MediaObject removeCrid(String crid) {
        // When calling crids.remove(crid) Hibernate does not re-index the
        // collection of elements, resulting in duplicate crids and a unique
        // constraint violation. Therefore create a new collection!
        if (crid != null && crids != null) {
            List<String> newCrids = new ArrayList<>();

            for (String c : crids) {
                if (!c.equals(crid.trim())) {
                    newCrids.add(c);
                }
            }
            crids = newCrids;
        }
        return this;
    }

    @XmlElement(name = "broadcaster", required = true)
    @JsonProperty("broadcasters")
    @JsonSerialize(using = BroadcasterList.Serializer.class)
    @JsonDeserialize(using = BroadcasterList.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Broadcaster> getBroadcasters() {
        if (broadcasters == null) {
            broadcasters = new ArrayList<>();
        }
        return broadcasters;
    }

    public MediaObject addBroadcaster(@lombok.NonNull Broadcaster broadcaster) {
        if (this.broadcasters == null) {
            this.broadcasters = new ArrayList<>();
        }

        if (!broadcasters.contains(broadcaster)) {
            broadcasters.add(broadcaster);
        }

        return this;
    }

    public boolean removeBroadcaster(Broadcaster broadcaster) {
        if (broadcaster == null || broadcasters == null) {
            return false;
        }
        return this.broadcasters.remove(broadcaster);
    }

    public Broadcaster getMainBroadcaster() {
        return getFromList(this.broadcasters);
    }

    @XmlElement(name = "portal")
    @JsonProperty("portals")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Portal> getPortals() {
        if (portals == null) {
            portals = new ArrayList<>();
        }
        return portals;
    }

    public void setPortals(List<Portal> portals) {
        this.portals = portals == null ? null : updateList(this.portals, portals.stream().distinct().collect(Collectors.toList()));
    }

    public MediaObject addPortal(Portal portal) {
        if (portal == null) {
            return this;
        }

        if (this.portals == null) {
            this.portals = new ArrayList<>();
        }

        if (!portals.contains(portal)) {
            portals.add(portal);
        }

        return this;
    }

    public boolean removePortal(Portal portal) {
        if (portal == null || portals == null) {
            return false;
        }
        return portals.remove(portal);
    }

    public void clearPortals() {
        if (portals != null) {
            portals.clear();
        }
    }

    public List<ThirdParty> getThirdParties() {
        if (thirdParties == null) {
            thirdParties = new ArrayList<>();
        }
        return thirdParties;
    }

    public MediaObject addThirdParty(ThirdParty thirdParty) {
        if (thirdParty == null) {
            return this;
        }

        if (this.thirdParties == null) {
            this.thirdParties = new ArrayList<>();
        } else if (thirdParties.contains(thirdParty)) {
            return this;
        }

        thirdParties.add(thirdParty);
        return this;
    }

    public boolean removeThirdParty(ThirdParty thirdParty) {
        if (thirdParty == null || thirdParties == null) {
            return false;
        }
        return thirdParties.remove(thirdParty);
    }

    public void clearThirdParties() {
        thirdParties.clear();
    }

    @XmlElement(name = "exclusive")
    @JsonProperty("exclusives")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<PortalRestriction> getPortalRestrictions() {
        if (this.portalRestrictions == null) {
            this.portalRestrictions = new ArrayList<>();
        }
        return this.portalRestrictions;
    }

    public boolean removePortalRestriction(PortalRestriction restriction) {
        if (this.portalRestrictions != null) {
            return this.portalRestrictions.remove(restriction);
        }
        return false;
    }

    @Nullable
    public PortalRestriction findPortalRestriction(Long id) {
        if (portalRestrictions != null) {
            for (PortalRestriction portalRestriction : portalRestrictions) {
                if (portalRestriction.getId().equals(id)) {
                    return portalRestriction;
                }
            }
        }
        return null;
    }

    public void addPortalRestriction(PortalRestriction restriction) {
        if (restriction == null) {
            throw new IllegalArgumentException("PortalRestriction to add should not be null");
        }

        if (this.portalRestrictions == null) {
            this.portalRestrictions = new ArrayList<>();
        }

        if (!portalRestrictions.contains(restriction)) {
            this.portalRestrictions.add(restriction);
        }
    }

    @XmlElement(name = "region")
    @JsonProperty("regions")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<GeoRestriction> getGeoRestrictions() {
        if (geoRestrictions == null) {
            geoRestrictions = new TreeSet<>();
        }
        return sorted(geoRestrictions);
    }

    @Nullable
    public GeoRestriction findGeoRestriction(Long id) {
        if (geoRestrictions != null) {
            for (GeoRestriction geoRestriction : geoRestrictions) {
                if (geoRestriction.getId().equals(id)) {
                    return geoRestriction;
                }
            }
        }
        return null;
    }

    public void addGeoRestriction(GeoRestriction geoRestriction) {
        if (geoRestriction == null) {
            throw new IllegalArgumentException("Null GeoRestriction argument not allowed");
        }

        if (geoRestrictions == null) {
            geoRestrictions = new TreeSet<>();
        }

        geoRestrictions.add(geoRestriction);
    }

    public boolean removeGeoRestriction(GeoRestriction restriction) {
        if (this.geoRestrictions != null) {
            return this.geoRestrictions.remove(restriction);
        }
        return false;
    }

    @Override
    @XmlElement(name = "title", required = true)
    @JsonProperty("titles")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Title> getTitles() {
        if (titles == null) {
            titles = new TreeSet<>();
        }
        return sorted(titles);
    }

    @Override
    public void setTitles(SortedSet<Title> titles) {
        this.titles = titles;
        for (Title t : titles) {
            t.setParent(this);
        }
    }

    /**
     * For NPA-403, to provide to ES the needed mapping.
     * <p>
     * The result can be calculated from other fields, so this is not available in XML, nor in the default json view.
     * <p>
     * Only in the {@link Views.Publisher} version of the json.
     */
    @JsonView({Views.ForwardPublisher.class})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public SortedSet<Title> getExpandedTitles() {
        return TextualObjects.expandTitlesMajorOwnerTypes(this);
    }


    @Override
    public MediaObject addTitle(Title title) {
        if (title != null) {
            if (title.getOwner() != OwnerType.TEMPORARY) {
                removeTitle(OwnerType.TEMPORARY, title.getType());
            }
            this.titles = addTo(titles, title);
        }
        return this;
    }

    @Override
    public boolean removeTitle(Title title) {
        if (titles == null) {
            return false;
        }

        return titles.remove(title);
    }

    @Override
    public TriFunction<String, OwnerType, TextualType, Title> getOwnedTitleCreator() {
        return Title::new;
    }

    @Override
    public MediaObject addTitle(@NonNull String title, @NonNull OwnerType owner, @NonNull TextualType type) {
        if (owner != OwnerType.TEMPORARY) {
            removeTitle(OwnerType.TEMPORARY, type);
        }
        final Title existingTitle = findTitle(owner, type);
        if (existingTitle != null) {
            existingTitle.set(title);
        } else {
            this.addTitle(getOwnedTitleCreator().apply(title, owner, type));
        }
        return this;
    }

    @Override
    public boolean hasTitles() {
        return titles != null && !titles.isEmpty();
    }

    @Override
    public boolean hasDescriptions() {
        return descriptions != null && !descriptions.isEmpty();
    }

    @Override
    @XmlElement(name = "description")
    @JsonProperty("descriptions")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Description> getDescriptions() {
        if (descriptions == null) {
            descriptions = new TreeSet<>();
        }
        return sorted(descriptions);
    }

    @Override
    public void setDescriptions(SortedSet<Description> descriptions) {
        this.descriptions = descriptions;
        for (Description d : descriptions) {
            d.setParent(this);
        }
    }


    @Override
    public MediaObject addDescription(Description description) {
        if (description != null) {
            if (description.getOwner() != OwnerType.TEMPORARY) {
                removeDescription(OwnerType.TEMPORARY, description.getType());
            }
            this.descriptions = addTo(descriptions, description);
        }
        return this;
    }

    private <T extends Child<MediaObject> & Comparable<?>> Set<T> addTo(Set<T> co, T ot) {
        if (ot != null) {
            ot.setParent(this);
            if (co == null) {
                co = new TreeSet<>();
            } else {
                co.remove(ot);
            }
            co.add(ot);
        }
        return co;
    }

    @Override
    public boolean removeDescription(Description description) {
        if (descriptions == null) {
            return false;
        }

        return descriptions.remove(description);
    }

    @Override
    public TriFunction<String, OwnerType, TextualType, Description> getOwnedDescriptionCreator() {
        return Description::new;
    }

    @Override
    public MediaObject addDescription(@Nullable String description, @NonNull OwnerType owner, @NonNull TextualType type) {
        if (description != null) {
            if (owner != OwnerType.TEMPORARY) {
                removeDescription(OwnerType.TEMPORARY, type);
            }
            final Description existingDescription = findDescription(owner, type);

            if (existingDescription != null) {
                existingDescription.set(description);
            } else {
                this.addDescription(getOwnedDescriptionCreator().apply(description, owner, type));
            }
        }
        return this;
    }

    @XmlElement(name = "genre")
    @JsonProperty("genres")
    @JsonSerialize(using = GenreSortedSet.Serializer.class)
    @JsonDeserialize(using = GenreSortedSet.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Genre> getGenres() {
        if (genres == null) {
            genres = new TreeSet<>();
        }
        return sorted(genres);
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = updateSortedSet(this.genres, genres);
    }

    public MediaObject addGenre(@lombok.NonNull Genre genre) {
        if (genres == null) {
            genres = new TreeSet<>();
        }
        genres.add(genre);

        return this;
    }

    boolean removeGenre(Genre genre) {
        return getGenres().remove(genre);
    }

    @Override
    @XmlElement(name = "tag")
    @JsonProperty("tags")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Tag> getTags() {
        if (tags == null) {
            tags = new TreeSet<>();
        }
        return sorted(tags);
    }

    /**
     * Consider using nl.vpro.domain.media.TagService#findOrCreate() first.
     */
    @Override
    public void setTags(Set<Tag> tags) {
        this.tags = updateSortedSet(this.tags, tags);
    }

    //region GeoLocations logic

    @NonNull
    public SortedSet<GeoLocations> getGeoLocations() {
        return this.geoLocations = createIfNull(this.geoLocations);
    }

    public void setGeoLocations(@NonNull SortedSet<GeoLocations> newGeoLocations) {
        this.geoLocations = createIfNullUnlessNull(this.geoLocations, newGeoLocations);
        MediaObjectOwnableLists.setIfNotNull(this, this.geoLocations, newGeoLocations);
    }

    @JsonView({Views.ForwardPublisher.class})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public SortedSet<GeoLocations>  getExpandedGeoLocations() {
        return MediaObjectOwnableLists.expandOwnedList(this.geoLocations,
                (owner, values) -> GeoLocations.builder()
                    .values(values).owner(owner).build(),
                OwnerType.ENTRIES
        );
    }

    //end region

    @NonNull
    public SortedSet<Topics> getTopics() {
        return this.topics = createIfNull(this.topics);
    }

    public void setTopics(@NonNull SortedSet<Topics> newTopics) {
        MediaObjectOwnableLists.set(this, getTopics(), newTopics);
    }

    @JsonView({Views.ForwardPublisher.class})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public SortedSet<Topics> getExpandedTopics() {
        return MediaObjectOwnableLists.expandOwnedList(this.topics,
                (owner, values) -> Topics.builder().values(values).owner(owner).build(),
                OwnerType.ENTRIES);
    }

    @NonNull
    public SortedSet<Intentions> getIntentions() {
        return this.intentions = createIfNull(this.intentions);
    }

    @JsonView({Views.ForwardPublisher.class})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public SortedSet<Intentions>  getExpandedIntentions() {
        return MediaObjectOwnableLists.expandOwnedList(this.intentions,
            (owner, list) -> Intentions.builder()
                .owner(owner)
                .values(list.stream().map(Intention::getValue).collect(Collectors.toList()))
                .build(),
            OwnerType.ENTRIES
        );
    }

    public void setIntentions(SortedSet<@NonNull  Intentions> newIntentions) {
        this.intentions = createIfNullUnlessNull(this.intentions, newIntentions);
        if (this.intentions != null) {
            MediaObjectOwnableLists.setIfNotNull(this, this.intentions, newIntentions);
        }
    }

    @NonNull
    public SortedSet<TargetGroups> getTargetGroups() {
        return this.targetGroups =  createIfNull(this.targetGroups);
    }


    @JsonView({Views.ForwardPublisher.class})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public SortedSet<TargetGroups>  getExpandedTargetGroups() {
         return MediaObjectOwnableLists.expandOwnedList(this.targetGroups,
            (owner, list) -> TargetGroups.builder()
                .owner(owner)
                .values(list.stream().map(TargetGroup::getValue).collect(Collectors.toList()))
                .build(),
            OwnerType.ENTRIES
        );
    }

    public void setTargetGroups(SortedSet<@NonNull TargetGroups> newTargetGroups) {
        this.targetGroups = createIfNullUnlessNull(this.targetGroups, newTargetGroups);
        if (this.targetGroups != null) {
            MediaObjectOwnableLists.set(this, this.targetGroups, newTargetGroups);
        }
    }

    @XmlElement
    public String getSource() {
        return source;
    }


    /**
     * The production location
     */
    @XmlElement(name = "country")
    @JsonProperty("countries")
    @JsonSerialize(using = CountryCodeList.Serializer.class)
    @JsonDeserialize(using = CountryCodeList.Deserializer.class)
    @XmlJavaTypeAdapter(value = CountryCodeAdapter.class)
    public List<org.meeuw.i18n.regions. @PomsValidCountry Region> getCountries() {
        if (countries == null) {
            countries = new ArrayList<>();
        }
        return countries;
    }

    public void setCountries(List<org.meeuw.i18n.regions.Region> countries) {
        this.countries = updateList(this.countries, countries);
    }

    public MediaObject addCountry(String code) {
        return addCountry(RegionService.getInstance().getByCode(code).orElseThrow(() ->
            new IllegalArgumentException("Unknown country " + code))
        );
    }

    public MediaObject addCountry(@NonNull CountryCode country) {
        return addCountry(Country.of(country));
    }

    public MediaObject addCountry(org.meeuw.i18n.regions.@NonNull Region country) {
        if (countries == null) {
            countries = new ArrayList<>();
        }

        if (!countries.contains(country)) {
            countries.add(country);
        }
        return this;
    }

    @XmlElement(name = "language")
    @XmlJavaTypeAdapter(value = LocaleAdapter.class)
    @JsonProperty("languages")
    @JsonSerialize(using = LanguageList.Serializer.class)
    @JsonDeserialize(using = LanguageList.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(implementation = String.class, type = "string")
    public List<Locale> getLanguages() {
        if (languages == null) {
            languages = new ArrayList<>();
        }
        return languages;
    }

    public void setLanguages(List<Locale> languages) {
        this.languages = updateList(this.languages, languages);
    }

    public MediaObject addLanguage(@lombok.NonNull Locale language) {
        if (languages == null) {
            languages = new ArrayList<>();
        }

        if (!languages.contains(language)) {
            languages.add(language);
        }

        return this;
    }

    @XmlAttribute(name = "avType", required = true)
    @JsonProperty("avType")
    @Nullable
    public AVType getAVType() {
        return avType;
    }

    public void setAVType(AVType avType) {
        this.avType = avType;
    }

    @XmlElement
    public AVAttributes getAvAttributes() {
        return avAttributes;
    }

    @XmlElement()
    public Short getReleaseYear() {
        return releaseYear;
    }

    @XmlElement()
    @Nullable
    public AuthorizedDuration getDuration() {
        return duration;
    }

    void setDuration(@Nullable AuthorizedDuration duration) {
        this.duration = duration;
    }


    /**
     * Use {@link AuthorizedDuration#get} in combination with {@link #getDuration} to get the java.time.Duration
     * @throws ModificationException If you may not set the duration
     */
    @JsonIgnore
    @XmlTransient
    public void setDuration(java.time.@Nullable Duration duration) throws ModificationException {
        if (this.duration != null && ObjectUtils.notEqual(this.duration.get(), duration) && hasAuthorizedDuration()) {
            throw new ModificationException("Updating an existing and authorized duration is not allowed");
        }
        if (duration == null) {
            this.duration = null;
        } else if (this.duration == null) {
            this.duration = AuthorizedDuration.of(duration);
        } else {
            this.duration.set(duration);
        }
    }



    public boolean hasAuthorizedDuration() {
        return duration != null && duration.isAuthorized();
    }

    @XmlElementWrapper(name = "credits")
    @XmlElements({
        @XmlElement(name = "person", type = Person.class),
        @XmlElement(name = "name", type = Name.class)
    })
    @JsonIgnore
    public List<Credits> getCredits() {
        if (credits == null) {
            credits = new ArrayList<>();
        }
        return credits;
    }

    /**
     * This method just exists to contain the json annotations.
     * Putting them on {@link #getCredits()} complicates matters, because we basicly configured jackson to
     * use jaxb annotations.
     */
    @JsonProperty("credits")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected List<Credits> getJsonCredits() {
        return getCredits();
    }
    protected void setJsonCredits(List<Credits> credits) {
        setCredits(credits);
    }

    public void setCredits(@Nullable List<? extends Credits> credits) {
        if (credits != null) {
            for (Credits name : credits) {
                name.setParent(this);
            }
        }
        this.credits = updateList(this.credits, credits);
    }

    /**
     * Returns only the {@link #getCredits()} that are {@link Person}
     */
    public List<Person> getPersons() {
        return getCredits().stream()
            .filter(c -> c instanceof Person)
            .map(c -> (Person) c)
            .collect(Collectors.toList());
    }
     /**
     * @deprecated Use {@link #setCredits(List)}
     */
    @Deprecated
    public void setPersons(@Nullable List<Person> persons) {
        setCredits(persons);
    }

    public boolean removePerson(Person person) {
        if (credits != null) {
            return credits.remove(person);
        }
        return false;
    }

    public boolean removePerson(Long id) {
        if (credits == null) {
            return false;
        }

        for (Credits person : credits) {
            if (id.equals(person.getId())) {
                return removePerson((Person) person);
            }
        }

        return false;
    }

    public MediaObject addPerson(Person person) {
        return giveCredits(person);
    }

    public MediaObject addName(Name name) {
        return giveCredits(name);
    }

    public boolean removeName(Name name) {
        if (credits != null) {
            return credits.remove(name);
        }
        return false;
    }

    /**
     * @since 5.12
     */
    public MediaObject giveCredits(Credits credit) {
        if (credits == null) {
            credits = new ArrayList<>();
        }

        if (!credits.contains(credit)) {
            if (credit != null) {
                credit.setParent(this);
                credit.setListIndex(credits.size());
                credits.add(credit);
            }
        }

        return this;
    }

    @Nullable
    public Person findPerson(Person person) {
        if (credits == null) {
            return null;
        }

        for (Credits p : credits) {
            if (p.equals(person)) {
                return (Person) p;
            }
        }

        return null;
    }

    @Nullable
    public Credits findCredit(Long id) {
        if (credits == null) {
            return null;
        }

        for (Credits credit : credits) {
            if (credit.getId().equals(id)) {
                return credit;
            }
        }

        return null;
    }

    @Nullable
    public Person findPerson(Long id) {
        if (credits == null) {
            return null;
        }

        for (Credits credit : credits) {
            if (credit instanceof Person && credit.getId().equals(id)) {
                return (Person) credit;
            }
        }

        return null;
    }

    @Nullable
    public Name findName(Long id) {
        if (credits == null) {
            return null;
        }

        for (Credits credit : credits) {
            if (credit instanceof Name && credit.getId().equals(id)) {
                return (Name) credit;
            }
        }

        return null;
    }


    @XmlElement(name = "award")
    @JsonProperty("awards")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getAwards() {
        if (awards == null) {
            awards = new ArrayList<>();
        }
        return awards;
    }

    public void setAwards(List<String> awards) {
        this.awards = updateList(this.awards, awards);
    }

    public MediaObject addAward(String award) {
        if (awards == null) {
            awards = new ArrayList<>();
        }

        if (!awards.contains(award)) {
            awards.add(award);
        }

        return this;
    }

    @XmlElement(name = "descendantOf")
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<DescendantRef> getDescendantOf() {
        if (descendantOf == null) {
            descendantOf = new TreeSet<>();
            for (MediaObject media : getAncestors()) {
                descendantOf.add(DescendantRef.forOwner(media));
            }
            descendantOf.addAll(getVirtualMemberRefs().stream()
                .map(DescendantRef::of).toList());
        }
        return sorted(descendantOf);
    }

    void setDescendantOf(Set<DescendantRef> descendantOf) {
        this.descendantOf = updateSortedSet(this.descendantOf, descendantOf);
    }

    @XmlElement()
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonManagedReference
    public SortedSet<@NonNull MemberRef> getMemberOf() {
        if (memberOf == null) {
            memberOf = new TreeSet<>();
        }
        for (MemberRef r : memberOf) {
            r.setRefType(MemberRefType.memberOf);
        }
        return sorted(memberOf);
    }

    public void setMemberOf(SortedSet<MemberRef> memberOf) {
        if (this.memberOf == null) {
            this.memberOf = new TreeSet<>();
        } else {
            this.memberOf.clear();
        }
        if (memberOf != null) {
            for (MemberRef ref : memberOf) {
                ref.setRefType(MemberRefType.memberOf);
                this.memberOf.add(MemberRef.copy(ref, this));
            }
        }
    }

    public boolean isMember() {
        return memberOf != null && !memberOf.isEmpty();
    }

    public boolean isMemberOf(MediaObject owner) {
        return MemberRefs.isOf(memberOf, owner);
    }

    public boolean isMemberOf(MediaObject owner, Integer number) {
        return MemberRefs.isOf(memberOf, owner, number);
    }

    public boolean hasMember(MediaObject member) {
        return member.isMemberOf(this);
    }

    public MemberRef findMemberOfRef(Long memberRefId) {
        for (MemberRef memberRef : memberOf) {
            if (memberRefId.equals(memberRef.getId())) {
                return memberRef;
            }
        }
        return null;
    }

    @Nullable
    public MemberRef findMemberOfRef(MediaObject owner) {
        for (MemberRef memberRef : memberOf) {
            if (owner.equals(memberRef.getGroup())) {
                return memberRef;
            }
        }
        return null;
    }

    @Nullable
    public MemberRef findMemberOfRef(MediaObject owner, Integer number) {
        if (memberOf == null) {
            return null;
        }

        for (MemberRef memberRef : memberOf) {
            if (owner.equals(memberRef.getGroup())) {
                if ((number == null && memberRef.getNumber() == null)
                    || (number != null && number.equals(memberRef.getNumber()))) {

                    return memberRef;
                }
            }
        }
        return null;
    }

    MemberRef createMember(
        @NonNull MediaObject member,
        @Nullable Integer number,
        OwnerType owner) throws CircularReferenceException {
        if (number == null) {
            throw new IllegalArgumentException("Must supply an ordering number.");
        }

        if (this.equals(member)) {
            throw CircularReferenceException.self(member, this, findAncestry(member));
        }
        if (this.hasAncestor(member)) {
            throw new CircularReferenceException(member, this, findAncestry(member));
        }

        if (member.memberOf == null) {
            member.memberOf = new TreeSet<>();
        }

        MemberRef memberRef = new MemberRef(member, this, number, owner);
        member.memberOf.add(memberRef);
        member.descendantOf = null;
        return memberRef;
    }

    MemberRef createMemberOf(
        @NonNull MediaObject group,
        Integer number,
        OwnerType owner) throws CircularReferenceException {
        return group.createMember(this, number, owner);

    }

    boolean removeMemberOfRef(MediaObject reference) {
        boolean success = false;
        if (memberOf != null) {
            Iterator<MemberRef> it = memberOf.iterator();

            while (it.hasNext()) {
                MemberRef memberRef = it.next();

                if (Objects.equals(memberRef.getGroup(), reference)) {
                    it.remove();
                    success = true;
                    descendantOf = null;
                }
            }
        }
        return success;
    }

    boolean removeMemberOfRef(Long memberRefId) {
        boolean success = false;
        if (memberOf != null) {
            Iterator<MemberRef> it = memberOf.iterator();

            while (it.hasNext()) {
                MemberRef memberRef = it.next();

                if (memberRef.getId().equals(memberRefId)) {
                    it.remove();
                    success = true;
                    descendantOf = null;
                }
            }
        }
        return success;
    }

    boolean removeMemberOfRef(MemberRef memberRef) {
        boolean success = false;
        if (memberOf != null) {
            Iterator<MemberRef> it = memberOf.iterator();

            while (it.hasNext()) {
                MemberRef existing = it.next();

                if (existing.equals(memberRef)) {
                    it.remove();
                    success = true;
                }
            }
        }
        return success;
    }

    @Override
    @XmlElement()
    @Nullable
    public AgeRating getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(@Nullable AgeRating ageRating) {
        if (this.ageRating != ageRating) {
            this.locationAuthorityUpdate = true;
        }
        this.ageRating = ageRating;
    }

    @Override
    @XmlElement(name = "contentRating")
    @JsonProperty("contentRatings")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<ContentRating> getContentRatings() {
        if (contentRatings == null) {
            contentRatings = new ArrayList<>();
        }
        return contentRatings;
    }

    public MediaObject setContentRatings(List<ContentRating> contentRatings) {
        this.contentRatings = updateList(this.contentRatings, contentRatings);
        return this;
    }

    public MediaObject addContentRating(ContentRating rating) {
        if (rating == null) {
            return this;
        }

        if (contentRatings == null) {
            contentRatings = new ArrayList<>();
        }

        if (!contentRatings.contains(rating)) {
            contentRatings.add(rating);
        }

        return this;
    }

    @XmlElement()
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<@Valid Email> getEmail() {
        if (email == null) {
            email = new ArrayList<>();
        }
        return email;
    }

    public void setEmail(List<Email> email) {
        this.email = updateList(this.email, email);
    }

    @Nullable
    public String getMainEmail() {
        return Optional.ofNullable(getFromList(email)).map(Email::get).orElse(null);
    }


    public MediaObject addEmail(String email) {
        return addEmail(email, OwnerType.BROADCASTER);
    }


    public MediaObject addEmail(String email, OwnerType owner) {
        if (StringUtils.isBlank(email)) {
            return this;
        }

        if (this.email == null) {
            this.email = new ArrayList<>();
        }

        email = email.trim();

        Email proposal = new Email(email, owner);
        if (!this.email.contains(proposal)) {
            this.email.add(proposal);
        }

        return this;
    }

    @Override
    @XmlElement(name = "website")
    @JsonProperty("websites")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<@Valid Website> getWebsites() {
        if (websites == null) {
            websites = new ArrayList<>();
        }
        return websites;
    }

    @Override
    public MediaObject setWebsites(List<Website> websites) {
        //this.websites = websites;
        this.websites = updateList(this.websites, websites);
        return this;
    }

    @Override
    @XmlElement(name = "twitter")
    @JsonProperty("twitter")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<TwitterRef> getTwitterRefs() {
        if (twitterRefs == null) {
            twitterRefs = new ArrayList<>();
        }
        return twitterRefs;
    }

    @Override
    public void setTwitterRefs(List<@NonNull  TwitterRef> twitterRefs) {
        this.twitterRefs = updateList(this.twitterRefs, twitterRefs);
    }

    @XmlElement()
    public Short getTeletext() {
        return teletext;
    }


    public Boolean hasSubtitles() {
        return isHasSubtitles();
    }

    public Boolean isDubbed() {
        return isDubbed;
    }


    @XmlAttribute(name = "hasSubtitles")
    @XmlJavaTypeAdapter(FalseToNullAdapter.class)
    protected Boolean isHasSubtitles() {
        if (mid == null)  {
            return false;
        }
        try {
            final List<AvailableSubtitles> list = getAvailableSubtitles();

            if (list == null || list.isEmpty()) {
                return false;
            }
            List<AvailableSubtitles> copy = new ArrayList<>(getAvailableSubtitles());
            return copy
                .stream()
                .filter(sub -> {
                    if (sub == null ) {
                        log.warn("{} has 'null' as subtitles", this);
                    }
                    return sub != null;
                    }
                )
                .anyMatch(
                    sub -> Locales.DUTCH.equals(sub.getLanguage()) &&
                        SubtitlesType.CAPTION == sub.getType());
        } catch (Exception e) {
            log.warn(e.getMessage(), e);
            return false;
        }
    }

    protected void setHasSubtitles(Boolean hasSubtitles) {
        // only to satisfy jaxb
        // it will set 'available subtitles' too.
    }


    public boolean hasEpisode(Program episode) {
        return episode.isEpisodeOf(this);
    }

    public boolean hasAncestor(MediaObject ancestor) {
        if (!isMember()) {
            return false;
        }
        for (MemberRef memberRef : memberOf) {
            if (Objects.equals(memberRef.getGroup(), ancestor) || (memberRef.getMidRef() != null && memberRef.getMidRef().equals(ancestor.getMid())) || memberRef.getGroup().hasAncestor(ancestor)) {
                return true;
            }
        }
        for (DescendantRef descendantRef : getDescendantOf()) {
            if (descendantRef.getMidRef() != null && descendantRef.getMidRef().equals(ancestor.getMid())) {
                return true;
            }
        }

        return false;
    }

    public List<MediaObject> findAncestry(MediaObject ancestor) {
        final List<MediaObject> ancestry = new ArrayList<>();
        findAncestry(ancestor, ancestry);
        return ancestry;
    }

    protected void findAncestry(MediaObject ancestor, List<MediaObject> ancestors) {
        if (isMember()) {
            for (MemberRef memberRef : memberOf) {
                if (Objects.equals(memberRef.getGroup(), ancestor)) {
                    ancestors.add(ancestor);
                    return;
                }

                memberRef.getGroup().findAncestry(ancestor, ancestors);
                if (!ancestors.isEmpty()) {
                    ancestors.add(memberRef.getGroup());
                    return;
                }
            }
        }
    }

    public boolean hasDescendant(MediaObject descendant) {
        return descendant.hasAncestor(this);
    }

    void addAncestors(SortedSet<MediaObject> set) {
        if (isMember()) {
            for (MemberRef memberRef : memberOf) {
                if (! memberRef.isVirtual()) {
                    final MediaObject reference = memberRef.getGroup();
                    if (set.add(reference)) { // avoid stack overflow if object
                        // happens to be descendant of itself
                        reference.addAncestors(set);
                    }
                }
            }
        }
    }

    public SortedSet<MediaObject> getAncestors() {
        SortedSet<MediaObject> set = new TreeSet<>((mediaObject, mediaObject1) -> {
            if (mediaObject == null || mediaObject1 == null) {
                return 1;
            }

            if (mediaObject.getId() == null) {
                if (mediaObject1.getId() == null) {
                    return mediaObject1.hashCode() - mediaObject.hashCode();
                } else {
                    return 1;
                }
            }

            return mediaObject.getId().compareTo(mediaObject1.getId() == null ? 0 : mediaObject1.getId());
        });
        addAncestors(set);
        return set;
    }

    /**
     * @since 5.9
     */
    protected Set<MemberRef> getVirtualMemberRefs() {
        Set<MemberRef> result = new TreeSet<>();
        if (memberOf != null) {
            for (MemberRef memberRef : memberOf) {
                if (memberRef.isVirtual()) {
                    result.add(memberRef);
                }

            }
        }
        return result;
    }

    /**
     * Returns (a copy of, since you have no business setting it) the {@link StreamingStatus}.
     * <p>
     * Note that this field is {@link XmlTransient} and not included in the json view or xml view. It is only used on
     * the server side, and <em>not available in the frontend api!</em>
     * @since 5.11
     */
    public StreamingStatus getStreamingPlatformStatus() {
        return StreamingStatus.copy(streamingPlatformStatus);
    }

    protected StreamingStatusImpl getModifiableStreamingPlatformStatus() {
        return streamingPlatformStatus;
    }

    /**
     * Every {@code MediaObject} can be assigned several {@link Prediction prediction records} (one per {@link Platform}). Originally these contained information about 'predicted playability' only. They can be used more generally, and also indicate whether for a certain platform the object <em>is</em> or <em>was</em> playable.
     *
     * @see MediaObjects#isPlayable(MediaObject)
     */
    @XmlTransient
    @NonNull
    public SortedSet<Prediction> getPredictions() {
        if (predictions == null) {
            if (predictionsForXml != null) {
                predictions = new TreeSet<>(predictionsForXml);
            } else {
                predictions = new TreeSet<>();
            }
        }



        // SEE https://jira.vpro.nl/browse/MSE-2313
        return new SortedSetSameElementWrapper<>(sorted(predictions)) {
            @Override
            protected Prediction adapt(Prediction prediction) {
                prediction.setParent(MediaObject.this);
                MediaObjects.autoCorrectPrediction(prediction, MediaObject.this);
                return prediction;
            }
            @Override
            public boolean add(Prediction element) {
                return super.add(adapt(element));
            }
        };
    }

   /**
     * Implicitly create predictions for all platforms that have a location, but no prediction yet.
     */
    public void implicitPredictions() {
        final Map<Platform, List<Location>> locations = new HashMap<>();
        getLocations().stream()
            .filter(Location::hasPlatform)
            .filter(Location::isConsiderableForPublication)
            .forEach(l ->
                locations.computeIfAbsent(
                    l.getPlatform(),
                    p -> new ArrayList<>()
                ).add(l)
            );
        for (Map.Entry<Platform, List<Location>> entry : locations.entrySet()) {
            findOrCreatePrediction(entry.getKey(), true, (created) -> {
                for (Location l : entry.getValue()) {
                    // make sure that such an implicit prediction is not more permissive then
                    Embargos.copyIfLessRestrictedOrTargetUnset(l.getOwnEmbargo(), created.getOwnEmbargo());
                }
                created.setState(Prediction.State.of(created));
                log.info("Implicitly created prediction {} for {} ({})", created, this, entry.getValue());
            });
        }
    }

    public void setPredictions(Collection<Prediction> predictions) {
        this.predictions = updateSortedSet(this.predictions, predictions);
        this.predictionsForXml = null;
    }

    /**
     * We don't return 'non announced' predictions in the xml.
     */
    @XmlElement(name = "prediction")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("predictions")
    protected Collection<Prediction> getPredictionsForXml() {
        if (predictionsForXml == null) {
            implicitPredictions();
            predictionsForXml = Collections2.filter(getPredictions(), p -> p.isPlannedAvailability() && p.getState() != Prediction.State.NOT_ANNOUNCED);

        }
        return predictionsForXml;
    }

    protected void setPredictionsForXml(List<Prediction> predictions) {
        // not call be jackson any more?
        this.setPredictions(predictions);
    }

    /**
     * Like {@link #getPrediction(Platform)} but without also implicitly correcting the {@link Prediction#getState() prediction state} if that happens to be not consistent with the {@link #getLocations() locations}. In other words this just returns the requested prediction without side effects.
     * <p>
     */
    public Prediction getPredictionWithoutFixing(Platform platform) {
        return MediaObjects.getPrediction(platform, predictions);
    }

    /**
     * Returns the prediction for given {@link Platform}. As a side effect the state of this prediction may also be implicitly corrected if that happens to be not consistent with the {@link #getLocations() locations}. In other words this returns the requested prediction, but also ensures that the prediction state is consistent with the locations.
     * @see #getPredictionWithoutFixing(Platform)
     */
    public Prediction getPrediction(Platform platform) {
        return MediaObjects.getPrediction(platform, getPredictions());
    }

    /**
     * see {@link Prediction#getAuthority()} ()} or User, if no such prediction
     * record.
     */
    public Authority getAuthority(Platform platform) {
        Prediction prediction = getPrediction(platform);
        return prediction == null ? Authority.USER : prediction.getAuthority();
    }

    void realizePrediction(Location location) {
        if (locations == null || (!locations.contains(location) && findLocation(location.getId()) == null)) {
            throw new IllegalArgumentException(
                    "Can only realize a prediction when accompanying locations is unavailable. Location " + location
                            + " is not available in " + getMid() + " " + locations);
        }

        Platform platform = location.getPlatform();
        if (platform == null) {
            log.debug("Can't realize prediction with location {} because it has no platform", location);
            return;
        }

        Prediction prediction = getPredictionWithoutFixing(platform);
        if (prediction == null) {
            if (! location.isDeleted()) {
                log.debug("No prediction for {}", location);
                findOrCreatePrediction(platform, true, (c) -> {
                    MediaObjects.correctPrediction(c, this, Level.DEBUG, instant(), (ps, p) -> {
                    });
                });
            }
        } else {
            if (!location.isDeleted()) {
                prediction.setPlannedAvailability(true);
            }
            MediaObjects.correctPrediction(prediction, this, Level.DEBUG, instant(), (ps, p) -> {});
        }
    }

    void correctPrediction(Platform platform) {
        Prediction prediction = getPredictionWithoutFixing(platform);
        if (prediction != null) {
            MediaObjects.correctPrediction(prediction, this, Level.DEBUG, instant(), (ps, p) -> {});
        }
    }

    public void correctPredictions() {
        implicitPredictions();
        for (Platform p : Platform.values()) {
            correctPrediction(p);
        }
    }

    public boolean removePrediction(Platform platform) {
        if (predictions == null) {
            return false;
        }

        return predictions.remove(new Prediction(platform));
    }

    public Prediction findOrCreatePrediction(Platform platform) {
        return findOrCreatePrediction(platform, false, (c) -> {});
    }

    protected Prediction findOrCreatePrediction(Platform platform, boolean planned, Consumer<Prediction> onCreate) {
        Prediction prediction = MediaObjects.getPrediction(platform, predictions);
        if (prediction == null) {
            log.debug("Creating prediction object for {}: {}", platform, this);
            prediction = new Prediction(platform);
            prediction.setPlannedAvailability(planned);
            prediction.setParent(this);
            prediction.setAuthority(Authority.USER);
            if (predictions == null) {
                predictions = new TreeSet<>();
                this.predictionsForXml = null;
            }
            this.predictions.add(prediction);
            onCreate.accept(prediction);
        }
        return prediction;
    }


    /**
     * Returns the locations in {@link Location#PRESENTATION_ORDER}
     */
    @NonNull
    public SortedSet<Location> getLocations() {
        if (locations == null) {
            locations = new TreeSet<>();
        }
        return locations;
    }

    @XmlElementWrapper(name = "locations")
    @XmlElement(name = "location")
    @JsonProperty("locations")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonSerialize(using = CollectionOfPublishable.class)
    protected SortedSet<Location> get_Locations() {
        return getLocations();
    }

    protected void set_Locations(SortedSet<Location> locations) {
        this.locations = locations;
    }

    /**
     * Returns the locations in {@link Location#PRESENTATION_ORDER}
     * @since 5.12 (in 5.11 {@link #getLocations} behaved like this)
     */
    public SortedSet<Location> getPresentationOrderLocations() {
        if (locations == null) {
            locations = new TreeSet<>();
        }
        return new ResortedSortedSet<>(locations, Location.PRESENTATION_ORDER);
    }

    public void setLocations(SortedSet<Location> locations) {

        if (this.locations == null) {
            this.locations = new TreeSet<>();
        } else {
           this.locations.clear();
        }
        for (Location l : locations) {
            l = Location.copy(l, this);
            addLocation(l);
        }
    }



    @Nullable
    public Location getLocation(Location location) {
        if (locations != null) {
            for (Location existing : locations) {
                if (existing.equals(location)) {
                    return existing;
                }
            }
        }
        return null;
    }

    @Nullable
    public Location findLocation(Long locationId) {
        if (locations != null && locationId != null) {
            for (Location location : locations) {
                if (locationId.equals(location.getId())) {
                    return location;
                }
            }
        }
        return null;
    }

    @Nullable
    public Location findLocation(String url) {
        if (locations != null && StringUtils.isNotBlank(url)) {
            for (Location location : locations) {
                if (url.equals(location.getProgramUrl())) {
                    return location;
                }
            }
        }
        return null;
    }

    @Nullable
    public Location findLocation(String url, OwnerType owner) {
        if (locations != null && StringUtils.isNotBlank(url) && owner != null) {
            for (Location location : locations) {
                if (location.getProgramUrl().equals(url) && owner == location.getOwner()) {
                    return location;
                }
            }
        }
        return null;
    }

    public MediaObject addLocation(Location location) {
        return addLocation(location, true);
    }

    public MediaObject addLocation(Location location, boolean implicitRealize) {
        if (location == null || location.getProgramUrl() == null) {
            throw new IllegalArgumentException("Must supply a not null location with an url.");
        }

        if (locations == null) {
            locations = new TreeSet<>();
        }

        Location existing = findLocation(location.getProgramUrl());

        if (existing != null) {
            if (!Objects.equals(location.getOwner(), existing.getOwner())
                    || !Objects.equals(location.getPlatform(), existing.getPlatform())) {

                throw new IllegalArgumentException("Collisions while updating " + existing + " with " + location);
            }

            existing.setAvAttributes(location.getAvAttributes());
            Embargos.copy(location, existing);
            existing.setSubtitles(location.getSubtitles());
            existing.setDuration(location.getDuration());
            existing.setOffset(location.getOffset());
        } else {
            locations.add(location);
            location.setParent(this);
        }
        if (implicitRealize) {
            realizePrediction(location);
        }
        return this;
    }

    public boolean removeLocation(Location location) {
        if (locations != null && locations.remove(location)) {
            markCeresUpdate();
            correctPrediction(location.getPlatform());
            return true;
        }
        return false;
    }

    public boolean removeLocation(final Long locationId) {
        boolean success = false;
        if (locationId != null && locations != null) {
            Iterator<Location> iterator = locations.iterator();
            while (iterator.hasNext()) {
                Location location = iterator.next();
                if (locationId.equals(location.getId())) {
                    iterator.remove();
                    markCeresUpdate();
                    correctPrediction(location.getPlatform());
                    success = true;
                }
            }

        }
        return success;
    }

    public void revokeLocations(Platform platform) {
        if (locations != null) {
            locations.removeIf(location -> platform.equals(location.getPlatform()));
        }

        Prediction prediction = findOrCreatePrediction(platform);
        prediction.setState(Prediction.State.REVOKED);
    }

    /**
     * Property used for marshalling/unmarshalling, avoiding the {@link Relation#copy} (which disappears the id)
     */
    @XmlElement(name = "relation")
    @JsonProperty("relations")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected SortedSet<Relation> get_Relations() {
        return getRelations();
    }

    protected void set_Relations(SortedSet<Relation> relations) {
        this.relations = relations;
    }


    public SortedSet<Relation> getRelations() {
        if (this.relations == null) {
            this.relations = new TreeSet<>();
        }
        return sorted(relations);
    }

    public void setRelations(SortedSet<Relation> relations) {
        if (this.relations == null) {
            this.relations = new TreeSet<>();
        } else {
            this.relations.clear();
        }
        for (Relation i : relations) {
            addRelation(Relation.copy(i));
        }
    }

    public MediaObject addRelation(@lombok.NonNull Relation relation) {
        if (this.relations == null) {
            this.relations = new TreeSet<>();
        }

        this.relations.add(relation);
        return this;
    }

    @Nullable
    public Relation findRelation(Relation relation) {
        if (relations != null) {
            for (Relation existing : relations) {
                if (Objects.equals(existing, relation)) {
                    return existing;
                }
            }
        }

        return null;
    }

    @Nullable
    public Relation findRelation(Long id) {
        if (relations != null && id != null) {
            for (Relation relation : relations) {
                if (id.equals(relation.getId())) {
                    return relation;
                }
            }
        }
        return null;
    }

    public boolean removeRelation(Long id) {
        if (relations != null && id != null) {
            for (Iterator<Relation> iterator = relations.iterator(); iterator.hasNext();) {
                Relation relation = iterator.next();

                if (id.equals(relation.getId())) {
                    iterator.remove();
                    return true;
                }
            }
        }

        return false;
    }

    @XmlElementWrapper(name = "images")
    @XmlElement(name = "image", namespace = Xmlns.SHARED_NAMESPACE)
    @JsonProperty("images")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @NonNull
    public List<Image> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }
        // Occasionally images contains null elements due to a Hibernate
        // synchronous access issue.
        images.removeIf(Objects::isNull);

        return images;
    }

    @Nullable
    public Image getImage(Image image) {
        if (images != null) {
            for (Image existing : getImages()) {
                if (existing.equals(image)) {
                    return existing;
                }
            }
        }
        return null;
    }

    @Nullable
    public Image getImage(int index) {
        if (images == null || index >= images.size() || index < 0) {
            return null;
        }

        return images.get(index);
    }

    @Nullable
    public Image getImage(ImageType type) {
        if (images != null) {
            for (Image image : images) {
                if (image.getType() == type) {
                    return image;
                }
            }
        }

        return null;
    }

    @Nullable
    public Image getMainImage() {
        if (images != null && !images.isEmpty()) {
            return images.get(0);
        }
        return null;
    }

    public MediaObject addImage(Image image) {
        return addImage(image, images == null ? 0 : images.size());
    }

    public MediaObject addImage(Image image, int index) {
        if (image == null) {
            throw new IllegalArgumentException();
        }
        getImages().add(index, image);
        image.setParent(this);
        return this;
    }

    public List<Image> findImages(OwnerType owner) {
        return getImages().stream().filter(i -> owner.equals(i.getOwner())).collect(Collectors.toList());
    }

    @Nullable
    public Image findImage(ImageType type) {
        for (Image image : getImages()) {
            if (type.equals(image.getType())) {
                return image;
            }
        }

        return null;
    }

    @Nullable
    public Image findImage(Long id) {
        if (images != null && id != null) {
            for (Image image : getImages()) {
                if (image != null) {
                    if (id.equals(image.getId())) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public Image findImage(String url, OwnerType owner) {
        if (images != null) {
            for (Image image : getImages()) {
                if (image != null) {
                    String uri = image.getImageUri();
                    if (uri != null && uri.equals(url) && owner == image.getOwner()) {
                        return image;
                    }
                }
            }
        }
        return null;
    }

    public boolean removeImage(@NonNull Image image) {
        image.setParent(null);
        if (image.getCrids() != null) {
            image.getCrids().clear();
        }
        if (images != null) {
            return images.remove(image);
        }
        return false;
    }

    public boolean removeImage(Long imageId) {
        boolean success = false;
        if (imageId != null && images != null) {

            for (Image image : getImages()) {
                if (imageId.equals(image.getId())) {
                    success = removeImage(image);
                    break;
                }
            }
        }
        return success;
    }

    /**
     * What does it mean to be 'embeddable'?
     */
    @XmlAttribute(name = "embeddable")
    public boolean isEmbeddable() {
        return isEmbeddable;
    }

    public void setEmbeddable(boolean embeddable) {
        isEmbeddable = embeddable;
    }

    /**
     * When true Ceres/Pluto needs a restriction update. The underlying field
     * is managed by Hibernate, and not accessible.
     */
    public boolean isLocationAuthorityUpdate() {
        return locationAuthorityUpdate;
    }

    @NonNull
    @Override
    public MediaObject setPublishStartInstant(Instant publishStart) {
        if (!Objects.equals(this.publishStart, publishStart)) {
            invalidateSortDate();
            if (hasInternetVodAuthority()) {
                locationAuthorityUpdate = true;
            }
        }
        return super.setPublishStartInstant(publishStart);
    }

    @NonNull
    @Override
    public MediaObject setPublishStopInstant(Instant publishStop) {
        if (!Objects.equals(this.publishStop, publishStop)) {
            invalidateSortDate();
            if (hasInternetVodAuthority()) {
                locationAuthorityUpdate = true;
            }
        }
        return super.setPublishStopInstant(publishStop);
    }

    protected boolean hasInternetVodAuthority() {
        return getAuthority(Platform.INTERNETVOD) == Authority.USER;
    }

    @Override
    public void setCreationInstant(Instant creationInstant) {
        invalidateSortDate();
        super.setCreationInstant(creationInstant);
    }

    @Override
    protected void setWorkflow(Workflow workflow) {
        if (workflow == Workflow.PUBLISHED && isMerged()) {
            throw new IllegalArgumentException(
                    "Merged media should obtain workflow  \"MERGED\" instead of \"PUBLISHED\"");
        }

        if (((this.workflow == Workflow.DELETED && workflow != Workflow.DELETED)
                || (this.workflow != Workflow.DELETED && workflow == Workflow.DELETED)) && hasInternetVodAuthority()) {
            locationAuthorityUpdate = true;
        }

        super.setWorkflow(workflow);
    }

    /**
     * Returns the sortDate for this MediaObject. The default behaviour for this
     * field falls back to other available fields in order:
     * <ul>
     * <li>First ScheduleEvent</li>
     * <li>Publication start</li>
     * <li>Creation date</li>
     * </ul>
     * <p/>
     * Subclasses might override this behavior and supply a more explicit value
     * persisted separately.
     *
     * @since 1.5
     * @see MediaObjects#getSortInstant(MediaObject)
     */
    @XmlAttribute(name = "sortDate", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("sortDate")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    public Instant getSortInstant() {
        if (!sortDateValid) {
            Instant date = MediaObjects.getSortInstant(this);
            if (date != null) {
                sortInstant = date;
            }
            sortDateValid = true;
        }
        return sortInstant;
    }


    /**
     *
     * @since 1.5
     * @deprecated Buts still used in FTL which doesn't support {@link Instant} well?
     */
    @Deprecated
    final public Date getSortDate() {
        return DateUtils.toDate(getSortInstant());
    }
    /**
     * Method is needed for unmarshalling. It does nothing. It may do something
     * in overrides (as in {@link Group})
     */
    void setSortInstant(@Nullable Instant date) {
        this.sortInstant = date;
        this.sortDateValid = true;
        this.sortDateInvalidatable = false;

    }

    protected void invalidateSortDate() {
        if (this.sortDateInvalidatable) {
            this.sortDateValid = false;
        }
    }

    /**
     * MediaObjects can be merged. This means that {@link #getMergedTo()} (or {@link #getMergedToRef()}) is non {@code null}
     */
    @Override
    public boolean isMerged() {
        return mergedTo != null || mergedToRef != null;
    }

    @Nullable
    public MediaObject getMergedTo() {
        return mergedTo;
    }

    /**
     * Mark this object as being merged to another mediaobject. This will only set the {@code mergedTo} field.
     * The workflow status will be unaffected. The publisher will pick up that the workflow is not {@link Workflow#MERGED} while
     * there the {@code mergedTo} field is set, and then correct that and republish accordingly.
     *
     * @param mergedTo The destination of the merge.
     * @throws IllegalArgumentException If this mediaobject was already merged to some other object
     */
    public void setMergedTo(@Nullable MediaObject mergedTo) {
        if (this.mergedTo != null && mergedTo != null && !this.mergedTo.equals(mergedTo)) {
            throw new IllegalArgumentException(
                "Can not merge " + this + " to " + mergedTo + " since it is already merged to " + this.mergedTo);
        }

        int depth = 10;
        MediaObject p = mergedTo;
        if (mergedTo != null) {
            while (p.mergedTo != null) {
                if (this.equals(p)) {
                    throw new IllegalArgumentException("Loop while merging source " + this + " to " + mergedTo);
                }
                if (depth-- == 0) {
                    throw new IllegalArgumentException(
                            "Deep regression while merging source " + this + " to " + mergedTo);
                }

                p = p.mergedTo;
            }
        }

        this.mergedTo = p;
    }

    @XmlAttribute(name = "mergedTo")
    @JsonProperty
    public String getMergedToRef() {
        if (mergedToRef != null) {
            return mergedToRef;
        }

        return mergedTo != null ? mergedTo.getMid() : null;
    }

    public void setMergedToRef(@Nullable String mergedToRef) {
        this.mergedToRef = mergedToRef;
    }

    /**
     * This setter is not intended for normal use in code. RepubDate is meant
     * for monitoring the publication delays. It contains the
     * scheduled publication date of this MediaObject. This field is set (in
     * SQL) when republishing descendants, and (in code) when revoking
     * locations/images. When the republication delay reaches a certain value an
     * alert can be raised.
     */
    void setRepubDate(Instant repubDate) {
        this.repubDate = repubDate;
    }

    private void markCeresUpdate() {
        // Shouldn't this check on authoritative?
        if (getPrediction(Platform.INTERNETVOD) != null) {
            locationAuthorityUpdate = true;
        }
    }


    public abstract SubMediaType getType();


    /**
     * @since 5.8
     */
    public abstract void setMediaType(MediaType type);

    /**
     * @since 3.2
     */
    @Override
    @NotNull
    public final MediaType getMediaType() {
        SubMediaType subMediaType = getType();
        return subMediaType == null ? MediaType.getMediaType(this) : subMediaType.getMediaType();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MediaObject mediaObject) {
            return super.equals(o) || equalsOnMid(mediaObject);
        } else {
            return super.equals(o);
        }
    }

    @Override
    public int hashCode() {
        return (id == null && mid != null) ? mid.hashCode() : super.hashCode();
    }

    private boolean equalsOnMid(MediaObject o) {
        return mid != null && mid.equals(o.getMid());
    }

    @SuppressWarnings("unused")
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (predictionsForXml != null) {
            this.predictions = new TreeSet<>(predictionsForXml);
        }
    }

    @Override
    protected abstract String getUrnPrefix();

    // Following are overridden to help FTL and hibernate
    // See https://issues.apache.org/jira/browse/FREEMARKER-24

    /**
     * <p>
     * Overridden to help hibernate search (see MediaSearchMappingFactory)
     * Probably has to to with <a href="https://bugs.openjdk.java.net/browse/JDK-8071693">JDK-8071693</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getMainTitle() {
        return Media.super.getMainTitle();
    }

    /**
     * <p>
     * Overridden to help hibernate search (see MediaSearchMappingFactory)
     * Probably has to to with <a href="https://bugs.openjdk.java.net/browse/JDK-8071693">...</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getSubTitle() {
        return Media.super.getSubTitle();
    }

    /**
     * <p>
     * Overridden to help hibernate search (see MediaSearchMappingFactory)
     * Probably has to to with <a href="https://bugs.openjdk.java.net/browse/JDK-8071693">...</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getMainDescription() {
        return Media.super.getMainDescription();
    }

    /**
     * <p>
     * Overridden to help FTL. See
     * <a href="https://issues.apache.org/jira/browse/FREEMARKER-24">FREEMARKER-24</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getShortTitle() {
        return Media.super.getShortTitle();
    }

    /**
     * <p>
     * Overridden to help FTL. See
     * <a href="https://issues.apache.org/jira/browse/FREEMARKER-24">FREEMARKER-24</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getOriginalTitle() {
        return Media.super.getOriginalTitle();
    }

    /**
     * <p>
     * Overridden to help FTL. See
     * <a href="https://issues.apache.org/jira/browse/FREEMARKER-24">FREEMARKER-24</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getWorkTitle() {
        return Media.super.getWorkTitle();
    }

    /**
     * <p>
     * Overridden to help FTL. See
     * <a href="https://issues.apache.org/jira/browse/FREEMARKER-24">FREEMARKER-24</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getLexicoTitle() {
        return Media.super.getLexicoTitle();
    }

    /**
     * <p>
     * Overridden to help FTL. See
     * <a href="https://issues.apache.org/jira/browse/FREEMARKER-24">FREEMARKER-24</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviatedTitle() {
        return Media.super.getAbbreviatedTitle();
    }

    /**
     * <p>
     * Overridden to help FTL. See
     * <a href="https://issues.apache.org/jira/browse/FREEMARKER-24">FREEMARKER-24</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getSubDescription() {
        return Media.super.getSubDescription();
    }

    /**
     * <p>
     * Overridden to help FTL. See
     * <a href="https://issues.apache.org/jira/browse/FREEMARKER-24">FREEMARKER-24</a>
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return Media.super.getShortDescription();
    }

    public void mergeImages(MediaObject incoming, OwnerType owner) {
        int index = 0;
        for (Image i: incoming.getImages()) {
            if (images == null) {
                images = new ArrayList<>();
            }
            if (Objects.equals(i.getOwner(), owner)) {
                int currentIndex = CollectionUtils.indexOf(images, i, index);
                if (currentIndex == -1) {
                    // not found, add it
                    i.setParent(this);
                    images.add(index, i);
                    // it will grow one now,
                } else {
                    // found so it exists at currentIndex
                    assert index < images.size() : index + ":" + images + " " + incoming.getImages();
                    assert currentIndex < images.size() : currentIndex  + ":" + images + " " + incoming.getImages();

                    Collections.swap(images, index, currentIndex);
                    Image existing = images.get(index);
                    if (existing == null) {
                        log.warn("Found  null in {}", images);
                        images.set(index, i);
                    } else {
                        existing.copyFrom(i);
                        if (existing.getCrids() == null || i.getCrids() == null) {
                            existing.setCrids(i.getCrids());
                        } else {
                            existing.getCrids().removeIf(c -> ! i.getCrids().contains(c));
                            List<String> copy = new ArrayList<>(i.getCrids());
                            copy.removeIf(c -> existing.getCrids().contains(c));
                            existing.getCrids().addAll(copy);
                        }
                    }
                }
                index++;
            } else {
                log.debug("Oddly, incoming image {} with different owner, will be ignored", i);
            }
        }
        if (images != null) {
            List<Image> toRemove = images
                .stream()
                .filter(i -> Objects.equals(i.getOwner(), owner))
                .filter(i -> !incoming.getImages().contains(i))
                .toList();

            toRemove.forEach(this::removeImage);
        }


    }

    public void addAllImages(List<Image> imgs) {
        imgs.removeIf(Objects::isNull);
        imgs.forEach(img -> img.setParent(this));
        images.addAll(imgs);
    }

    public void removeImages() {
        getImages().forEach(img -> {
            img.setParent(null);
            img.getCrids().clear();
        });
        images.clear();
    }

    private Image addOrUpdate(Image img) {
        Image existing = this.getImage(img);
        if (existing != null) {
            if (existing.getOwner() != img.getOwner()) {
                log.info("Copying from different owner {} <- {}", existing, img);
            }
            existing.copyFrom(img);
            existing.setCrids(img.getCrids());
            return existing;
        } else {
            addImage(img);
            return img;
        }
    }

    @Override
    // Overridden to give access to test
    protected byte[] serializeForCalcCRC32() {
        return super.serializeForCalcCRC32();
    }

    @Override
    protected CRC32 calcCRC32() {
        CRC32 result = super.calcCRC32();
        // Some fields not appearing in XML, but which _are_ relevant changess
        if (streamingPlatformStatus != null) {
            streamingPlatformStatus.calcCRC32(result);
        }
        return result;
    }


    @Override
    public final Correlation getCorrelation() {
        if (correlationId == null) {
            Correlation cor = calcCorrelation();
            if (cor.type == Correlation.Type.MID) {
                correlationId = calcCorrelation().id;
            } else {
                return cor;
            }
        }
        return Correlation.mid(correlationId);
    }


    protected Correlation calcCorrelation() {
        return Media.super.getCorrelation();
    }


    @Override
    @NonNull
    public final String toString() {
        String mainTitle;
        try {
            String mt = getMainTitle();
            mainTitle = mt == null ? "<no title>" : ('"' + mt + '"');
        } catch(RuntimeException le) {
            mainTitle = "[" + le.getClass() + " " + le.getMessage() + "]"; // (could be a LazyInitializationException)
        }
        String id;
        if (this.isPersisted()) {
            id = ", id=" + this.getId();
        } else {
            Long thisId = this.getId();
            if (thisId != null) {
                id = ", id=[" + this.getId() + "]"; // bracket then signal that not yet persistent
            } else {
                if (Workflow.API.contains(workflow)) {
                    // probably testing ES or so.
                    id = "";
                } else {
                    id = " (not persistent)";
                }
            }
        }
        return String.format(getClass().getSimpleName() + "{%1$s%2$smid=%3$s, title=%4$s%5$s}",
            (! PUBLICATIONS.contains(workflow) ? workflow + ":" : "" ),
            getType() == null ? "" : getType() + " ",
            this.getMid() == null ? ("<no mid @" + superHashCode() + ">") : "\"" + this.getMid() + "\"",
            mainTitle,
            id
            );
    }

}
