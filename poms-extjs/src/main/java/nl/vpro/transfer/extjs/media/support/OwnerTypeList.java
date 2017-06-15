/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.AVType;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "owners")
public class OwnerTypeList extends TransferList<OwnerTypeView> {

    private OwnerTypeList() {
    }

    public static OwnerTypeList create() {
        OwnerTypeList typeList = new OwnerTypeList();

        for(OwnerType type : OwnerType.values()) {
            typeList.add(OwnerTypeView.create(type));
        }

        typeList.success = true;

        return typeList;
    }
}