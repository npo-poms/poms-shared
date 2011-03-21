/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.activation.DataHandler;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.io.IOException;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nl.vpro.domain.image.Image;
import nl.vpro.domain.image.ImageService;
import nl.vpro.domain.image.UnsupportedImageFormatException;
import nl.vpro.domain.image.cache.NotFoundException;

@Service("imageServiceWs")
public class ImageServiceWsImpl implements ImageServiceWs {

    @Autowired
    ImageService imageService;

    public String upload(String title, String description, DataHandler data) {
        Image image = new Image(title);
        image.setDescription(description);

        try {
            image = imageService.setData(image, data.getInputStream());
        } catch(IOException e) {
            e.printStackTrace();
        } catch(UnsupportedImageFormatException e) {
            e.printStackTrace();
        } catch(NotFoundException e) {
            e.printStackTrace();
        }

        return image.getUrn();
    }

    public Future<?> uploadAsync(String title, String description, DataHandler data, AsyncHandler<UploadResponse> handler) {
        return null;
    }

    public Response<UploadResponse> uploadAsync(String title, String description, DataHandler data) {
        return null;
    }

    public String download(String url) {
        return "Incoming url: " + url;
    }

    public Future<?> downloadAsync(String url, AsyncHandler<DownloadResponse> handler) {
        return null;
    }

    public Response<DownloadResponse> downloadAsync(String url) {
        return null;
    }
}
