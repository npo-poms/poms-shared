/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.AVType;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "avTypes")
public class AVTypeList extends TransferList<AVTypeView> {

    private AVTypeList() {
    }

    public static AVTypeList create() {
        AVTypeList typeList = new AVTypeList();

        for(AVType type : AVType.values()) {
            typeList.add(AVTypeView.create(type));
        }

        typeList.success = true;

        return typeList;
    }
}