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

import nl.vpro.domain.image.ImageType;

/**
 * 'Run as' Wrapper for ImageWebService
 * @author Danny Sedney
 *
 */
@WebService(
	    name = "runAsImageWebService",
	    portName = "runAsImageServicePort",
	    targetNamespace = "urn:vpro:ws:image:2009")
public interface RunAsImageWebService {
	// all methods get @WebParam(name = "principalId", targetNamespace = "") String principalId,

	/**
	 * upload
     * @param principalId
	 * @param title
	 * @param description
	 * @param type
	 * @param data
	 * @return String
	 */
    @WebMethod
    @WebResult(
        name = "urn",
        targetNamespace = "")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.UploadAsRequest")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    String upload(
    	@WebParam(name = "principalId", targetNamespace = "") String principalId,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "data", targetNamespace = "") DataHandler data);

    /**
     * uploadAsync
     * @param principalId
     * @param title
     * @param description
     * @param type
     * @param data
     * @param handler Future<?>
     * @return
     */
    @WebMethod(operationName = "upload")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.UploadAsRequest")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    Future<?> uploadAsync(
    	@WebParam(name = "principalId", targetNamespace = "") String principalId,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "data", targetNamespace = "") DataHandler data,
        AsyncHandler<UploadResponse> handler);

    /**
     * uploadAsync
     * @param principalId
     * @param title
     * @param description
     * @param type
     * @param data
     * @return UploadResponse
     */
    @WebMethod(operationName = "upload")
    @RequestWrapper(
        localName = "upload",
        className = "nl.vpro.ws.image.UploadAsRequest")
    @ResponseWrapper(
        localName = "uploadResponse",
        className = "nl.vpro.ws.image.UploadResponse")
    Response<UploadResponse> uploadAsync(
    	@WebParam(name = "principalId", targetNamespace = "") String principalId,
        @WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "data", targetNamespace = "") DataHandler data);

    /**
     * download
     * @param principalId
     * @param title
     * @param description
     * @param type
     * @param url
     * @return String result
     * @throws DownloadFault
     */
    @WebMethod
    @WebResult(
        name = "urn",
        targetNamespace = "")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.DownloadAsRequest")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    String download(
    	@WebParam(name = "principalId", targetNamespace = "") String principalId,
    	@WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "url", targetNamespace = "") String url) throws DownloadFault;

    /**
     * downloadAsync
     * @param principalId
     * @param title
     * @param description
     * @param type
     * @param url
     * @param handler
     * @return Future<?>
     */
    @WebMethod(operationName = "download")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.DownloadAsRequest")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    Future<?> downloadAsync(
    	@WebParam(name = "principalId", targetNamespace = "") String principalId,
    	@WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "url", targetNamespace = "") String url,
        AsyncHandler<DownloadResponse> handler);

    /**
     * downloadAsync
     * @param principalId
     * @param title
     * @param description
     * @param type
     * @param url
     * @return DownloadResponse
     */
    @WebMethod(operationName = "download")
    @RequestWrapper(
        localName = "download",
        className = "nl.vpro.ws.image.DownloadAsRequest")
    @ResponseWrapper(
        localName = "downloadResponse",
        className = "nl.vpro.ws.image.DownloadResponse")
    Response<DownloadResponse> downloadAsync(
    	@WebParam(name = "principalId", targetNamespace = "") String principalId,
    	@WebParam(name = "title", targetNamespace = "") String title,
        @WebParam(name = "description", targetNamespace = "") String description,
        @WebParam(name = "type", targetNamespace = "") ImageType type,
        @WebParam(name = "url", targetNamespace = "") String url);


}
