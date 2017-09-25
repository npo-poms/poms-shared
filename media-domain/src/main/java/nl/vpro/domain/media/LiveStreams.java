package nl.vpro.domain.media;

import lombok.Getter;

import nl.vpro.domain.Displayable;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Getter
public enum LiveStreams implements Displayable {

    NPO1("NPO-1dvr", Channel.NED1),
    NPO2("NPO-2", Channel.NED2),
    NPO3("NPO-3", Channel.NED3);

    private final String id;
    private final Channel channel;

    LiveStreams(String id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    @Override
    public String getDisplayName() {
        return channel.getDisplayName();
    }
}
