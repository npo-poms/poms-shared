/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import java.util.concurrent.Future;

import javax.activation.DataHandler;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.Response;
import javax.xml.ws.ResponseWrapper;

import nl.vpro.domain.image.BasicImageMetadata;
import nl.vpro.domain.image.ImageType;

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
    BasicImageMetadata upload(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "data", targetNamespace = "") DataHandler data);

    @WebMethod(operationName = "upload")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.UploadRequest")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    Future<?> uploadAsync(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
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
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
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
    BasicImageMetadata download(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "url", targetNamespace = "") String url,
        @WebParam(name = "imageMetadata", targetNamespace = "") Boolean imageMetdata
    ) throws DownloadFault;

    @WebMethod(operationName = "download")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.DownloadRequest")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    Future<?> downloadAsync(
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
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
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "url", targetNamespace = "") String url);
}
