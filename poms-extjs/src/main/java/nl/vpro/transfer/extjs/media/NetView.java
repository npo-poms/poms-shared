package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Net;

/**
 * @author Michiel Meeuwissen
 * @since 1.8
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
    "name",
    "displayName"
})
public class NetView {

    private final Net net;

    NetView(Net n) {
        this.net = n;
    }
    public String getName() {
        return net.getId();
    }
    public String getDisplayName() {
        return net.toString();
    }
}
