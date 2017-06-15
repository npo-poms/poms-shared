/**
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.spring.web.controllers.ceres;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import nl.vpro.camel.media.services.CeresImporter;
import nl.vpro.domain.media.ceres.metadata.Metadata;
import nl.vpro.domain.user.MediaEditorService;

/**
 * See https://jira.vpro.nl/browse/MSE-1992
 *
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@Controller
@RequestMapping(value = "/")
@Transactional
public class CeresController {
    private final MediaEditorService editorService;

    private final CeresImporter ceresImporter;

    @Autowired
    public CeresController(MediaEditorService editorService, CeresImporter ceresImporter) {
        this.editorService = editorService;
        this.ceresImporter = ceresImporter;
    }

    @RequestMapping(value = "/metadata/{mediaId}", method = RequestMethod.GET)
    public void sendMetadata(
        @PathVariable(value = "mediaId") final String id,
        @QueryParam(value = "version")  final String version,
        HttpServletResponse response
    ) throws IOException {
        editorService.authenticate("ceres-publisher");
        try {
            response.setContentType(MediaType.APPLICATION_XML_VALUE);
            response.setStatus(HttpServletResponse.SC_OK);
            Metadata.Version mversion = version == null ? Metadata.Version.V20 : Metadata.Version.valueOf("V" + version);
                ceresImporter.writeMetaData(id, response.getOutputStream(), mversion);
        } catch (CeresImporter.NotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No BROADCAST for MID: " + id);
        } catch (CeresImporter.NotABroadcastException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "MID: " + id + " is not a broadcast but a " + e.getActualType());
        } catch (IllegalArgumentException iae) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, iae.getMessage());
        } finally {
            editorService.dropAuthentication();
        }
    }


}
