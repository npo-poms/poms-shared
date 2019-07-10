package nl.vpro.nep.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import nl.vpro.nep.sam.model.ApiObject;
import nl.vpro.nep.sam.model.StreamAccessItem;

/**
 * NEP 'Stream Access Management' API.
 *
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public interface NEPSAMService {



    String streamAccess(String streamId, boolean drm, StreamAccessItem streamUrlRequest);


    static StreamAccessItem createStreamAccessItem(String ip, Duration duration) {
        StreamAccessItem item = new StreamAccessItem().data(new ApiObject().type("access"));
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("viewer", "pomsgui");
        attributes.put("ip", ip);
        attributes.put("duration", duration);
        item.getData().setAttributes(attributes);
        return item;
    }
}
