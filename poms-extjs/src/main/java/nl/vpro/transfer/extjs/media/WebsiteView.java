package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.ToStringBuilder;

import nl.vpro.domain.media.Website;
import nl.vpro.transfer.extjs.ExtRecord;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "id",
        "website",
        "index"
        })
public class WebsiteView extends ExtRecord {

    private Long id;
    private String website;
    private int index;

    private WebsiteView() {
    }

    private WebsiteView(Website website, int index) {
        this.id = website.getId();
        this.website = website.getUrl();
        this.index = index;
    }

    public static WebsiteView create(Website website, int index) {
        return new WebsiteView(website, index);
    }

    public Website toWebsite() {
        Website site = new Website();
        site.setId(this.id);
        return updateTo(site);
    }

    public Website updateTo(Website fullWebsite) {
        fullWebsite.setUrl(this.website);
        return fullWebsite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("id", id)
            .append("website", website)
            .append("index", index)
            .toString();
    }
}
