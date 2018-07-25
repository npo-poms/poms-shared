package nl.vpro.domain.user;

import java.util.*;

import nl.vpro.domain.NotFoundException;

/**
 * @author Michiel Meeuwissen
 */
public final class Broadcasters {

    private Broadcasters() {
        // no instances for this
    }


    public static List<Broadcaster> match(BroadcasterService broadcasterService, List<Broadcaster> broadcasters) throws NotFoundException {
        List<Broadcaster> result = new ArrayList<>(broadcasters.size());
        for (Broadcaster b : broadcasters) {
            if (b.getId() != null) {
                result.add(broadcasterService.find(b.getId()));
            } else if (b.getWhatsOnId() != null) {
                result.add(broadcasterService.findForWhatsOnId(b.getWhatsOnId()));
            } else if (b.getNeboId() != null) {
                result.add(broadcasterService.findForNeboId(b.getNeboId()));
            } else {
                result.add(b);
            }
        }
        return result;
    }


    public static Optional<Broadcaster> getByAnyId(BroadcasterService broadcasterService, String id) {
        Broadcaster byId = broadcasterService.find(id);
        if (byId != null ) {
            return Optional.of(byId);
        }
        return broadcasterService.findAll()
            .stream()
            .filter(b -> b.getWhatsOnId().equals(id) || b.getNeboId().equals(id) || b.getMisId().equals(id)
            )
            .findFirst();
    }


}
