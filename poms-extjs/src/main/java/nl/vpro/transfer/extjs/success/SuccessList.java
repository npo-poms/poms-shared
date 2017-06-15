/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.success;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.transfer.extjs.TransferList;
import nl.vpro.transfer.extjs.errors.ErrorView;

@XmlRootElement(name = "success")
public class SuccessList extends TransferList<SuccessView> {

    public SuccessList(String message) {
        this.success = true;
        this.results = 0;
        this.message = message;
    }
}