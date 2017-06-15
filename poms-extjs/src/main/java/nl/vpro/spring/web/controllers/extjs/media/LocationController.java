package nl.vpro.spring.web.controllers.extjs.media;

import nl.vpro.domain.media.search.LocationForm;
import nl.vpro.domain.media.search.Pager;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.errors.ErrorList;
import nl.vpro.web.media.ExtLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/locations")
public class LocationController {

    @Autowired
    private ExtLocationService locationService;

    @RequestMapping(value = "/orphans", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<?> findOrphans(
        @RequestParam(value = "start", required = false, defaultValue = "0") int start,
        @RequestParam(value = "limit", required = false, defaultValue = "25") int limit,
        @RequestParam(value = "sort", required = false, defaultValue = "creationDate") String sort,
        @RequestParam(value = "dir", required = false, defaultValue = "desc") String dir) {

        Pager pager = new Pager(start, limit, sort, Pager.Direction.valueOf(dir.toUpperCase()));

        LocationForm form = new LocationForm(pager);

        try {
            return locationService.findOrphans(form);
        } catch (Exception e) {
            return new ErrorList(e.getMessage());
        }
    }
}
