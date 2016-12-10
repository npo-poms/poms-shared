/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.update;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.page.Crid;
import nl.vpro.domain.page.PageType;
import nl.vpro.domain.page.util.Urls;
import nl.vpro.domain.page.validation.ValidBroadcaster;
import nl.vpro.domain.page.validation.ValidGenre;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.validation.NoHtml;
import nl.vpro.validation.NoHtmlList;
import nl.vpro.validation.URI;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Roelof Jan Koekoek
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pageUpdateType")
@XmlRootElement(name = "page")
public class PageUpdate {

    @NotNull
    @XmlAttribute(required = true)
    protected PageType type;

    @NotNull
    @URI
    @XmlAttribute(required = true)
    protected String url;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    protected Instant publishStart;

    @XmlElement(name = "crid")
    @JsonProperty("crids")
    @Valid
    protected List<Crid> crids;

    @XmlElement(name = "alternativeUrl")
    @JsonProperty("alternativeUrls")
    @Valid
    protected List<String> alternativeUrls;

    @NotNull
    @Size(min = 1)
    @XmlElement(name = "broadcaster", required = true)
    @JsonProperty("broadcasters")
    @ValidBroadcaster
    protected List<String> broadcasters;

    @Valid
    protected PortalUpdate portal;

    @NotNull
    @Size(min = 1)
    @XmlElement(required = true, nillable = false)
    @NoHtml
    protected String title;

    @NoHtml
    protected String subtitle;

    @XmlElement(name = "keyword")
    @JsonProperty("keywords")
    @NoHtmlList
    protected List<String> keywords;

    @NoHtml
    protected String summary;

    @XmlElementWrapper(name = "paragraphs")
    @XmlElement(name = "paragraph")
    @JsonProperty("paragraphs")
    @Valid
    protected List<ParagraphUpdate> paragraphs;

    @XmlElement(name = "tag")
    @JsonProperty("tags")
    //@Pattern("(?i)[a-z]")
    @NoHtmlList
    protected List<String> tags;


    @XmlElement(name = "genre")
    @JsonProperty("genres")
    @ValidGenre
    protected List<String> genres;

    @XmlElement(name = "link")
    @JsonProperty("links")
    @Valid
    private List<LinkUpdate> links;

    @XmlElementWrapper(name = "embeds")
    @XmlElement(name = "embed")
    @JsonProperty("embeds")
    @Valid
    private List<EmbedUpdate> embeds;

    @XmlElement(name = "statRef")
    @JsonProperty("statRefs")
    @NoHtmlList
    private List<String> statRefs;

    @XmlElement(name = "image")
    @JsonProperty("images")
    @Valid
    protected List<ImageUpdate> images;


    @XmlElement(name = "relation")
    @JsonProperty("relations")
    @Valid
    protected List<RelationUpdate> relations;


    @XmlTransient
    private String rev;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant lastPublished;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant creationDate;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    private Instant lastModified;

    public PageUpdate() {
    }

    public PageUpdate(PageType type, String url) {
        this.type = type;
        setUrl(url);
    }

    public PageType getType() {
        return type;
    }

    public void setType(PageType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : Urls.normalize(url);
    }

    public Instant getPublishStart() {
        return publishStart;
    }

    public void setPublishStart(Instant publishStart) {
        this.publishStart = publishStart;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public List<Crid> getCrids() {
        if (crids == null) {
            crids = new ArrayList<>();
        }
        return crids;
    }

    public void setCrids(List<Crid> crids) {
        this.crids = crids;
    }

    public List<String> getAlternativeUrls() {
        if (alternativeUrls == null) {
            alternativeUrls = new ArrayList<>();
        }
        return alternativeUrls;
    }

    public void setAlternativeUrls(List<String> alternativeUrls) {
        this.alternativeUrls = alternativeUrls;
    }

    public List<String> getBroadcasters() {
        if (broadcasters == null) {
            broadcasters = new ArrayList<>();
        }
        return broadcasters;
    }

    public void setBroadcasters(List<String> broadcasters) {
        this.broadcasters = broadcasters;
    }

    public PortalUpdate getPortal() {
        return portal;
    }

    public void setPortal(PortalUpdate portal) {
        this.portal = portal;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public List<String> getKeywords() {
        if (keywords == null) {
            keywords = new ArrayList<>();
        }
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<ParagraphUpdate> getParagraphs() {
        if (paragraphs == null) {
            paragraphs = new ArrayList<>();
        }
        return paragraphs;
    }

    public void setParagraphs(List<ParagraphUpdate> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public List<String> getTags() {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<LinkUpdate> getLinks() {
        if (links == null) {
            links = new ArrayList<>();
        }
        return links;
    }

    public void setLinks(List<LinkUpdate> links) {
        this.links = links;
    }

    public List<EmbedUpdate> getEmbeds() {
        if (embeds == null) {
            embeds = new ArrayList<>();
        }
        return embeds;
    }

    public void setEmbeds(List<EmbedUpdate> embeds) {
        this.embeds = embeds;
    }

    public void embed(EmbedUpdate embedUpdate) {
        getEmbeds().add(embedUpdate);
    }

    public List<String> getStatRefs() {
        return statRefs;
    }

    public void setStatRefs(List<String> statRefs) {
        this.statRefs = statRefs;
    }

    public List<ImageUpdate> getImages() {
        if (images == null) {
            images = new ArrayList<>();
        }

        return images;
    }

    public void setImages(List<ImageUpdate> images) {
        this.images = images;
    }

    public List<RelationUpdate> getRelations() {
        if (relations == null) {
            relations = new ArrayList<>();
        }
        return relations;
    }

    public void setRelations(List<RelationUpdate> relations) {
        this.relations = relations;
    }

    @JsonProperty("_rev")
    public String getRevision() {
        return rev;
    }

    public void setRevision(String rev) {
        this.rev = rev;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public List<String> getGenres() {
        if (genres == null) {
            genres = new ArrayList<>();
        }

        return genres;
    }

    public Instant getLastPublished() {
        return lastPublished;
    }

    public void setLastPublished(Instant lastPublished) {
        this.lastPublished = lastPublished;
    }

    @JsonProperty("_id")
    public String getId() {
        return getUrl();
    }

    public void setId(String u) {
        setUrl(u);
    }


    @Override
    public String toString() {
        return url + (crids != null ? (" " + crids) : "") + " " + getTitle();
    }

}
