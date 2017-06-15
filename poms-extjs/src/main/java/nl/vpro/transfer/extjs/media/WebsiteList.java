/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import nl.vpro.domain.media.Website;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "websites")
public class WebsiteList extends TransferList<WebsiteView>{

    public WebsiteList() {
    }

    public WebsiteList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static WebsiteList create(List<Website> fullList) {
        WebsiteList list = new WebsiteList();

        int index = 0;
        for(Website website : fullList) {
            if(website != null) {
                list.add(WebsiteView.create(website, index));
                index++;
            }
        }

        list.success = true;

        return list;
    }
}