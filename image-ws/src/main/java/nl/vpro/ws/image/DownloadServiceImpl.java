/**
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.ws.image;

import javax.annotation.Resource;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;
import java.util.concurrent.ExecutionException;

//import org.springframework.stereotype.Service;

//@Service("downloadService")
public class DownloadServiceImpl implements DownloadService {

    @Resource(name = "imageServiceWSClient")
    ImageServiceWs imageServiceWs;

    public void download(String url) {
        String urn = "inUrl";

//        imageServiceWs.downloadAsync(urn, new AsyncHandler<DownloadResponse>() {
//            public void handleResponse(Response<DownloadResponse> response) {
//                try {
//                    System.out.println(response.get());
//                } catch(InterruptedException e) {
//                    e.printStackTrace();
//                } catch(ExecutionException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
}
