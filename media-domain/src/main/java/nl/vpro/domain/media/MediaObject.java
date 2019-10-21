/*
 * Copyright (C) 2008 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.CRC32;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.meeuw.i18n.RegionService;
import org.meeuw.i18n.countries.Country;
import org.meeuw.i18n.countries.validation.ValidCountry;
import org.meeuw.i18n.persistence.RegionToStringConverter;
import org.meeuw.i18n.validation.Language;
import org.meeuw.i18n.validation.ValidRegion;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.CountryCode;

import nl.vpro.domain.*;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.bind.*;
import nl.vpro.domain.media.exceptions.CircularReferenceException;
import nl.vpro.domain.media.exceptions.ModificationException;
import nl.vpro.domain.media.support.Description;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.user.*;
import nl.vpro.i18n.Locales;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.jackson2.Views;
import nl.vpro.nicam.NicamRated;
import nl.vpro.util.*;
import nl.vpro.validation.*;
import nl.vpro.xml.bind.FalseToNullAdapter;
import nl.vpro.xml.bind.InstantXmlAdapter;

import static javax.persistence.CascadeType.ALL;
import static nl.vpro.domain.TextualObjects.sorted;
import static nl.vpro.domain.media.MediaObject.*;
import static nl.vpro.domain.media.support.MediaObjectOwnableLists.createIfNull;
import static nl.vpro.domain.media.support.OwnableLists.containsDuplicateOwner;

/**
 * Base objects for programs and groups
 *
 * @author roekoe
 */
@SuppressWarnings("WSReferenceInspection")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Cacheable
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
        "locations",
        "_Relations",
        "images"
    })

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
@JsonSubTypes({ @JsonSubTypes.Type(value = Program.class, name = "program"),
    @JsonSubTypes.Type(value = Group.class, name = "group"),
    @JsonSubTypes.Type(value = Segment.class, name = "segment") })

// Improvement: Filters can be defined in hibernate-mapping in the hibernate-config.xml
// See https://docs.jboss.org/hibernate/orm/5.0/manual/en-US/html/ch19.html
@FilterDefs({ @FilterDef(name = "titleFilter", parameters = { @ParamDef(name = "title", type = "string") }),
    @FilterDef(name = "typeFilter", parameters = { @ParamDef(name = "types", type = "string"),
        @ParamDef(name = "segments", type = "boolean") }),
    @FilterDef(name = "organizationFilter", parameters = { @ParamDef(name = "organizations", type = "string") }),
    @FilterDef(name = "noBroadcast"), @FilterDef(name = "hasLocations"), @FilterDef(name = "noPlaylist"),
    @FilterDef(name = "eventRange", parameters = { @ParamDef(name = "eventStart", type = "date"),
        @ParamDef(name = "eventStop", type = "date") }),
    @FilterDef(name = "creationRange", parameters = { @ParamDef(name = "creationStart", type = "date"),
        @ParamDef(name = "creationStop", type = "date") }),
    @FilterDef(name = "modifiedRange", parameters = { @ParamDef(name = "modifiedStart", type = "date"),
        @ParamDef(name = "modifiedStop", type = "date") }),
    @FilterDef(name = PUBLICATION_FILTER),
    @FilterDef(name = EMBARGO_FILTER, parameters = {
        @ParamDef(name = "broadcasters", type = "string") }),
    @FilterDef(name = DELETED_FILTER),
    @FilterDef(name = "relationFilter", parameters = { @ParamDef(name = "broadcasters", type = "string") }) })
@Filters({
    @Filter(name = "titleFilter", condition = "0 < (select count(*) from title t where t.parent_id = id and lower(t.title) like :title)"),
    @Filter(name = "typeFilter", condition = "(0 < (select count(*) from program p where p.id = id and p.type in (:types)))"
        + " or (0 < (select count(*) from group_table g where g.id = id and g.type in (:types)))"
        + " or (:segments and 0 < (select count(*) from segment s where s.id = id))"),
    @Filter(name = "organizationFilter", condition = "0 < ("
        + "(select count(*) from mediaobject_portal o where o.mediaobject_id = id and o.portals_id in (:organizations))"
        + " + "
        + "(select count(*) from mediaobject_broadcaster o where o.mediaobject_id = id and o.broadcasters_id in (:organizations))"
        + " + "
        + "(select count(*) from mediaobject_thirdparty o where o.mediaobject_id = id and o.thirdparties_id in (:organizations))"
        + ")"),
    @Filter(name = "noBroadcast", condition = "0 = (select count(*) from scheduleevent e where e.mediaobject_id = id)"),
    @Filter(name = "hasLocations", condition = "0 < (select count(*) from location l where l.mediaobject_nid = id)"),
    @Filter(name = "noPlaylist", condition = "0 = (select count(*) from group_table g, memberref mr where mr.member_id = id "
        + "and g.id = mr.owner_id " + "and g.type = 'PLAYLIST')"),
    @Filter(name = "eventRange", condition = ":eventStart <= (select min(e.start) from scheduleevent e where e.mediaobject_id = id) and "
        + ":eventStop >= (select min(e.start) from scheduleevent e where e.mediaobject_id = id)"),
    @Filter(name = "creationRange", condition = ":creationStart <= creationDate and :creationStop >= creationDate"),
    @Filter(name = "modifiedRange", condition = ":modifiedStart <= lastModified and :modifiedStop >= lastModified"),
    @Filter(name = PUBLICATION_FILTER, condition = "(publishStart is null or publishStart <= now()) "
        + "and (publishStop is null or publishStop > now())"),
    @Filter(name = EMBARGO_FILTER, condition = "(publishstart is null "
        + "or publishstart < now() " + "or (select p.type from program p where p.id = id) != 'CLIP' "
        + "or (0 < (select count(*) from mediaobject_broadcaster o where o.mediaobject_id = id and o.broadcasters_id in (:broadcasters))))"),
    @Filter(name = DELETED_FILTER, condition = "(workflow NOT IN ('MERGED', 'FOR_DELETION', 'DELETED') and mergedTo_id is null)") })

