package nl.vpro.transfer.extjs.media;

import java.util.Arrays;
import java.util.Collection;

import nl.vpro.domain.media.MediaService;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.transfer.extjs.TransferList;

/**
 * @author Michiel Meeuwissen
 */
public class AdminBroadcasterList extends TransferList<BroadcasterView> {


    public AdminBroadcasterList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static AdminBroadcasterList create(MediaService mediaService, Collection<? extends Broadcaster> broadcasters) {
        AdminBroadcasterList simpleList = new AdminBroadcasterList(true, null);

        for (Broadcaster b : broadcasters) {
            simpleList.add(new BroadcasterView(b.getId(), b.getDisplayName(), false, mediaService.countForBroadcaster(b.getId()), b.getWhatsOnId(), b.getNeboId(), b.getMisId()));
        }
        return simpleList;
    }

    public static AdminBroadcasterList create(MediaService mediaService, Broadcaster... broadcasters) {
        return create(mediaService, Arrays.asList(broadcasters));

    }
}
