package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Channel;

/**
 * @author Michiel Meeuwissen
 * @since 1.8
 */
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {
    "name",
    "displayName"
})
public class ChannelView {

    private final Channel channel;

    ChannelView(Channel c) {
        this.channel = c;
    }
    public String getName() {
        return channel.name();
    }
    public String getDisplayName() {
        return channel.toString();
    }
}
