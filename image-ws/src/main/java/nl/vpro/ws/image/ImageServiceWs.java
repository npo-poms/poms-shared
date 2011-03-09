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
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.Response;
import javax.xml.ws.ResponseWrapper;
import java.util.concurrent.Future;

@WebService(
//        serviceName = "ImageService",
    portName = "ImageServicePort",
    targetNamespace = "urn:vpro:ws:image:2009")
public interface ImageServiceWs {

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
        @WebParam(name = "url", targetNamespace = "") String url);

    @WebMethod(operationName = "download")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.Download")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    Future<?> downloadAsync(
        @WebParam(name = "url", targetNamespace = "") String url,
        /*@WebParam(name = "handler", partName = "parameters")*/ AsyncHandler<DownloadResponse> handler);

    @WebMethod(operationName = "download")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.Download")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    Response<DownloadResponse> downloadAsync(
        @WebParam(name = "url", targetNamespace = "") String url);
}
