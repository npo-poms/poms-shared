/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.media.AVFileFormat;
import nl.vpro.transfer.extjs.TransferList;

@XmlRootElement(name = "formats")
public class FormatsList extends TransferList<FormatsView> {

    private FormatsList() {
    }

    public static FormatsList create() {
        FormatsList formatsList = new FormatsList();

        for(AVFileFormat format : AVFileFormat.values()) {
            formatsList.add(FormatsView.create(format));
        }

        formatsList.success = true;
        formatsList.results = AVFileFormat.values().length;

        return formatsList;
    }
}