@Slf4j
public abstract class MediaObject
    extends
    PublishableObject<MediaObject>
    implements
    NicamRated,
    LocalizedObject<Title, Description, Website, TwitterRef, MediaObject>,
    TrackableMedia, MediaIdentifiable {


    public static final String DELETED_FILTER = "deletedFilter";
    public static final String INVERSE_DELETED_FILTER = "inverseDeletedFilter";
    public static final String PUBLICATION_FILTER = "publicationFilter";
    public static final String INVERSE_PUBLICATION_FILTER = "inversePublicationFilter";
    public static final String EMBARGO_FILTER = "embargoFilter";
    public static final String INVERSE_EMBARGO_FILTER = "inverseEmbargoFilter";

    @Column(name = "mid", nullable = false, unique = true)
    @Size.List({ @Size(max = 255), @Size(min = 4) })
    @Pattern(
        regexp = "^[a-zA-Z0-9][ .a-zA-Z0-9_-]*$",
        flags = {
            Pattern.Flag.CASE_INSENSITIVE }, message = "{nl.vpro.constraints.mid}")
    protected String mid;

    //@Version
    @Transient // Remove for MSE-3753
    @Getter
    protected Integer version;

    @ElementCollection
    @Column(name = "crids", nullable = false, unique = true) // TODO, rename to 'crid'.
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    // Improvement: cache configuration can be put in a hibernate-config.xml. See
    // https://docs.jboss.org/hibernate/orm/4.0/devguide/en-US/html/ch06.html
    @StringList(maxLength = 255)
    protected List<@NotNull @CRID  String> crids;

    @ManyToMany
    @OrderColumn(name = "list_index",
        nullable = false)
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Size(min = 0, message = "{nl.vpro.constraints.Size.min}") // komt soms voor
                                                                // bij imports.
    protected List<@NotNull Broadcaster> broadcasters;

    @ManyToMany
    @OrderColumn(name = "list_index", nullable = false)
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<@NotNull  Portal> portals;

    @ManyToMany
    @OrderColumn(name = "list_index", nullable = false)
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<@NotNull  ThirdParty> thirdParties;

    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition = "(start is null or start <= now()) "
            + "and (stop is null or stop > now())")
    @Valid
    protected List<@NotNull PortalRestriction> portalRestrictions;

    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "mediaobject_id")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition = "(start is null or start <= now()) "
            + "and (stop is null or stop > now())")
    @Valid
    protected Set<@NotNull GeoRestriction> geoRestrictions;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade = {ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    // @NotNull(message = "titles: {nl.vpro.constraints.NotNull}") // Somewhy
    // hibernates on merge first merges an object without titles.
    @Size.List({ @Size(min = 1, message = "{nl.vpro.constraints.collection.Size.min}"), })
    @Valid
    protected Set<@NotNull Title> titles;

    @OneToMany(mappedBy = "parent", orphanRemoval = true, cascade=ALL)
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected Set<@NotNull Description> descriptions;

    @ManyToMany(cascade = {ALL})
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected Set<@NotNull Genre> genres;

    @ManyToMany(cascade = {ALL})
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    @JoinTable(foreignKey = @ForeignKey(name = "fk_mediaobject_tag__mediaobject"), inverseForeignKey = @ForeignKey(name = "fk_mediaobject_tag__tag"))
    protected Set<@NotNull Tag> tags;


    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "parent_id")
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    @XmlElement(name = "intentions")
    @JsonProperty("intentions")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @SortNatural
    protected SortedSet<@NotNull Intentions> intentions;

    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "parent_id")
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    @XmlElement
    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @SortNatural
    protected SortedSet<@NotNull TargetGroups> targetGroups;

    protected String source;

    @ElementCollection
    @Column(length = 10)
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Convert(converter = RegionToStringConverter.class)
    protected List<
        // valid are countries (further validated by @ValidCountry), and a list of codes.
        org.meeuw.i18n.
        @ValidRegion(classes = {Country.class}, includes = {"GB-ENG", "GB-NIR", "GB-SCT", "GB-WLS"})
        @ValidCountry(value = ValidCountry.OFFICIAL | ValidCountry.USER_ASSIGNED | ValidCountry.FORMER, excludes = {"XN"})
        @NotNull Region> countries;


    @ElementCollection
    @Column(length = 10)
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected List<
        @ValidRegion(classes = {Country.class})
        @ValidCountry(value = ValidCountry.OFFICIAL | ValidCountry.USER_ASSIGNED | ValidCountry.FORMER, excludes = {"XN"})
        @Language(mayContainCountry = true)
        @NotNull Locale> languages;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "avType: {nl.vpro.constraints.NotNull}")
    protected AVType avType = null;

    @OneToOne(orphanRemoval = true, cascade = {ALL})
    protected AVAttributes avAttributes;

    @Column(name = "releaseDate")
    protected Short releaseYear;

    @Embedded
    @Valid
    protected AuthorizedDuration duration;

    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "mediaobject_id")
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected List<@NotNull Person> credits;

    @OneToMany(orphanRemoval = true, cascade = ALL)
    @JoinColumn(name = "parent_id")
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    @XmlElement(name = "geoLocations")
    @JsonProperty("geoLocations")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @SortNatural
    protected SortedSet<@NotNull GeoLocations> geoLocations;

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

    // Improvement: These filters are EXTREMELY HORRIBLE, actually UNACCEPTABLE

    // Before hiberante 5.2 we used Filter rather then FilterJoinTable.
    // It doesn't really make much sense.
    @FilterJoinTables({
            @FilterJoinTable(name = PUBLICATION_FILTER, condition = "(" + "(mediaobjec2_.mergedTo_id is null) and " + // MSE-3526                 // ?
                    "(mediaobjec2_.publishstart is null or mediaobjec2_.publishstart < now()) and "
                    + "(mediaobjec2_.publishstop is null or mediaobjec2_.publishstop > now())" + ")"),
            @FilterJoinTable(name = EMBARGO_FILTER, condition = "(mediaobjec2_2_.type != 'CLIP' "
                    + "or mediaobjec2_.publishstart is null " + "or mediaobjec2_.publishstart < now() "
                    + "or 0 < (select count(*) from mediaobject_broadcaster o where o.mediaobject_id = mediaobjec2_.id and o.broadcasters_id in (:broadcasters)))"),
            @FilterJoinTable(name = DELETED_FILTER, condition = "(mediaobjec2_.workflow NOT IN ('FOR_DELETION', 'DELETED') and (mediaobjec2_.mergedTo_id is null))") })
    @Valid
    protected Set<@NotNull MemberRef> memberOf;

    @SuppressWarnings("NullableProblems")
    @Enumerated(EnumType.STRING)
    @NotNull(groups = { WarningValidatorGroup.class })
    protected AgeRating ageRating;

    @ElementCollection
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Enumerated(value = EnumType.STRING)
    protected List<@NotNull ContentRating> contentRatings;

    @ElementCollection
    @OrderColumn(name = "list_index", nullable = false)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @StringList(maxLength = 255)
    protected List<
        @NotNull
        @Email(message = "{nl.vpro.constraints.Email.message}", groups = {}) String> email;

    @OneToMany(targetEntity = Website.class, orphanRemoval = true, cascade = {ALL})
    @JoinColumn(name = "mediaobject_id", nullable = true)
    // not nullable media/index blocks ordering updates on the collection
    @OrderColumn(name = "list_index",
        nullable = true // Did I mention that hibernate sucks?
    )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected List<@NotNull Website> websites;

    @OneToMany(cascade = ALL, targetEntity = TwitterRef.class, orphanRemoval = true)
    @JoinColumn(name = "mediaobject_id", nullable = true)
    // not nullable media/index blocks ordering updates on the collection
    @OrderColumn(name = "list_index",
        nullable = true // hibernate sucks
    )
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Valid
    protected List<@NotNull TwitterRef> twitterRefs;

    protected Short teletext;

    @XmlElement
    @Setter
    protected Boolean isDubbed;

    @OneToMany(orphanRemoval = true, mappedBy = "mediaObject", cascade={ALL})
    @Valid
    protected Set<Prediction> predictions;

    @Transient
    List<Prediction> predictionsForXml;

    @OneToMany(cascade = ALL, mappedBy = "mediaObject", orphanRemoval = true)
    @SortNatural
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition =
        "workflow != 'DELETED' and ( "
            + "(platform is null and  (publishStart is null or publishStart <= now())  and (publishStop is null or publishStop > now())) "
            + " or "
            + " ( not(platform is null)  " + "   and ( "
            + "        select count(*) from prediction c where c.platform = platform and c.mediaobject_id = mediaobject_id and "
            + "              (c.publishStart is null or c.publishStart <= now()) and (c.publishStop is null or c.publishStop > now()) "
            + "       ) > 0)"
            + ")"

    )
    @Valid
    @XmlElementWrapper(name = "locations")
    @XmlElement(name = "location")
    @JsonProperty("locations")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    protected SortedSet<@NotNull Location> locations = new TreeSet<>();


    @OneToMany(orphanRemoval = true, cascade= {ALL})
    @JoinColumn(name = "mediaobject_id", updatable = false, nullable = false)
    @SortNatural
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    protected Set<@NotNull Relation> relations;

    @OneToMany(
        orphanRemoval = true,
            mappedBy = "mediaObject",
            cascade = {ALL}
    )
    @OrderColumn(
        name = "list_index",
        nullable = true // hibernate sucks
    )
    @Valid
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @Filter(name = PUBLICATION_FILTER, condition = "(publishStart is null or publishStart <= now()) "
            + "and (publishStop is null or publishStop > now())")
    // @Field(name = "images", store=Store.YES, analyze = Analyze.NO,
    // bridge = @FieldBridge(impl = JsonBridge.class, params = @Parameter(name =
    // "class", value = "[Lnl.vpro.domain.media.support.Image;")))
    protected List<@NotNull Image> images;

    @Column(nullable = false)
    @JsonIgnore // Oh Jackson2...
    protected boolean isEmbeddable = true;

    // The sortDate field is actually calculatable, but it can be a bit
    // expensive, so we cache its value in a persistent field.
    @Column(name = "sortdate", nullable = true, unique = false)
    protected Instant sortInstant;

    // Used for monitoring publication delay. Not exposed via java.
    // Set its value in sql to now() when unmodified media is republished.
    @Column(name = "repubDate", nullable = true, unique = false)
    protected Instant repubDate;

    @Column
    @XmlTransient
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected String repubReason;

    @Column
    @XmlTransient
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected String repubDestinations;

    @Column(nullable = false)
    @JsonIgnore // Oh Jackson2...
    private Boolean locationAuthorityUpdate = false;

    @OneToOne
    private MediaObject mergedTo;

     // Holds the descendantOf value when unmarshalled from XML. Used by XML
    // clients working in a detached environment.
    @Transient
    private String mergedToRef;

    // Holds the descendantOf value when unmarshalled from XML. Used by XML
    // clients working in a detached environment.
    @Transient
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
    private AvailableSubtitlesWorkflow subtitlesWorkflow = AvailableSubtitlesWorkflow.FOR_PUBLICATION;

    @ElementCollection(fetch = FetchType.EAGER)
    // it is needed for every persist and display (because of hasSubtitles), so lets fetch it eager
    // also we got odd NPE's from PersistentBag otherwise.
    @CollectionTable(name = "Subtitles", joinColumns = @JoinColumn(name = "mid", referencedColumnName = "mid"))
    @Setter
    private List<AvailableSubtitles> availableSubtitles;



    @Embedded()
    @XmlTransient
    @Setter(AccessLevel.PACKAGE)
    private StreamingStatusImpl streamingPlatformStatus = StreamingStatus.unset();

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
        source.getCredits().forEach(person -> this.addPerson(Person.copy(person, this)));
        source.getAwards().forEach(this::addAward);
        source.getMemberOf().forEach(ref -> this.createMemberOf(ref.getGroup(), ref.getNumber(), ref.getOwner()));
        this.ageRating = source.ageRating;
        source.getContentRatings().forEach(this::addContentRating);
        source.getEmail().forEach(this::addEmail);
        source.getWebsites().forEach(website -> this.addWebsite(Website.copy(website)));
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

    public static Long idFromUrn(String urn) {
        final String id = urn.substring(urn.lastIndexOf(':') + 1);
        return Long.valueOf(id);
    }

    protected static <T> List<T> updateList(List<T> toUpdate, Collection<T> values) {
        if (toUpdate != null && toUpdate == values) {
            return toUpdate;
        }
        if (toUpdate == null) {
            toUpdate = new ArrayList<>();
        } else {
            if (toUpdate.equals(values)) {
                // the object is already exactly correct, do nothing
                return toUpdate;
            }
            toUpdate.clear();
        }
        if (values != null) {
            toUpdate.addAll(values);
        }
        return toUpdate;
    }

    @SuppressWarnings("unchecked")
    protected static <T extends Comparable<?>> Set<T> updateSortedSet(Set<T> toUpdate, Collection<T> values) {
        if (toUpdate != null && toUpdate == values) {
            return toUpdate;
        }
        if (toUpdate == null) {
            toUpdate = new TreeSet<>();
            if (values != null) {
                toUpdate.addAll(values);
            }

        } else {
            if (values != null) {
                toUpdate.retainAll(values);
                for (T v : values) {
                    for (T toUpdateValue : toUpdate) {
                        if (toUpdateValue instanceof Updatable && toUpdateValue.equals(v)) {
                            ((Updatable) toUpdateValue).update(v);
                        }
                    }
                }
                toUpdate.addAll(values);
            }
        }
        return toUpdate;
    }

    protected static <E> E getFromList(List<E> list) {
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @XmlAttribute(required = true)
    @Override
    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        if (mid != null && mid.length() == 0) {
            mid = null;
        }
        if (this.mid != null && mid != null && !this.mid.equals(mid)) {
            throw new IllegalArgumentException(
                    "Not allowed to assign new value to MID (current = " + this.mid + ", new = " + mid + ")");
        }
        this.mid = mid;
    }

    /**
     * Return the available subtitles. These subtitles may not be published.
     *
     * In the publisher this list is explicely cleared before publishing to the API if there are no published locations
     * This is kind of a hack, may be it is better to have the workflow in AvailableSubtitles also.
     * @return
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

    public void setCrids(List<String> crids) {
        this.crids = crids;
    }

    public MediaObject addCrid(String crid) {
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

    public void setBroadcasters(List<Broadcaster> broadcasters) {
        this.broadcasters = broadcasters;
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

    public void setPortalRestrictions(List<PortalRestriction> portalRestrictions) {
        this.portalRestrictions = portalRestrictions;
    }

    public boolean removePortalRestriction(PortalRestriction restriction) {
        if (this.portalRestrictions != null) {
            return this.portalRestrictions.remove(restriction);
        }
        return false;
    }

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

    public void setGeoRestrictions(Set<GeoRestriction> geoRestrictions) {
        this.geoRestrictions = geoRestrictions;
    }

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
     * Experimental. For NPA-403, to provide to ES the needed mapping.
     */
    @JsonView({Views.Publisher.class})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public SortedSet<Title> getExpandedTitles() {
        return TextualObjects.expandTitlesMajorOwnerTypes(this);
    }


    @Override
    public MediaObject addTitle(Title title) {
        this.titles = addTo(titles, title);
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
        this.descriptions = addTo(descriptions, description);
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
        MediaObjectOwnableLists.set(this, getGeoLocations(), newGeoLocations);
    }


    @JsonView({Views.Publisher.class})
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public SortedSet<GeoLocations>  getExpandedGeoLocations() {
        return MediaObjectOwnableLists.expandOwnedList(this.geoLocations,
                (owner, values) -> GeoLocations.builder().values(values).owner(owner).build(),
                OwnerType.ENTRIES
        );
    }

    //end region

    //region Intentions logic

    public SortedSet<Intentions> getIntentions() {
        return intentions;
    }

    @JsonView({Views.Publisher.class})
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

    public void setIntentions(SortedSet<Intentions> newIntentions) {
        if (newIntentions == null) {
            this.intentions = null;
            return;
        }

        if (containsDuplicateOwner(newIntentions)) {
            throw new IllegalArgumentException("The list you want to set has a duplicate owner: " + newIntentions);
        }
        if (this.intentions == null) {
            this.intentions = new TreeSet<>();
        } else {
           this.intentions.clear();
        }
        for (Intentions i : newIntentions) {
            addIntention(i.clone());
        }
    }

    public MediaObject addIntention(@NonNull Intentions newIntentions) {
        if(this.intentions != null) {
            this.intentions.removeIf(existing -> existing.getOwner() == newIntentions.getOwner());
        } else {
            this.intentions = new TreeSet<>();
        }
        newIntentions.setParent(this);
        this.intentions.add(newIntentions);
        return this;
    }

    public boolean removeIntention(Intentions intentions) {
        return this.intentions.remove(intentions);
    }

    //endregion


    public SortedSet<TargetGroups> getTargetGroups() {
        return targetGroups;
    }


    @JsonView({Views.Publisher.class})
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

    public void setTargetGroups(SortedSet<TargetGroups> newTargetGroups) {

        if (newTargetGroups == null){
            this.targetGroups = null;
            return ;
        }
        if (containsDuplicateOwner(newTargetGroups)) {
           throw new IllegalArgumentException("The targetgroup list you want to set has a duplicate owner: " + newTargetGroups);
        }
        if (this.targetGroups == null) {
            this.targetGroups = new TreeSet<>();
        } else {
           this.targetGroups.clear();
        }
        for (TargetGroups i : newTargetGroups) {
            addTargetGroups(i.clone());
        }
    }

    public MediaObject addTargetGroups(@NonNull TargetGroups newTargetGroups) {
        if(this.targetGroups != null) {
            targetGroups.removeIf(existing -> existing.getOwner() == newTargetGroups.getOwner());
        } else {
            this.targetGroups = new TreeSet<>();
        }
        newTargetGroups.setParent(this);
        targetGroups.add(newTargetGroups);
        return this;
    }

    @XmlElement
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @XmlElement(name = "country")
    @JsonProperty("countries")
    @JsonSerialize(using = CountryCodeList.Serializer.class)
    @JsonDeserialize(using = CountryCodeList.Deserializer.class)
    @XmlJavaTypeAdapter(value = CountryCodeAdapter.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<org.meeuw.i18n.Region> getCountries() {
        if (countries == null) {
            countries = new ArrayList<>();
        }
        return countries;
    }

    public void setCountries(List<org.meeuw.i18n.Region> countries) {
        this.countries = updateList(this.countries, countries);
    }

    public MediaObject addCountry(String code) {
        return addCountry(RegionService.getInstance().getByCode(code).orElseThrow(() ->
            new IllegalArgumentException("Unknown country " + code)));

    }
    public MediaObject addCountry(@NonNull CountryCode country) {
        return addCountry(Country.of(country));
    }

    public MediaObject addCountry(@lombok.NonNull org.meeuw.i18n.Region country) {
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

    public void setAvAttributes(AVAttributes avAttributes) {
        this.avAttributes = avAttributes;
    }

    @XmlElement()
    public Short getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Short releaseYear) {
        this.releaseYear = releaseYear;
    }

    @XmlElement()
    public AuthorizedDuration getDuration() {
        return duration;
    }

    void setDuration(AuthorizedDuration duration) {
        this.duration = duration;
    }


    /**
     * Use {@link AuthorizedDuration#get} in combination with {@link #getDuration} to get the java.time.Duration
     * @throws ModificationException If you may not set the duration
     */
    @JsonIgnore
    @XmlTransient
    public void setDuration(java.time.Duration duration) throws ModificationException {
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

    @Deprecated
    public void setDurationWithDate(Date duration) throws ModificationException {
        Date oldDuration = AuthorizedDuration.asDate(this.duration);
        if (ObjectUtils.notEqual(oldDuration, duration) && hasAuthorizedDuration()) {
            throw new ModificationException("Updating an existing and authorized duration is not allowed");
        }

        if (duration == null) {
            this.duration = null;
        } else if (this.duration == null) {
            this.duration = new AuthorizedDuration(duration);
        } else {
            this.duration.setValue(duration);
        }

    }

    @Deprecated
    public Date getDurationAsDate() {
        return AuthorizedDuration.asDate(duration);
    }

    public boolean hasAuthorizedDuration() {
        return duration != null && duration.isAuthorized();
    }

    @XmlElementWrapper(name = "credits")
    @XmlElement(name = "person")
    @JsonProperty("credits")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Person> getCredits() {
        if (credits == null) {
            credits = new ArrayList<>();
        }
        return credits;
    }

    public void setCredits(@Nullable List<Person> persons) {
        if (persons != null) {
            for (Person person : persons) {
                person.setParent(this);
            }
        }

        this.credits = updateList(this.credits, persons);
    }

    /**
     * @deprecated Use {@link #getCredits}
     */
    @Deprecated
    public List<Person> getPersons() {
        return getCredits();
    }
     /**
     * @deprecated Use {@link #getCredits}
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

        for (Person person : credits) {
            if (id.equals(person.getId())) {
                return removePerson(person);
            }
        }

        return false;
    }

    public MediaObject addPerson(Person person) {
        if (credits == null) {
            credits = new ArrayList<>();
        }

        if (!credits.contains(person)) {
            if (person != null) {
                person.setParent(this);
                person.setListIndex(credits.size());
                credits.add(person);
            }
        }

        return this;
    }

    public Person findPerson(Person person) {
        if (credits == null) {
            return null;
        }

        for (Person p : credits) {
            if (p.equals(person)) {
                return p;
            }
        }

        return null;
    }

    public Person findPerson(Long id) {
        if (credits == null) {
            return null;
        }

        for (Person p : credits) {
            if (p.getId().equals(id)) {
                return p;
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
                .map(DescendantRef::of).collect(Collectors.toList()));
        }
        return sorted(descendantOf);
    }

    void setDescendantOf(Set<DescendantRef> descendantOf) {
        this.descendantOf = updateSortedSet(this.descendantOf, descendantOf);
    }

    @XmlElement()
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<MemberRef> getMemberOf() {
        if (memberOf == null) {
            memberOf = new TreeSet<>();
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
                this.memberOf.add(MemberRef.copy(ref, this));
            }
        }
    }

    public boolean isMember() {
        return memberOf != null && memberOf.size() > 0;
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

    public MemberRef findMemberOfRef(MediaObject owner) {
        for (MemberRef memberRef : memberOf) {
            if (owner.equals(memberRef.getGroup())) {
                return memberRef;
            }
        }
        return null;
    }

    public MemberRef findMemberOfRef(MediaObject owner, Integer number) {
        if (memberOf == null) {
            return null;
        }

        for (MemberRef memberRef : memberOf) {
            if (memberRef != null) {
                if (owner.equals(memberRef.getGroup())) {
                    if (number == null && memberRef.getNumber() == null
                            || number != null && number.equals(memberRef.getNumber())) {

                        return memberRef;
                    }
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

        if (this.equals(member) || this.hasAncestor(member)) {
            throw new CircularReferenceException(this, findAncestry(member));
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

                if (memberRef.getGroup().equals(reference)) {
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
    public AgeRating getAgeRating() {
        return ageRating;
    }

    public void setAgeRating(@NonNull  AgeRating ageRating) {
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
    public List<String> getEmail() {
        if (email == null) {
            email = new ArrayList<>();
        }
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = updateList(this.email, email);
    }

    public String getMainEmail() {
        return getFromList(email);
    }

    public MediaObject addEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return this;
        }

        if (this.email == null) {
            this.email = new ArrayList<>();
        }

        email = email.trim();
        if (!this.email.contains(email)) {
            this.email.add(email);
        }

        return this;
    }

    @Override
    @XmlElement(name = "website")
    @JsonProperty("websites")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Website> getWebsites() {
        if (websites == null) {
            websites = new ArrayList<>();
        }
        return websites;
    }

    @Override
    public MediaObject setWebsites(List<Website> websites) {
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
    public void setTwitterRefs(List<TwitterRef> twitterRefs) {
        this.twitterRefs = updateList(this.twitterRefs, twitterRefs);
    }

    @XmlElement()
    public Short getTeletext() {
        return teletext;
    }

    public void setTeletext(Short teletext) {
        this.teletext = teletext;
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
        try {
            List<AvailableSubtitles> list = getAvailableSubtitles();

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
            if (memberRef.getGroup().equals(ancestor) || (memberRef.getMidRef() != null && memberRef.getMidRef().equals(ancestor.getMid())) || memberRef.getGroup().hasAncestor(ancestor)) {
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
        List<MediaObject> ancestry = new ArrayList<>();
        findAncestry(ancestor, ancestry);
        return ancestry;
    }

    protected void findAncestry(MediaObject ancestor, List<MediaObject> ancestors) {
        if (isMember()) {
            for (MemberRef memberRef : memberOf) {
                if (memberRef.getGroup().equals(ancestor)) {
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
                        // happens to be descendant of
                        // it self
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
     * Returns (a copy of, since you have no bussines setting it) the {@link StreamingStatus}.
     * @since 5.11
     */
    public StreamingStatus getStreamingPlatformStatus() {
        return StreamingStatus.copy(streamingPlatformStatus);
    }

    protected StreamingStatusImpl getModifiableStreamingPlatformStatus() {
        return streamingPlatformStatus;
    }
    @XmlTransient
    public SortedSet<Prediction> getPredictions() {
        if (predictions == null) {
            predictions = new TreeSet<>();
        }

        // SEE https://jira.vpro.nl/browse/MSE-2313
        return new SortedSetSameElementWrapper<Prediction>(sorted(predictions)) {
            @Override
            protected Prediction adapt(Prediction prediction) {
                prediction.setParent(MediaObject.this);
                if (prediction.getState() == Prediction.State.ANNOUNCED) {
                    for (Location location : MediaObject.this.getLocations()) {
                        if (location.getPlatform() == prediction.getPlatform()
                                && Workflow.PUBLICATIONS.contains(location.getWorkflow())
                                && prediction.inPublicationWindow(Instant.now())) {
                            log.info("Silentely set state of {} to REALIZED (by {}) of object {}", prediction,
                                    location.getProgramUrl(), MediaObject.this.mid);
                            prediction.setState(Prediction.State.REALIZED);
                            MediaObjects.markForRepublication(MediaObject.this, "realized prediction");
                            break;
                        }
                    }
                }
                return prediction;

            }
        };
    }

    public void setPredictions(Collection<Prediction> predictions) {
        this.predictions = updateSortedSet(this.predictions, predictions);
        this.predictionsForXml = null;
    }


    @XmlElement(name = "prediction")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonProperty("predictions")
    protected List<Prediction> getPredictionsForXml() {
        if (predictionsForXml == null) {
            predictionsForXml = getPredictions().stream()
                .filter(Prediction::isPlannedAvailability)
                .collect(Collectors.toList());
        }
        return predictionsForXml;
    }

    protected void setPredictionsForXml(List<Prediction> predictions) {
        // called by jackson
        this.predictions =  new TreeSet<>(predictions);
        this.predictionsForXml = null;
    }

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
                    "Can only realize a prediction when accompanying locations is available. Location " + location
                            + " is not available in " + getMid() + " " + locations);
        }

        Platform platform = location.getPlatform();
        if (platform == null) {
            log.debug("Can't realize prediction with location {} because it has no platform", location);
            return;
        }

        Prediction prediction = getPrediction(platform);
        if (prediction == null) {
            prediction = findOrCreatePrediction(platform, location);
            prediction.setPlannedAvailability(true);
        }
        prediction.setState(Prediction.State.REALIZED);

    }

    public boolean removePrediction(Platform platform) {
        if (predictions == null) {
            return false;
        }

        return predictions.remove(new Prediction(platform));
    }

    public Prediction findOrCreatePrediction(Platform platform) {
        return findOrCreatePrediction(platform, Embargos.unrestrictedInstance());
    }

    protected Prediction findOrCreatePrediction(Platform platform, Embargo embargo) {
         Prediction prediction = getPrediction(platform);
        if (prediction == null) {
            log.debug("Creating prediction object for {}: {}", platform, this);
            prediction = new Prediction(platform);
            Embargos.copy(embargo, prediction);
            prediction.setPlannedAvailability(false);
            prediction.setParent(this);
            prediction.setAuthority(Authority.USER);
            if (predictions == null) {
                predictions = new TreeSet<>();
            }
            this.predictions.add(prediction);
        }
        return prediction;
    }


    /**
     * Returns the locations in {@link Location#PRESENTATION_ORDER}
     */
    public SortedSet<Location> getLocations() {
        if (locations == null) {
            locations = new TreeSet<>();
        }
        return locations;
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
            if (location.hasPlatform() && location.isPublishable()) {
                realizePrediction(location);
            }
        }

        return this;
    }

    public boolean removeLocation(Location location) {
        if (locations != null && locations.remove(location)) {
            markCeresUpdate();
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

    public void setImages(List<Image> images) {
        this.images = images;
    }

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

    public Image getImage(int index) {
        if (images == null || index >= images.size()) {
            return null;
        }

        return images.get(index);
    }

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



    public Image findImage(ImageType type) {
        for (Image image : getImages()) {
            if (type.equals(image.getType())) {
                return image;
            }
        }

        return null;
    }

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

    public boolean removeImage(Image image) {
        if (images != null) {
            image.setParent(null);
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
     * What does it mean to be 'embedddable'?
     */
    @XmlAttribute(name = "embeddable")
    public boolean isEmbeddable() {
        return isEmbeddable;
    }

    public void setEmbeddable(boolean embeddable) {
        isEmbeddable = embeddable;
    }

    /**
     * When true Ceres/Pluto.. needs a restriction update. The underlying field
     * is managed by Hibernate, and not accessible.
     */
    public boolean isLocationAuthorityUpdate() {
        return locationAuthorityUpdate;
    }

    public void setLocationAuthorityUpdate(Boolean ceresUpdate) {
        this.locationAuthorityUpdate = ceresUpdate;
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
     */

    @Deprecated
    final public Date getSortDate() {
        return DateUtils.toDate(getSortInstant());
    }

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
     * Method is needed for unmarshalling. It does nothing. It may do something
     * in overrides (as in {@link Group})
     */
    @JsonIgnore
    final void setSortDate(Date date) {
        this.setSortInstant(DateUtils.toInstant(date));
    }

    /**
     * Method is needed for unmarshalling. It does nothing. It may do something
     * in overrides (as in {@link Group})
     */
    void setSortInstant(Instant date) {
        this.sortInstant = date;
        this.sortDateValid = true;
        this.sortDateInvalidatable = false;

    }

    protected void invalidateSortDate() {
        if (this.sortDateInvalidatable) {
            this.sortDateValid = false;
        }
    }

    @Override
    public boolean isMerged() {
        return mergedTo != null || mergedToRef != null;
    }

    public MediaObject getMergedTo() {
        return mergedTo;
    }

    public void setMergedTo(MediaObject mergedTo) {
        if (this.mergedTo != null && mergedTo != null && !this.mergedTo.equals(mergedTo)) {
            throw new IllegalArgumentException(
                    "Can not merge " + this + " to " + mergedTo + " since it is already merged to " + this.mergedTo);
        }

        int depth = 10;
        MediaObject p = mergedTo;
        if (mergedTo != null) {
            while (p.isMerged()) {
                if (this.equals(p)) {
                    throw new IllegalArgumentException("Loop while merging source " + this + " to " + mergedTo);
                }
                if (depth-- == 0) {
                    throw new IllegalArgumentException(
                            "Deep regression while merging source " + this + " to " + mergedTo);
                }

                p = p.getMergedTo();
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

    public void setMergedToRef(String mergedToRef) {
        this.mergedToRef = mergedToRef;
    }

    /**
     * This setter is not intended for normal use in code. RepubDate is meant
     * for monitoring the publication delays in NewRelic. It contains the
     * scheduled publication date of this MediaObject. This field is set (in
     * SQL) when republishing descendants, and (in code) when revoking
     * locations/images. When the republication delay reaches a certain value an
     * alert is raised in NewRelic.
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
        if (o instanceof MediaObject) {
            return super.equals(o) || equalsOnMid((MediaObject) o);
        } else {
            return super.equals(o);
        }
    }

    private boolean equalsOnMid(MediaObject o) {
        return mid != null && mid.equals(o.getMid());
    }

    @SuppressWarnings("unused")
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (predictionsForXml != null) {
            this.predictions = new TreeSet<>(predictionsForXml);
            this.predictionsForXml = null;
        }
    }

    @Override
    protected abstract String getUrnPrefix();

    // Following are overriden to help FTL and hibernate
    // See https://issues.apache.org/jira/browse/FREEMARKER-24

    /**
     * <p>
     * Overriden to help hibernate search (see MediaSearchMappingFactory)
     * Probably has to to with https://bugs.openjdk.java.net/browse/JDK-8071693
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getMainTitle() {
        return LocalizedObject.super.getMainTitle();
    }

    /**
     * <p>
     * Overriden to help hibernate search (see MediaSearchMappingFactory)
     * Probably has to to with https://bugs.openjdk.java.net/browse/JDK-8071693
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getSubTitle() {
        return LocalizedObject.super.getSubTitle();
    }

    /**
     * <p>
     * Overriden to help hibernate search (see MediaSearchMappingFactory)
     * Probably has to to with https://bugs.openjdk.java.net/browse/JDK-8071693
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getMainDescription() {
        return LocalizedObject.super.getMainDescription();
    }

    /**
     * <p>
     * Overriden to help FTL. See
     * https://issues.apache.org/jira/browse/FREEMARKER-24
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getShortTitle() {
        return LocalizedObject.super.getShortTitle();
    }

    /**
     * <p>
     * Overriden to help FTL. See
     * https://issues.apache.org/jira/browse/FREEMARKER-24
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getOriginalTitle() {
        return LocalizedObject.super.getOriginalTitle();
    }

    /**
     * <p>
     * Overriden to help FTL. See
     * https://issues.apache.org/jira/browse/FREEMARKER-24
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getWorkTitle() {
        return LocalizedObject.super.getWorkTitle();
    }

    /**
     * <p>
     * Overriden to help FTL. See
     * https://issues.apache.org/jira/browse/FREEMARKER-24
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getLexicoTitle() {
        return LocalizedObject.super.getLexicoTitle();
    }

    /**
     * <p>
     * Overriden to help FTL. See
     * https://issues.apache.org/jira/browse/FREEMARKER-24
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getAbbreviatedTitle() {
        return LocalizedObject.super.getAbbreviatedTitle();
    }

    /**
     * <p>
     * Overriden to help FTL. See
     * https://issues.apache.org/jira/browse/FREEMARKER-24
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getSubDescription() {
        return LocalizedObject.super.getSubDescription();
    }

    /**
     * <p>
     * Overriden to help FTL. See
     * https://issues.apache.org/jira/browse/FREEMARKER-24
     * </p>
     * {@inheritDoc}
     */
    @Override
    public String getShortDescription() {
        return LocalizedObject.super.getShortDescription();
    }

    public void mergeImages(MediaObject incoming, OwnerType owner) {
        List<Image> firstImages = new ArrayList<>();
        incoming.getImages().forEach(i -> {
            if (Objects.equals(i.getOwner(), owner)) {
                firstImages.add(addOrUpdate(i));
            } else {
                log.debug("A bit odd, incoming with different owner");
            }
        });
        List<Image> toRemove = getImages()
            .stream()
            .filter(i -> Objects.equals(i.getOwner(), owner))
            .filter(i -> ! incoming.getImages().contains(i))
            .collect(Collectors.toList());

        toRemove.forEach(this::removeImage);
        List<Image> rest =
            getImages().stream()
                .filter(i -> !owner.equals(i.getOwner()))
                .collect(Collectors.toList());

        getImages().clear();
        addAllImages(firstImages);
        addAllImages(rest);

    }

    public void addAllImages(List<Image> imgs) {
        imgs.forEach(img -> img.setParent(this));
        getImages().addAll(imgs);
    }

    public void removeImages() {
        getImages().forEach(img -> img.setParent(null));
        images.clear();
    }

    private Image addOrUpdate(Image img) {
        Image existing = this.getImage(img);
        if (existing != null) {
            if (existing.getOwner() != img.getOwner()) {
                log.info("Copying from different owner {} <- {}", existing, img);
            }
            existing.copyFrom(img);
            return existing;
        } else {
            addImage(img);
            return img;
        }
    }

    @Override
    // overriden to give access to test
    protected byte[] prepareForCRCCalc() {
        return super.prepareForCRCCalc();
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
    public final String toString() {
        String mainTitle;
        try {
            String mt = getMainTitle();
            mainTitle = mt == null ? "null" : ('"' + mt + '"');
        } catch(RuntimeException le) {
            mainTitle = "[" + le.getClass() + " " + le.getMessage() + "]"; // (could be a LazyInitializationException)
        }
        return String.format(getClass().getSimpleName() + "{%1$s%2$smid=%3$s, title=%4$s}",
            (! Workflow.PUBLICATIONS.contains(workflow) ? workflow + ":" : "" ),
            getType() == null ? "" : getType() + " ",
            this.getMid() == null ? "null" : "\"" + this.getMid() + "\"",
            mainTitle);
    }

}
