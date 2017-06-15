/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.Net;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "nets")
public class NetList extends TransferList<NetView> {

    public NetList() {
    }

    public NetList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static NetList create(List<Net> searchResult) {
        NetList simpleList = new NetList();
        simpleList.success = true;

        for(Net net : searchResult) {
            simpleList.add(new NetView(net));
        }

        simpleList.results = searchResult.size();
        return simpleList;
    }


}
