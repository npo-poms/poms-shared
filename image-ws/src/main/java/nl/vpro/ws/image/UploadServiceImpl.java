/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.annotation.Resource;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

@Service("uploadService")
public class UploadServiceImpl implements UploadService {

    @Resource(name = "imageServiceWSClient")
    ImageServiceWs imageServiceWs;

    public void upload(Upload upload) {

        imageServiceWs.uploadAsync(
            upload.getTitle(),
            upload.getDescription(),
            upload.getData(), new AsyncHandler<UploadResponse>() {
                public void handleResponse(Response<UploadResponse> response) {
                    try {
                        System.out.println(response.get());
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    } catch(ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            });
    }
}
