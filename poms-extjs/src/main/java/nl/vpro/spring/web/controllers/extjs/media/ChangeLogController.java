package nl.vpro.spring.web.controllers.extjs.media;

import java.util.AbstractList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import nl.vpro.domain.media.EventService;
import nl.vpro.domain.user.EditorService;
import nl.vpro.esper.event.MediaEvent;
import nl.vpro.transfer.extjs.TransferList;

/**
 * @author Michiel Meeuwissen
 * @since 3.2
 */
@Controller
@RequestMapping(value = "/changelog")
public class ChangeLogController {

    @Autowired
    private EventService eventService;

    @Autowired
    private EditorService editorService;


    @RequestMapping(value = "/{mediaId}", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<MediaEventView> list (
        @PathVariable(value = "mediaId") final Long mediaObject,
        @RequestParam(value = "start", required = false, defaultValue = "0") final int start,
        @RequestParam(value = "limit", required = false, defaultValue = "100") final int limit
        ) {
        final List<MediaEvent> result = eventService.find(mediaObject, start, limit);

        TransferList<MediaEventView> list = new TransferList<MediaEventView>() {
            {
                addAll(adapt(result));
                success = true;
                writable = false;
                results = result.size() < limit ? start + result.size() :  eventService.count(mediaObject);
            }
        };
        return list;
    }

    public class MediaEventView extends MediaEvent {

        public MediaEventView(MediaEvent e) {
            super(e);
        }
        @JsonProperty
        public String getDisplayName() {
            return editorService.get(getPrincipalId()).getDisplayName();
        }

    }
    public List<MediaEventView> adapt(final List<MediaEvent> list) {
        return new AbstractList<MediaEventView>() {
            @Override
            public MediaEventView get(int index) {
                return new MediaEventView(list.get(index));
            }

            @Override
            public int size() {
                return list.size();
            }
        };

    }
}
