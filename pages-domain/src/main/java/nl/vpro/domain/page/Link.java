package nl.vpro.domain.page;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.validation.NoHtml;
import nl.vpro.validation.URI;

/**
 * A link is an outgoing {@link Association}
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@XmlType(name = "linkType", propOrder = {"text"})
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class Link implements Association {

    public static Link of(String url, String title, LinkType type) {
        Link referral = new Link();
        referral.setText(title);
        referral.setPageRef(url);
        referral.setType(type);
        return referral;
    }

    @NotNull
    @URI
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
}
