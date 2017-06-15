/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.admin;

import java.util.HashMap;
import java.util.Map;

import nl.vpro.transfer.extjs.TransferList;

public class IndexResultList extends TransferList<IndexResult> {

    public IndexResultList() {
        this.success = true;
        this.results = 0;
    }

    public IndexResultList addResult(String index, Integer result) {
        if(result > 0) {
            this.list.add(new IndexResult(index, result));
            this.results = this.results + 1;
        }
        return this;
    }

}
