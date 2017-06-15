/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs;

import java.util.List;

public class SuggestList extends TransferList<SuggestView> {

    private SuggestList() {
    }

    public static SuggestList create(List<String> fullList) {
        SuggestList simpleList = new SuggestList();

        simpleList.success = true; 
        simpleList.results = fullList.size();

        for(String title : fullList) {
            simpleList.list.add(SuggestView.create(title));
        }
        return simpleList;
    }
}
