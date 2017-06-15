/**
 * Copyright (C) 2009 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.support.Image;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.transfer.extjs.ExtRecord;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "id",
    "index",
    "typeText",
    "imageType",
    "title",
    "description",
    "url",
    "height",
    "width",
    "highlighted",
    "credits",
    "source",
    "date"
})
public class ImageView extends ExtRecord {
    private Long id;

    private Integer index;

    private String typeText;

    private String imageType;

    private String title;

    private String description;

    private String url;

    private Integer height;

    private Integer width;

    private Boolean highlighted;

    @XmlAttribute
    private Date publishStart;

    @XmlAttribute
    private Date publishStop;

    private String credits;

    private String source;

    private String date;

    private ImageView() {
    }

    private ImageView(Long id) {
        this.id = id;
    }

    public static ImageView create(Image fullImage, int index) {
        ImageView simpleImage = new ImageView(fullImage.getId());

        simpleImage.index = index;
        simpleImage.typeText = fullImage.getType().toString();
        simpleImage.imageType = fullImage.getType().name();
        simpleImage.title = fullImage.getTitle();
        simpleImage.description = fullImage.getDescription();
        simpleImage.url = fullImage.getImageUri();
        simpleImage.height = fullImage.getHeight();
        simpleImage.width = fullImage.getWidth();
        simpleImage.highlighted = fullImage.isHighlighted();
        simpleImage.publishStart = fullImage.getPublishStart();
        simpleImage.publishStop = fullImage.getPublishStop();
        simpleImage.credits = fullImage.getCredits();
        simpleImage.source = fullImage.getSource();
        simpleImage.date = fullImage.getDate();


        return simpleImage;
    }

    public Image toImage() {
        Image fullImage = new Image();
        fullImage.setId(this.id);
        return updateTo(fullImage);
    }

    public Image updateTo(Image fullImage) {
        fullImage
            .setImageUri(url)
            .setType(ImageType.valueOf(imageType))
            .setTitle(title)
            .setDescription(description)
            .setHeight(height)
            .setWidth(width)
            .setHighlighted(highlighted)
            .setPublishStart(publishStart)
            .setPublishStop(publishStop)
            .setCredits(credits)
            .setSource(source)
            .setDate(date)
        ;

        fullImage.setOwner(OwnerType.BROADCASTER);
        return fullImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTypeText() {
        return typeText;
    }

    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url.trim();
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Boolean isHighlighted() {
        return highlighted;
    }

    public void setHighlighted(Boolean highlighted) {
        this.highlighted = highlighted;
    }

    public Date getPublishStart() {
        return publishStart;
    }

    public void setPublishStart(Date publishStart) {
        this.publishStart = publishStart;
    }

    public Date getPublishStop() {
        return publishStop;
    }

    public void setPublishStop(Date publishStop) {
        this.publishStop = publishStop;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = StringUtils.isEmpty(date) ? null : date;
    }
}
