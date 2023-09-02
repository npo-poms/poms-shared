package nl.vpro.domain.page;


import lombok.*;

import java.time.Instant;
import java.util.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.MutableEmbargo;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.jackson2.Views;
import nl.vpro.validation.*;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 2.0

 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
@XmlType(name = "pageType", propOrder =
    {
        "url",
        "crids",
        "alternativeUrls",
        "broadcasters",
        "portal",
        "title",
        "subtitle",
        "keywords",
        "genres",
        "summary",
        "paragraphs",
        "tags",
        "referrals",
        "links",
        "embeds",
        "statRefs",
        "images",
        "relations"
    }
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "objectType")
@JsonTypeName("page")
@JsonPropertyOrder(
    {
        "objectType",
        "type",
        "url",
        "sortDate",
        "crids",
        "alternativeUrls",
        "broadcasters",
        "portal",
        "title",
        "subtitle",
        "keywords",
        "genres",
        "summary",
        "paragraphs",
        "tags",
        "referrals",
        "links",
        "embeds",
        "statRefs",
        "images",
        "relations",
        "publishStart",
        "creationDate",
        "lastModified"
    })
@EqualsAndHashCode
public class Page implements MutableEmbargo<Page> {

    public static PageBuilder builder() {
        return new PageBuilder(new Page());
    }

    @NotNull
    @XmlAttribute(required = true)
    @Getter
    @Setter
    private PageType type;


    @NotNull
    @Getter
    @Setter
    private PageWorkflow workflow;

    @NotNull
    @URI
    @XmlAttribute(required = true)
    @Getter
    @Setter
    protected String url;

    @Valid
    @XmlElement(name = "crid")
    @JsonProperty("crids")
    @Getter
    @Setter
    protected List<@CRID String> crids;

    @Valid
    @URI
    @XmlElement(name = "alternativeUrl")
    @JsonProperty("alternativeUrls")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Getter
    @Setter
    protected List<String> alternativeUrls;

    @Valid
    @XmlElement(name = "broadcaster", required=true)
    @JsonProperty("broadcasters")
    @Getter
    @Setter
    protected List<Broadcaster> broadcasters;

    @XmlElement(name = "portal")
    @JsonProperty("portal")
    @Getter
    @Setter
    protected Portal portal;

    @NotNull
    @Size(min = 1)
    @NoHtml
    protected String title;

    @NoHtml
    protected String subtitle;

    @NoHtml
    protected List<String> keywords;

    @Valid
    protected SortedSet<Genre> genres;

    @NoHtml
    protected String summary;

    protected List<Paragraph> paragraphs;

    protected List<String> tags;

    protected Integer refCount;

    protected List<Referral> referrals;

    protected List<Link> links;

    protected List<Embed> embeds;

    protected List<String> statRefs;

    protected List<Image> images;

    @Valid
    protected SortedSet<Relation> relations;

    protected Instant creationDate;

    protected Instant lastModified;

    protected Instant publishStart;

    protected Instant publishStop;

    protected Instant lastPublished;

    protected List<Credits> credits;


    public Page() {
    }

    public Page(PageType type) {
        this.type = type;
    }


    @XmlElement(name = "title", required = true)
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name = "subTitle")
    @JsonProperty("subTitle")
    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @XmlElement(name = "keyword")
    @JsonProperty("keywords")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getKeywords() {
        return keywords;
    }



    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    @XmlElement(name = "genre")
    @JsonProperty("genres")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Genre> getGenres() {
        return genres;
    }

    public void setGenres(SortedSet<Genre> genres) {
        this.genres = genres;
    }

    @XmlElement(name = "summary")
    @JsonProperty("summary")
    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @XmlElementWrapper(name = "paragraphs")
    @XmlElement(name = "paragraph")
    @JsonProperty("paragraphs")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    @XmlElement(name = "tag")
    @JsonProperty("tags")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @XmlAttribute
    @JsonProperty
    public Integer getRefCount() {
        return refCount;
    }

    public void setRefCount(Integer refCount) {
        this.refCount = refCount;
    }

    @XmlElement(name = "referral")
    @JsonProperty("referrals")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Referral> getReferrals() {
        return referrals;
    }

    public void setReferrals(List<Referral> referrals) {
        this.referrals = referrals;
    }

    public void add(Referral referral) {
        if(this.referrals == null) {
            this.referrals = new ArrayList<>();
        }

        this.referrals.add(referral);
    }

    @XmlElement(name = "link")
    @JsonProperty("links")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public void add(Link link) {
        if (this.links == null) {
            this.links = new ArrayList<>();
        }
        this.links.add(link);
    }


    @XmlElement(name = "embed")
    @JsonProperty("embeds")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Embed> getEmbeds() {
        return embeds;
    }

    public void setEmbeds(List<Embed> embeds) {
        this.embeds = embeds;
    }

    public void add(Embed embed) {
        if(this.embeds == null) {
            this.embeds = new ArrayList<>();
        }

        this.embeds.add(embed);
    }

    @XmlElement(name = "statRef")
    @JsonProperty("statRefs")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getStatRefs() {
        return statRefs;
    }

    public void setStatRefs(List<String> statRefs) {
        this.statRefs = statRefs;
    }

    @XmlElementWrapper(name = "images")
    @XmlElement(name = "image")
    @JsonProperty("images")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }


    @XmlElement(name = "relation")
    @JsonProperty("relations")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Relation> getRelations() {
        if (this.relations == null) {
            this.relations = new TreeSet<>();
        }
        return relations;
    }

    public void setRelations(SortedSet<Relation> relations) {
        this.relations = relations;
    }


    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    public Instant getSortDate() {
        return publishStart != null ? publishStart : creationDate;
    }

    @SuppressWarnings("unused")
    public void setSortDate(Instant date) {
        // ignored
    }

    @SuppressWarnings("unused")
    @XmlTransient
    @JsonIgnore
    public void setSortDate(Date date) {
        // ignored

    }

    @Override
    @XmlAttribute(name = "publishStart")
    @JsonProperty("publishStart")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @NonNull
    @Override
    public Page setPublishStartInstant(Instant publishStart) {
        this.publishStart = publishStart;
        return this;
    }

    @Override
    @XmlAttribute(name = "publishStop")
    @JsonProperty("publishStop")
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @NonNull
    @Override
    public Page setPublishStopInstant(Instant publishStop) {
        this.publishStop = publishStop;
        return this;
    }


    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public Instant getLastPublished() {
        return lastPublished;
    }

    public void setLastPublished(Instant lastPublished) {
        this.lastPublished = lastPublished;
    }

    @Override
    public String toString() {
        return "Page{url='" + url + '\'' + '}';
    }

    @XmlAttribute(name = "workflow")
    protected PageWorkflow getWorkflowAttribute() {
        return workflow == PageWorkflow.PUBLISHED ? null : workflow;
    }


    protected void setWorkflowAttribute(PageWorkflow workflow) {
        this.workflow = workflow;
    }

    @JsonProperty("expandedWorkflow")
    @JsonView({Views.Publisher.class})
    protected PageWorkflow getExpandedWorkflow() {
        return workflow == null ? PageWorkflow.PUBLISHED : workflow;
    }


}
