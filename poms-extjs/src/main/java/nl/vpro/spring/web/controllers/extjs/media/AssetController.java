package nl.vpro.spring.web.controllers.extjs.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import nl.vpro.domain.encoder.GeoRestriction;
import nl.vpro.domain.encoder.Job;
import nl.vpro.domain.media.EncodeService;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.MediaService;
import nl.vpro.domain.user.EditorService;
import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.errors.ErrorList;
import nl.vpro.transfer.extjs.success.SuccessList;
import nl.vpro.transfer.extjs.upload.JobResultList;
import nl.vpro.ws.encoder.EncoderWebService;

/**
 * @author Ernst Bunders
 */
@Controller
@RequestMapping(value = "/assets")
public class AssetController {
    private static final Logger LOG = LoggerFactory.getLogger(AssetController.class);

    private final static String DATE_FORMAT = "dd/MM/yyyy HH:mm";

    @Autowired
    private EditorService userService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private EncodeService encodeService;

    @Resource(name = "encoder")
    private EncoderWebService encoder;

    @RequestMapping(value = "/asset/{filename}.{extension}", method = RequestMethod.GET)
    public void getAsset(@PathVariable("filename") String fileName,
                         @PathVariable("extension") String extension,
                         HttpServletResponse response) throws IOException {
        if(extension != null && extension.length() > 0) {
            fileName = fileName + "." + extension;
        }

        final File file = encodeService.get(fileName);
        if(file == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found: " + fileName);
            return;
        }

        response.addHeader("Content-Length", Long.toString(file.length())); // ServletResponse.setContentLength(i) only takes integer values, but we're talking big uploads here.

        InputStream in = null;
        final ServletOutputStream out = response.getOutputStream();
        try {
            in = new FileInputStream(file);
            IOUtils.copy(in, out);
            in.close();
            out.close();
        } catch(Exception e) {
            if(!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            }
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
    }

    @RequestMapping(value = "/asset/{filename}.{extension}", method = RequestMethod.DELETE)
    public TransferList<?> deleteAsset(@PathVariable("filename") String fileName, @PathVariable("extension") String extension, HttpServletResponse response) throws IOException {
        if(extension != null && extension.length() > 0) {
            fileName = fileName + "." + extension;
        }

        boolean success = encodeService.remove(fileName);
        if(!success) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found: " + fileName);
        }

        return new SuccessList("Deleted " + fileName);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public TransferList<?> handleUpload(
        @RequestParam(value = "id", required = true) String id,
        @RequestParam(value = "name", required = true) String name,
        @RequestParam(value = "mediaId", required = true) String mediaId,
        @RequestParam(value = "file", required = true) MultipartFile multipartFile,
        @RequestParam(value = "chunk", required = false, defaultValue = "-1") int chunk,
        @RequestParam(value = "chunks", required = false, defaultValue = "-1") int chunks,
        @RequestParam(value = "publishStart", required = false) String publishStartString,
        @RequestParam(value = "publishStop", required = false) String publishStopString,
        @RequestParam(value = "georestriction", required = false) String georestriction,
        HttpServletResponse response) throws IOException {
        Date publishStart = parse(publishStartString);
        Date publishStop = parse(publishStopString);

        MediaObject media;
        try {
            media = mediaService.get(Long.parseLong(mediaId));
        } catch(NumberFormatException nfe) {
            if(mediaId.startsWith("urn:")) {
                media = mediaService.findByUrn(mediaId);
            } else {
                media = mediaService.findByMid(mediaId);
            }
        }
        if(media == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No media found for id: " + mediaId);
            return null;
        }
        String mid = media.getMid();
        String fileName = id + "_" + name.replaceAll("[^a-zA-Z0-9\\.]+", "_") + ".asset";
        InputStream inputStream = multipartFile.getInputStream();

        storeData(fileName, inputStream, chunk, chunks);
        GeoRestriction geoRestriction = StringUtils.isNotBlank(georestriction) ? GeoRestriction.valueOf(georestriction.toUpperCase()) : null;

        if(chunk == -1 || chunk == chunks - 1) { // chunk index starts at 0
            encodeService.encode(media, fileName, publishStart, publishStop);
            return new SuccessList("Uploaded last data chunk (of " + chunks + ")  and started encoding for " + (mid == null ? media.getUrn() : mid));
        } else {
            return new SuccessList("Uploaded data chunk " + (chunk + 1) + "/" + chunks + " for " + (mid == null ? media.getUrn() : mid));
        }
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    @ResponseBody
    public TransferList<?> jobs() {
        try {
            final List<Job> jobs;
            if(userService.currentUserHasRole("ROLE_MEDIA_SUPERUSER", "ROLE_MEDIA_SUPERADMIN")) {
                jobs = encoder.findAll();
            } else if(userService.currentUserHasRole("ROLE_MEDIA_ADMIN")) {
                jobs = encoder.findForBroadcaster(userService.currentEmployer().getId());
            } else if(userService.currentUserHasRole("ROLE_MEDIA_UPLOAD")) {
                jobs = encoder.findForUser(userService.currentUser().getPrincipalId());
            } else {
                throw new IllegalStateException("You dont' have the correct permissions");
            }
            return JobResultList.create(jobs);
        } catch(Exception e) {
            String message = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            LOG.warn("Error calling encoder service. Root cause: " + message);
            return new ErrorList(message);
        }

    }

    private String storeData(final String fileName, final InputStream inputStream, final int chunk, final int chunks) {
        if(chunk <= 0) {
            // Unchunked file or first chunk
            return encodeService.store(fileName, inputStream);
        } else if(chunk > 0 && chunk < chunks) {
            // Next chunk
            return encodeService.append(fileName, inputStream);
        }
        return null;
    }

    private Date parse(final String dateString) {
        if(StringUtils.isNotBlank(dateString)) {
            try {
                return new SimpleDateFormat(DATE_FORMAT).parse(dateString);
            } catch(ParseException e) {
                // Do nothing
            }
        }
        return null;
    }
}
