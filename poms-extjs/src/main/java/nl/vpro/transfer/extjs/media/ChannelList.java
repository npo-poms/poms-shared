package nl.vpro.transfer.extjs.media;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.Channel;
import nl.vpro.transfer.extjs.TransferList;

/**
 * @author Michiel Meeuwissen
 * @since 1.8
 */
@XmlRootElement(name = "channels")
public class ChannelList extends TransferList<ChannelView> {

    private ChannelList() {
        success = true;
    }

    public static ChannelList create(List<Channel> searchResult) {
        ChannelList simpleList = new ChannelList();

        for (Channel channel : searchResult) {
            simpleList.add(new ChannelView(channel));
        }

        simpleList.results = searchResult.size();
        return simpleList;
    }
}
