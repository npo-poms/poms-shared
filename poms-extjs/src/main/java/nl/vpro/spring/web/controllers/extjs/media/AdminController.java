package nl.vpro.spring.web.controllers.extjs.media;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.vpro.domain.admin.AdminService;
import nl.vpro.domain.admin.IndexRequest;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.admin.MediaRepublisher;
import nl.vpro.domain.media.admin.SubtitlesRepublisher;
import nl.vpro.domain.media.search.DateRange;
import nl.vpro.domain.media.search.RepublishMediaForm;
import nl.vpro.domain.user.Editor;
import nl.vpro.domain.user.EditorService;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.errors.ErrorList;
import nl.vpro.transfer.extjs.success.SuccessList;
import nl.vpro.util.DateUtils;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private EditorService userService;

    @Autowired
    private MediaRepublisher mediaRepublisher;

    @Autowired
    private SubtitlesRepublisher subtitlesRepublisher;

    @Autowired
    private ScheduleEventService scheduleEventService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @ResponseBody
    public TransferList indexAll() {
        try {
            IndexRequest request = adminService.updateAllIndexes();
            return new SuccessList(request.toString());
        } catch(Exception e) {
            return new ErrorList(e.getMessage());
        }
    }

    @RequestMapping(value = "/republish/media", method = RequestMethod.GET)
    @ResponseBody
    public TransferList republishMedia(
        @RequestParam(value = "mediaType", required = false, defaultValue = "MEDIA") String typeParam,
        @RequestParam(value = "modifiedStart", required = false) String modifiedStart,
        @RequestParam(value = "modifiedStop", required = false) String modifiedStop,
        @RequestParam(value = "createdBy", required = false) String userParam,
        @RequestParam(value = "destination", required = false, defaultValue = "") String destination,
        @RequestParam(value = "channel", required = false, defaultValue = "") String channel,
        @RequestParam(value = "net", required = false, defaultValue = "") String net,
        @RequestParam(value = "mids", required = false, defaultValue = "") String mids,
        @RequestParam(value = "max", required = false, defaultValue = "") String maxStr,
        @RequestParam(value = "offset", required = false, defaultValue = "") String offsetStr
    ) {

        MediaPublisher.Destination[] destinations = MediaPublisher.Destination.arrayOf(destination);

        if(destinations.length == 0) {
            return new ErrorList("No destination given");
        }

        Collection<MediaType> types;
        try {
            types = MediaType.valuesOf(typeParam.toUpperCase());
        } catch(IllegalArgumentException e) {
            return new ErrorList("Not a valid media type: " + typeParam);
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");

        DateRange modifiedRange = null;
        if(StringUtils.isNotEmpty(modifiedStart) || StringUtils.isNotEmpty(modifiedStop)) {
            try {
                modifiedRange = new DateRange(DateUtils.toInstant(dayFormat.parse(modifiedStart)), DateUtils.toInstant(dayFormat.parse(modifiedStop)));
            } catch(ParseException e) {
                return new ErrorList("Error parsing modified range. Start: " + modifiedStart + " stop: " + modifiedStop);
            }
        }

        Editor user = null;
        if(StringUtils.isNotEmpty(userParam)) {
            user = userService.get(userParam);

            if(user == null) {
                return new ErrorList("Unknown user: " + userParam);
            }
        }

        List<Net> nets = null;
        if(StringUtils.isNotEmpty(net)) {
            nets = new ArrayList<>();
            for(String n : net.split(",")) {
                Net no = scheduleEventService.getNet(n);
                if(no == null) {
                    return new ErrorList("Could not find net " + n);
                }
                nets.add(no);
            }
        }

        RepublishMediaForm form = new RepublishMediaForm();
        form.setTypes(types);
        form.setLastPublishedRange(modifiedRange);
        if(user != null) {
            form.setCreatedBy(user);
        }
        if(StringUtils.isNotEmpty(channel)) {
            form.setChannels(Channel.valuesOf(Arrays.asList(channel.split(","))));
        }
        form.setNets(nets);
        if(StringUtils.isNotBlank(mids)) {
            form.setIds(Arrays.asList(mids.split("\\s*,\\s*")));
        }
        if(StringUtils.isNotBlank(offsetStr)) {
            try {
                Long offset = Long.valueOf(offsetStr);
                form.setOffset(offset);
            } catch(NumberFormatException e) {
                return new ErrorList("Error parsing offset query param: " + offsetStr);
            }
        }
        if(StringUtils.isNotBlank(maxStr) && !"Alle media".equals(maxStr)) {
            try {
                Integer max = Integer.valueOf(maxStr);
                form.setMax(max);
            } catch(NumberFormatException e) {
                return new ErrorList("Error parsing max query param: " + offsetStr);
            }
        }
        try {
            if(null != mediaRepublisher.republish(form, destinations)) {
                return new SuccessList("Republishing " + (form.hasIds() ? form.getIds() : (" type: " + types)) + " to " + Arrays.asList(destinations));
            }
        } catch(Exception e) {
            return new ErrorList(e.getMessage());
        }
        return new ErrorList("No publisher available");
    }

    @RequestMapping(value = "/republish/subtitles", method = RequestMethod.GET)
    @ResponseBody
    public TransferList republishSubtitles(
        @RequestParam(value = "modifiedStart", required = false) String modifiedStart,
        @RequestParam(value = "modifiedStop", required = false) String modifiedStop,
        @RequestParam(value = "destination", required = false, defaultValue = "") String destination) {

        MediaPublisher.Destination[] destinations = MediaPublisher.Destination.arrayOf(destination);

        if(destinations.length == 0) {
            return new ErrorList("No destination given");
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");

        DateRange modifiedRange = null;
        if(StringUtils.isNotEmpty(modifiedStart) || StringUtils.isNotEmpty(modifiedStop)) {
            try {
                modifiedRange = new DateRange(DateUtils.toInstant(dayFormat.parse(modifiedStart)), DateUtils.toInstant(dayFormat.parse(modifiedStop)));
            } catch(ParseException e) {
                return new ErrorList("Error parsing modified range. Start: " + modifiedStart + " stop: " + modifiedStop);
            }
        }

        if(null != subtitlesRepublisher.republish(modifiedRange, destination)) {
            return new SuccessList("Republishing subtitles");
        }

        return new ErrorList("No publisher available");
    }
}
