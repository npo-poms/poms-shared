package nl.vpro.domain.page;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.URL;

import nl.vpro.validation.NoHtml;

/**
 * A link is an outgoing {@link Association}
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@XmlType(name = "linkType", propOrder = {"text"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Link implements Association {


    public static Link of(String url, String title, LinkType type) {
        Link referral = new Link();
        referral.setText(title);
        referral.setPageRef(url);
        referral.setType(type);
        return referral;
    }

    @NotNull
    @URL
    @XmlAttribute
    private String pageRef;

    @XmlAttribute
    private LinkType type;

    @NoHtml
    private String text;

    public Link() {

    }
    public Link(String pageRef, String text) {
        this.pageRef = pageRef;
        this.text = text;
    }

    public Link(String pageRef, String text, LinkType type) {
        this.pageRef = pageRef;
        this.text = text;
        this.type = type;
    }

    @Override
    public String getPageRef() {
        return pageRef;
    }

    public void setPageRef(String pageRef) {
        this.pageRef = pageRef;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public LinkType getType() {
        return type;
    }

    public void setType(LinkType type) {
        this.type = type;
    }
}
