package nl.vpro.domain.page;


import lombok.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

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
public class Page implements MutableEmbargo<Page>, Serializable {

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

    @Setter
    @NotNull
    @Size(min = 1)
    @NoHtml
    protected String title;

    @Setter
    @NoHtml
    protected String subtitle;

    @Setter
    protected List<@NoHtml String> keywords;

    @Setter
    @Valid
    protected SortedSet<@Valid Genre> genres;

    @Setter
    @NoHtml(aggressive = false)
    protected String summary;

    @Setter
    protected List<@Valid Paragraph> paragraphs;

    @Setter
    protected List<@NoHtml String> tags;

    @Setter
    protected Integer refCount;

    @Setter
    protected List<@Valid Referral> referrals;

    @Setter
    protected List<@Valid Link> links;

    @Setter
    protected List<@Valid Embed> embeds;

    @Setter
    protected List<String> statRefs;

    @Setter
    protected List<@Valid Image> images;

    @Setter
    @Valid
    protected SortedSet<@Valid Relation> relations;

    @Setter
    protected Instant creationDate;

    @Setter
    protected Instant lastModified;

    protected Instant publishStart;

    protected Instant publishStop;

    @Setter
    protected Instant lastPublished;

    protected List<@Valid Credits> credits;


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

    @XmlElement(name = "subTitle")
    @JsonProperty("subTitle")
    public String getSubtitle() {
        return subtitle;
    }

    @XmlElement(name = "keyword")
    @JsonProperty("keywords")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getKeywords() {
        return keywords;
    }


    @XmlElement(name = "genre")
    @JsonProperty("genres")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public SortedSet<Genre> getGenres() {
        return genres;
    }

    @XmlElement(name = "summary")
    @JsonProperty("summary")
    public String getSummary() {
        return summary;
    }

    @XmlElementWrapper(name = "paragraphs")
    @XmlElement(name = "paragraph")
    @JsonProperty("paragraphs")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    @XmlElement(name = "tag")
    @JsonProperty("tags")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<String> getTags() {
        return tags;
    }

    @XmlAttribute
    @JsonProperty
    public Integer getRefCount() {
        return refCount;
    }

    @XmlElement(name = "referral")
    @JsonProperty("referrals")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Referral> getReferrals() {
        return referrals;
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

    @XmlElementWrapper(name = "images")
    @XmlElement(name = "image")
    @JsonProperty("images")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public List<Image> getImages() {
        return images;
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


    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    public Instant getLastModified() {
        return lastModified;
    }

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    public Instant getCreationDate() {
        return creationDate;
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
