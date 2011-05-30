/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.*;
import javax.xml.ws.soap.SOAPBinding;
import java.util.concurrent.Future;

import nl.vpro.domain.image.ImageType;

@WebService(
    name = "imageService",
    portName = "imageServicePort",
    targetNamespace = "urn:vpro:ws:image:2009")
public interface ImageWebService {

    @WebMethod
    @WebResult(
        name = "urn",
        targetNamespace = "")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.Upload")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    String upload(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "data", targetNamespace = "") DataHandler data);

    @WebMethod(operationName = "upload")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.Upload")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    Future<?> uploadAsync(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "data", targetNamespace = "") DataHandler data,
        AsyncHandler<UploadResponse> handler);

    @WebMethod(operationName = "upload")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.Upload")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    Response<UploadResponse> uploadAsync(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "data", targetNamespace = "") DataHandler data);

    @WebMethod
    @WebResult(
        name = "urn",
        targetNamespace = "")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.Download")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    String download(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "url", targetNamespace = "") String url,
        @WebParam(name = "principalId", targetNamespace = "") String principalId) throws DownloadFault;

    @WebMethod(operationName = "download")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.Download")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    Future<?> downloadAsync(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "url", targetNamespace = "") String url,
        @WebParam(name = "principalId", targetNamespace = "") String principalId,
        AsyncHandler<DownloadResponse> handler);

    @WebMethod(operationName = "download")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.Download")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    Response<DownloadResponse> downloadAsync(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "url", targetNamespace = "") String url,
        @WebParam(name = "principalId", targetNamespace = "") String principalId);
}
