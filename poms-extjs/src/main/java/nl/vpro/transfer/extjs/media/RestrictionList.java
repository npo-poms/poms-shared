/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.Restriction;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "restrictions")
public class RestrictionList extends TransferList<RestrictionView> {

    public RestrictionList() {
    }

    public RestrictionList(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static <T extends Restriction> RestrictionList create(List<T> restrictions) {
        RestrictionList list = new RestrictionList();

        for(T restriction : restrictions) {
            list.add(RestrictionView.create(restriction));
        }

        list.success = true;

        return list;
    }
}