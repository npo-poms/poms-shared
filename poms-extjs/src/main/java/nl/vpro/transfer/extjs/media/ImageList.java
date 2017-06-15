/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.SortedSet;

import nl.vpro.domain.media.Location;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.support.Image;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "images")
public class ImageList extends TransferList<ImageView> {

    public ImageList() {
    }

    public ImageList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ImageList create(MediaObject media) {
        List<Image> fullList = media.getImages();

        ImageList simpleList = new ImageList();
        simpleList.success = true;

        if(fullList == null) {
            return simpleList;
        }

        for(int i = 0; i < fullList.size(); i++) {
            Image image = fullList.get(i);
            if(image != null) {
                simpleList.add(ImageView.create(image, i));
            }
        }

        return simpleList;
    }
}