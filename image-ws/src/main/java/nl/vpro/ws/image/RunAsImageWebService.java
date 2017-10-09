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

/**
 * 'Run as' Wrapper for ImageWebService
 *
 * @author Danny Sedney
 *
 */
@WebService(name = "runAsImageWebService", portName = "runAsImageServicePort", targetNamespace = IMAGE_WS_NAMESPACE)
public interface RunAsImageWebService {
    // all methods get @WebParam(name = "principalId", targetNamespace = "") String
    // principalId,

    @WebMethod
    @WebResult(name = "imageMetadata", targetNamespace = "")
    @RequestWrapper(localName = "upload", className = "nl.vpro.ws.image.UploadAsRequest")
    @ResponseWrapper(localName = "uploadResponse", className = "nl.vpro.ws.image.UploadResponse")
    BasicImageMetadata upload(
        @WebParam(name = "principalId", targetNamespace = "") String principalId,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "data", targetNamespace = "") DataHandler data,
        @WebParam(name = "imageMetadata", targetNamespace = "") Boolean imageMetadata

    );

    @WebMethod(operationName = "upload")
    @RequestWrapper(localName = "upload", className = "nl.vpro.ws.image.UploadAsRequest")
    @ResponseWrapper(localName = "uploadResponse", className = "nl.vpro.ws.image.UploadResponse")
    Future<?> uploadAsync(@WebParam(name = "principalId", targetNamespace = "") String principalId,
            @WebParam(name = "title", targetNamespace = "") String title,
            @WebParam(name = "description", targetNamespace = "") String description,
            @WebParam(name = "type", targetNamespace = "") ImageType type,
            @WebParam(name = "data", targetNamespace = "") DataHandler data, AsyncHandler<UploadResponse> handler);

    @WebMethod(operationName = "upload")
    @RequestWrapper(localName = "upload", className = "nl.vpro.ws.image.UploadAsRequest")
    @ResponseWrapper(localName = "uploadResponse", className = "nl.vpro.ws.image.UploadResponse")
    Response<UploadResponse> uploadAsync(@WebParam(name = "principalId", targetNamespace = "") String principalId,
            @WebParam(name = "title", targetNamespace = "") String title,
            @WebParam(name = "description", targetNamespace = "") String description,
            @WebParam(name = "type", targetNamespace = "") ImageType type,
            @WebParam(name = "data", targetNamespace = "") DataHandler data);

    @WebMethod
    @WebResult(name = "imageMetadata", targetNamespace = "")
    @RequestWrapper(localName = "download", className = "nl.vpro.ws.image.DownloadAsRequest")
    @ResponseWrapper(localName = "downloadResponse", className = "nl.vpro.ws.image.DownloadResponse")
    BasicImageMetadata download(
        @WebParam(name = "principalId", targetNamespace = "") String principalId,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "url", targetNamespace = "") String url,
        @WebParam(name = "imageMetadata", targetNamespace = "") Boolean imageMetdata
    ) throws DownloadFault;

    @WebMethod(operationName = "download")
    @RequestWrapper(localName = "download", className = "nl.vpro.ws.image.DownloadAsRequest")
    @ResponseWrapper(localName = "downloadResponse", className = "nl.vpro.ws.image.DownloadResponse")
    Future<?> downloadAsync(@WebParam(name = "principalId", targetNamespace = "") String principalId,
            @WebParam(name = "title", targetNamespace = "") String title,
            @WebParam(name = "description", targetNamespace = "") String description,
            @WebParam(name = "type", targetNamespace = "") ImageType type,
            @WebParam(name = "url", targetNamespace = "") String url, AsyncHandler<DownloadResponse> handler);

    @WebMethod(operationName = "download")
    @RequestWrapper(localName = "download", className = "nl.vpro.ws.image.DownloadAsRequest")
    @ResponseWrapper(localName = "downloadResponse", className = "nl.vpro.ws.image.DownloadResponse")
    Response<DownloadResponse> downloadAsync(@WebParam(name = "principalId", targetNamespace = "") String principalId,
            @WebParam(name = "title", targetNamespace = "") String title,
            @WebParam(name = "description", targetNamespace = "") String description,
            @WebParam(name = "type", targetNamespace = "") ImageType type,
            @WebParam(name = "url", targetNamespace = "") String url);

}
