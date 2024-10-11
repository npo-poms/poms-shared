/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.images.ws;

import java.util.concurrent.Future;

import jakarta.activation.DataHandler;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.ws.AsyncHandler;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.Response;
import jakarta.xml.ws.ResponseWrapper;

import nl.vpro.domain.image.backend.BasicBackendImageMetadata;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.support.OwnerType;

import static nl.vpro.domain.Xmlns.IMAGE_WS_NAMESPACE;

@WebService(
    name = "imageWebService",
    portName = "imageServicePort",
    targetNamespace = IMAGE_WS_NAMESPACE)
public interface ImageWebService {

    @WebMethod
    @WebResult(
        name = "imageMetadata",
        targetNamespace = "")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.UploadRequest")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    BasicBackendImageMetadata upload(
        @WebParam(name = "owner", targetNamespace = "") OwnerType owner,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "imageMetadata", targetNamespace = "") Boolean imageMetadata,
        @WebParam(name = "data", targetNamespace = "") DataHandler data
    );

    @WebMethod(operationName = "upload")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.UploadRequest")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    Future<?> uploadAsync(
        @WebParam(name = "owner", targetNamespace = "") OwnerType owner,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "imageMetadata", targetNamespace = "") Boolean imageMetadata,
        @WebParam(name = "data", targetNamespace = "") DataHandler data,
        AsyncHandler<UploadResponse> handler);

    @WebMethod(operationName = "upload")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.UploadRequest")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    Response<UploadResponse> uploadAsync(
        @WebParam(name = "owner", targetNamespace = "") OwnerType owner,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "imageMetadata", targetNamespace = "") Boolean imageMetadata,
        @WebParam(name = "data", targetNamespace = "") DataHandler data);

    @WebMethod
    @WebResult(
        name = "imageMetadata",
        targetNamespace = "")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.DownloadRequest")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    BasicBackendImageMetadata download(
        @WebParam(name = "owner", targetNamespace = "") OwnerType owner,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "imageMetadata", targetNamespace = "") Boolean imageMetadata,
        @WebParam(name = "url", targetNamespace = "") String url
    ) throws DownloadFault;

    @WebMethod(operationName = "download")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.DownloadRequest")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    Future<?> downloadAsync(
        @WebParam(name = "owner", targetNamespace = "") OwnerType owner,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "imageMetadata", targetNamespace = "") Boolean imageMetadata,
        @WebParam(name = "url", targetNamespace = "") String url,
        AsyncHandler<DownloadResponse> handler);

    @WebMethod(operationName = "download")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.DownloadRequest")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    Response<DownloadResponse> downloadAsync(
        @WebParam(name = "owner", targetNamespace = "") OwnerType owner,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "imageMetadata", targetNamespace = "") Boolean imageMetadata,
        @WebParam(name = "url", targetNamespace = "") String url
        );
}
